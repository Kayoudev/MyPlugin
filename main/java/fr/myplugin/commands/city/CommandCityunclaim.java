package fr.myplugin.commands.city;

import fr.myplugin.data.Database;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandCityunclaim implements CommandExecutor {

    private final Database database;

    public CommandCityunclaim(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cSeul un joueur peut utiliser cette commande.");
            return true;
        }

        String playerUuid = player.getUniqueId().toString();

        int cityId = database.getCityIdByMayorOrDeputy(playerUuid);
        if(cityId == -1) {
            player.sendMessage("§cTu dois être maire ou adjoint pour unclaim un chunk.");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        String world = player.getWorld().getName();

        if(!database.isChunkClaimedByCity(cityId,world,chunkX,chunkZ)) {
            player.sendMessage("§cCe chunk n'appartient pas à ta ville.");
            return true;
        }

        boolean success = database.claimChunk(cityId,world,chunkX,chunkZ);

        if(success) {
            player.sendMessage("§aChunk unclaim avec succès.");
        } else {
            player.sendMessage("§cUne erreur est survenue pendant l’unclaim.");
        }

        return true;

    }
}
