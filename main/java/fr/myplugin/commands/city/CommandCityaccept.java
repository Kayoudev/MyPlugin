package fr.myplugin.commands.city;

import fr.myplugin.data.Database;
import fr.myplugin.utils.city.CityInviteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandCityaccept implements CommandExecutor {

    private final Database database;

    public CommandCityaccept(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return true;

        UUID playerUuid = player.getUniqueId();

        if(!CityInviteManager.hasInvite(playerUuid)){
            player.sendMessage("§cTu n’as aucune invitation en attente.");
            return true;
        }

        UUID mayorUuid = CityInviteManager.getInviter(playerUuid);
        CityInviteManager.removeInvite(playerUuid);

        int cityId = database.getCityIdByMayor(mayorUuid.toString());
        if(cityId == -1){
            player.sendMessage("§cErreur : ville introuvable.");
            return true;
        }

        database.addCitizen(cityId, playerUuid.toString());
        player.sendMessage("§aTu as rejoint la ville.");
        Player mayor = Bukkit.getPlayer(mayorUuid);
        if(mayor != null){
            mayor.sendMessage("§a" + player.getName() + " a accepté l\'invitation. Il est maintenant citoyen de ta ville.");
        }

        return true;
    }
}
