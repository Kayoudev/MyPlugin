package fr.myplugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemUtils {

    public static ItemStack getItem(Material material, int number, String customName, String... loreLines) {

        ItemStack item = new ItemStack(material, number);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(customName);
        itemM.setLore(Arrays.asList(loreLines));
        item.setItemMeta(itemM);

        return item;

    }
}
