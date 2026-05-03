package fr.myplugin.commands.city;

import fr.myplugin.data.Database;
import fr.myplugin.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class CommandCityedit implements CommandExecutor {

    private final Database database;

    public CommandCityedit(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        Player player = (Player) commandSender;
        String uuid = player.getUniqueId().toString();

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cSeul un joueur peut exécuter cette commande.");
            return true;
        }

        if(!database.isMayor(uuid) && !database.isDeputy(uuid)) {
            player.sendMessage("§cVous devez être maire pour utiliser cette commande.");
            return true;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "City Edit");

        //Nom de la ville
        String cityName = database.getCityName(uuid);

        inventory.setItem(10, ItemUtils.getItem(Material.NAME_TAG, 1, "Nom de la ville :", cityName, "Clique sur le tag pour modifier le nom de ta ville."));

        //description de la ville
        String cityDescription = database.getCityDescription(uuid);

        inventory.setItem(11, ItemUtils.getItem(Material.BOOK, 1, "Description de la ville :", cityDescription, "Clique sur le livre pour modifier la description de ta ville."));

        //maire de la ville
        String cityMayor = String.valueOf(database.getCityMayor(uuid));

        inventory.setItem(15, ItemUtils.getItem(Material.GOLDEN_CHESTPLATE, 1, "Maire de la ville :", cityMayor));

        //adjoint de la ville
        String cityDeputy = database.getCityDeputy(uuid);

        if(cityDeputy == null) {
            cityDeputy = "Vous n'avez pas nommé d'adjoint.";
        }

        inventory.setItem(16, ItemUtils.getItem(Material.IRON_CHESTPLATE, 1, "Adjoint de la ville :", cityDeputy, "Clique sur l'armure pour modifier l'adjoint' de ta ville."));

        //nombre de citoyens
        String citizensCount = String.valueOf(database.getCitizenCount(uuid));
        int citizensNumber = Math.max(1,database.getCitizenCount(uuid));

        inventory.setItem(29, ItemUtils.getItem(Material.PLAYER_HEAD, citizensNumber, "Nombre de citoyens :", citizensCount));

        //ajouter un citoyen
        inventory.setItem(30, ItemUtils.getItem(Material.LIME_WOOL, 1, "Ajouter un citoyen", "Clique sur le bloc vert pour inviter un joueur dans ta ville."));

        //retirer un citoyen
        inventory.setItem(31, ItemUtils.getItem(Material.RED_WOOL, 1, "Exclure un citoyen", "Clique sur le bloc rouge pour exclure un joueur de ta ville."));


        //nombre de chunks
        String chunkCount = String.valueOf(database.getChunkCount(uuid));
        int chunksNumber = Math.max(1,database.getChunkCount(uuid));

        inventory.setItem(33, ItemUtils.getItem(Material.GRASS_BLOCK, chunksNumber, "Nombre de chunks claim :", chunkCount));

        inventory.setItem(45, ItemUtils.getItem(Material.LIGHT_GRAY_WOOL, 1, "Annuler", "Clique sur le bloc pour sortir du menu."));

        //supprimer la ville
        inventory.setItem(53, ItemUtils.getItem(Material.TNT, 1, "Supprimer la ville", "Clique sur le bloc pour supprimer ta ville."));

        player.openInventory(inventory);

        return false;
    }
}

