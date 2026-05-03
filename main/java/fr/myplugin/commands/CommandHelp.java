package fr.myplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandHelp implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        commandSender.sendMessage("Voici les différentes commandes disponibles : \n §c/city create §6<nom> <description> §f-> §3créer une ville \n §c/city edit §f-> §3accéder au panneau de modification de la ville (réservé) \n §c/city info §6<nom> §f-> §3permet d'obtenir les informations d'une ville \n §c/city claim §f-> §3permet de claim un chunck (réservé)");

        return false;
    }
}
