package fr.myplugin;

import fr.myplugin.commands.CommandHelp;
import fr.myplugin.commands.CommandTest;
import fr.myplugin.commands.city.*;
import fr.myplugin.data.Database;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MyPlugin extends JavaPlugin{

    private Database database;

    @Override
    public void onEnable() {

        getLogger().info("MyPlugin is starting...");

        try {

            //chargement de la db
            File databaseFile = getDataFolder();
            database = new Database(databaseFile);
            database.connect();

            getLogger().info("La base de données s'est bien connecté.");

            System.out.println("Le plugin viens de s'allumer.");

            //chargement des commandes
            //commandes des villes
            getCommand("citycreate").setExecutor(new CommandCitycreate(database));
            getCommand("cityedit").setExecutor(new CommandCityedit(database));
            getCommand("cityaccept").setExecutor(new CommandCityaccept(database));
            getCommand("citydeny").setExecutor(new CommandCitydeny());
            getCommand("cityclaim").setExecutor(new CommandCityclaim(database));
            getCommand("cityunclaim").setExecutor(new CommandCityunclaim(database));
            getCommand("cityinfo").setExecutor(new CommandCityinfo(database));

            //commandes d'informations
            getCommand("help").setExecutor(new CommandHelp());

            getCommand("test").setExecutor(new CommandTest());

            Bukkit.getPluginManager().registerEvents((Listener) new MyPluginListener(database, this), this);

            getLogger().info("MyPlugin started successfully.");
        } catch (Exception e) {
            getLogger().severe("MyPlugin failed to start: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this); // très important
        }
    }


    @Override
    public void onDisable() {

        //fermeture de la db
        if(database != null){
            database.close();
        }
        getLogger().info("La base de donnée s'est bien déconnecté.");

        System.out.println("Le plugin viens de s'éteindre.");
    }
}
