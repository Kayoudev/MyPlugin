package fr.myplugin.commands.city;

import fr.myplugin.data.Database;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandCityclaim implements CommandExecutor {

    private final Database database;

    public CommandCityclaim(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }

        String playerUuid = player.getUniqueId().toString();
        int cityId = database.getCityIdByMayorOrDeputy(playerUuid);
        int chunkCount = database.getChunkCount(playerUuid);

        if (cityId == -1) {
            player.sendMessage("§cTu dois être maire ou adjoint d'une ville pour claim un chunk.");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        String world  = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        if(database.isChunkAlreadyClaimed(world,x,z)) {
            player.sendMessage("§cCe chunk est déjà claim par une autre ville.");
            return true;
        }

        if (chunkCount >= 20) {
            player.sendMessage("§cTa ville a atteint la limite de 20 chunks claim.");
            return true;
        }

        boolean success = database.claimChunk(cityId,world,x,z);
        if(success) {
            player.sendMessage("§aChunk claim avec succès !");
        } else {
            player.sendMessage("§cErreur lors du claim.");
        }

        return true;
    }
}
