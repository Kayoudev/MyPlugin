package fr.myplugin.commands.city;

import fr.myplugin.utils.city.CityInviteManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandCitydeny implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)) return true;

        if(!CityInviteManager.hasInvite(player.getUniqueId())) {
            player.sendMessage("§cTu n’as aucune invitation en attente.");
            return true;
        }

        CityInviteManager.removeInvite(player.getUniqueId());
        player.sendMessage("§eTu as bien refusé l’invitation.");

        return true;
    }
}
