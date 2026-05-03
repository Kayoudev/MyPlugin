package fr.myplugin.commands.city;

import fr.myplugin.data.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandCitycreate implements CommandExecutor {

    private final Database database;

    public CommandCitycreate(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {

        Player player = (Player) commandSender;

        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cSeul un joueur peut exécuter cette commande.");
            return true;
        }

        if(args.length < 2) {
            commandSender.sendMessage("§cUtilisation: /createcity <nom> <description>");
            return true;
        }

        String cityName = args[0];

        StringBuilder descriptionBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            descriptionBuilder.append(args[i]).append(" ");
        }

        String description = descriptionBuilder.toString().trim();

        boolean success = database.createCity(cityName, description, player.getUniqueId().toString());

        if(success) {
            player.sendMessage("§aLa ville '\" + cityName + \"' a été créée.");
        } else {
            player.sendMessage("§cImpossible de créer la ville. Elle existe peut-être déjà.");
        }

        return true;

    }
}
