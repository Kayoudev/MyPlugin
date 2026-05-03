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

public class CommandCityinfo implements CommandExecutor {

    private final Database database;

    public CommandCityinfo(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String str, @NotNull String[] args) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cSeul un joueur peut exécuter cette commande.");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage("§cUsage: /cityinfo <nom_de_la_ville>");
            return true;
        }

        String cityName = args[0];
        int cityId = database.getCityIdByName(cityName);

        String description = database.getCityDescriptionById(cityId);
        String mayorUuid = database.getCityMayorById(cityId);
        String deputyUuid = database.getCityDeputyById(cityId);
        int citizens = database.getCitizenCountByCityId(cityId);
        String citizensNumber = database.getCitizenCountByCityId().toString();
        int chunks = database.getClaimedChunksCount(cityId);
        String chunksNumber = database.getClaimedChunksCount(cityId).toString();

        String mayorName = getNameFromUUID(mayorUuid);
        String deputyName = deputyUuid != null ? getNameFromUUID(deputyUuid) : "Aucun";

        Inventory inventory = Bukkit.createInventory(null, 54, "Information ");

        //Nom de la ville
        inventory.setItem(10, ItemUtils.getItem(Material.NAME_TAG, 1, "Nom de la ville :", cityName));

        //description de la ville
        inventory.setItem(11, ItemUtils.getItem(Material.BOOK, 1, "Description de la ville :", description));

        //maire de la ville
        inventory.setItem(15, ItemUtils.getItem(Material.GOLDEN_CHESTPLATE, 1, "Maire de la ville :", mayorName));

        //adjoint de la ville
        inventory.setItem(16, ItemUtils.getItem(Material.IRON_CHESTPLATE, 1, "Adjoint de la ville :", deputyName));

        //nombre de citoyens
        inventory.setItem(29, ItemUtils.getItem(Material.PLAYER_HEAD, citizens, "Nombre de citoyens :", citizensNumber));

        //nombre de chunks

        inventory.setItem(33, ItemUtils.getItem(Material.GRASS_BLOCK, chunks, "Nombre de chunks claim :", chunksNumber));

        //annuler
        inventory.setItem(45, ItemUtils.getItem(Material.LIGHT_GRAY_WOOL, 1, "Annuler", "Clique sur le bloc pour sortir du menu."));

        player.openInventory(inventory);

        return true;
    }
}
