package fr.myplugin.data;

import java.io.File;
import java.sql.*;

public class Database {

    private final File dbFile;
    private Connection connection;

    public Database(File pluginFolder) {
        this.dbFile = new File(pluginFolder, "data.db");
    }

    // Connexion + création des tables
    public void connect() {
        try {
            dbFile.getParentFile().mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            Statement stmt = connection.createStatement();

            String createCitiesTable = """
                CREATE TABLE IF NOT EXISTS cities (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    description TEXT,
                    mayor_uuid TEXT NOT NULL,
                    deputy_uuid TEXT,
                    chunks INTEGER DEFAULT 0
                );
            """;

            String createCitizensTable = """
                CREATE TABLE IF NOT EXISTS citizens (
                    city_id INTEGER,
                    player_uuid TEXT,
                    PRIMARY KEY (city_id, player_uuid),
                    FOREIGN KEY (city_id) REFERENCES cities(id)
                );
            """;

            String createClaimsTable = """
                CREATE TABLE IF NOT EXISTS city_chunks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    city_id INTEGER,
                    world TEXT,
                    chunk_x INTEGER,
                    chunk_z INTEGER,
                    UNIQUE(city_id, world, chunk_x, chunk_z)
                );
            """;

            stmt.executeUpdate(createCitiesTable);
            stmt.executeUpdate(createCitizensTable);
            stmt.executeUpdate(createClaimsTable);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fermer la connexion
    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Création de ville
    public boolean createCity(String name, String description, String mayorUuid) {
        String query = "INSERT INTO cities (name, description, mayor_uuid) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, mayorUuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Vérifie si un joueur est maire
    public boolean isMayor(String uuid) {
        String query = "SELECT 1 FROM cities WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //vérifié si le joueur est adjoint
    public boolean isDeputy(String uuid) {
        String query = "SELECT * FROM cities WHERE deputy_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Récupère le nom de la ville du maire
    public String getCityName(String mayorUuid) {
        String query = "SELECT name FROM cities WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Inconnue";
    }

    // Récupère la description de la ville du maire
    public String getCityDescription(String mayorUuid) {
        String query = "SELECT description FROM cities WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucune description.";
    }

    // Récupère l'UUID du maire
    public String getCityMayor(String mayorUuid) {
        String query = "SELECT mayor_uuid FROM cities WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("mayor_uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "undefined";
    }

    // Récupère l'adjoint (UUID) à partir du maire
    public String getCityDeputy(String mayorUuid) {
        String query = "SELECT deputy_uuid FROM cities WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("deputy_uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "undefined";
    }

    // Récupère le nombre de citoyens à partir du maire
    public int getCitizenCount(String mayorUuid) {
        String query = """
            SELECT COUNT(*) AS total
            FROM cities
            JOIN citizens ON citizens.city_id = cities.id
            WHERE cities.mayor_uuid = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Récupère le nombre de chunks à partir du maire
    public int getChunkCount(String mayorUuid) {
        String query = "SELECT chunks FROM cities WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("chunks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //récupère l'id de la ville à partir du maire
    public int getCityIdByMayor(String mayorUuid) {
        String query = "SELECT id FROM cities WHERE mayor_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mayorUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //récupérer l'id de la ville à partir du maire ou de l'adjoint
    public int getCityIdByMayorOrDeputy(String playerUuid) {
        String query = "SELECT id FROM cities WHERE mayor_uuid = ? OR  deputy_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerUuid);
            stmt.setString(2, playerUuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //vérifier qu'un chunk est pas claim
    public boolean isChunkAlreadyClaimed(String world, int x, int z) {
        String query = "SELECT 1 FROM city_chunks WHERE world = ? AND chunk_x = ? AND chunk_z = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, z);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    //vérifier le nombre de claim possibles
    public int getClaimedChunksCount(int cityId) {
        String query = "SELECT COUNT(*) AS total FROM city_chunks WHERE city_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //obtenir l'id d'une ville par le chunk
    public int getCityIdByChunk(String world, int x, int z) {
        String query = "SELECT city_id FROM city_chunks WHERE world = ? AND chunk_x = ? AND chunk_z = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, z);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("city_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //savoir si une personne est citoyen par l'id de la ville
    public boolean isCitizen(String uuid, int cityId) {
        String query = "SELECT 1 FROM citizens WHERE player_uuid = ? AND city_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setInt(2, cityId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //savoir si le chunk est bien claim par x ville
    public boolean isChunkClaimedByCity(int cityId, String world, int chunkX, int chunkZ) {
        String sql = "SELECT 1 FROM city_chunks WHERE city_id = ? AND world = ? AND chunk_x = ? AND chunk_z = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cityId);
            stmt.setString(2, world);
            stmt.setInt(3, chunkX);
            stmt.setInt(4, chunkZ);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public int getCityIdByName(String name) {
        String query = "SELECT id FROM cities WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getCityMayorById(int cityId) {
        String query = "SELECT mayor_uuid FROM cities WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("mayor_uuid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCityDeputyById(int cityId) {
        String query = "SELECT deputy_uuid FROM cities WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("deputy_uuid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCityDescriptionById(int cityId) {
        String query = "SELECT description FROM cities WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("description");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Aucune description.";
    }

    public int getCitizenCountByCityId(int cityId) {
        String query = "SELECT COUNT(*) AS total FROM citizens WHERE city_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }




    //UPDATE

    //nom
    public boolean updateCityName(String mayorUuid, String newName) {
        String query = "UPDATE cities SET name = ? WHERE mayor_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setString(2, mayorUuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //description
    public boolean updateCityDescription(String uuid, String newDescription) {
        String query = "UPDATE cities SET description = ? WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newDescription);
            stmt.setString(2, uuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //adjoint
    public boolean setCityDeputy(String mayorUuid, String deputyUuid) {
        String query = "UPDATE cities SET deputy_uuid = ? WHERE mayor_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, deputyUuid);
            stmt.setString(2, mayorUuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //entrée dans la ville de nvx citoyens
    public boolean addCitizen(int cityId, String uuid) {
        String query = "INSERT OR IGNORE INTO citizens (city_id, player_uuid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            stmt.setString(2, uuid);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //retirer un citoyen de la ville

    public boolean removeCitizen(int cityId, String playerUUID) {
        String query = "DELETE FROM citizens WHERE city_id = ? AND player_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            stmt.setString(2, playerUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //supprimer la ville
    public boolean deleteCity(String mayorUUID) {
        try {
            // Supprime la ville liée au maire
            String query = "DELETE FROM cities WHERE mayor_uuid = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, mayorUUID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //claim un chunk
    public boolean claimChunk(int cityId, String world, int x, int z) {
        String query = "INSERT INTO city_chunks (city_id, world, chunk_x, chunk_z) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            stmt.setString(2, world);
            stmt.setInt(3, x);
            stmt.setInt(4, z);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //unclaim un chunk
    public boolean unclaimChunk(int cityId, String world, int chunkX, int chunkZ) {
        String sql = "DELETE FROM city_chunks WHERE city_id = ? AND world = ? AND chunk_x = ? AND chunk_z = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cityId);
            stmt.setString(2, world);
            stmt.setInt(3, chunkX);
            stmt.setInt(4, chunkZ);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
