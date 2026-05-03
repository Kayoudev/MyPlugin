package fr.myplugin;

import fr.myplugin.data.Database;
import fr.myplugin.utils.ItemUtils;
import fr.myplugin.utils.city.CityEditInputManager;
import fr.myplugin.utils.city.CityInviteManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPluginListener implements Listener {

    private final Database database;
    private final JavaPlugin plugin;

    public MyPluginListener(Database database, JavaPlugin plugin) {
        this.database = database;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if(!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inventoryGet = event.getInventory();

        if (event.getView().getTitle().equals("City Edit")) {

            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.contains("Nom de la ville")) {
                player.closeInventory();
                player.sendMessage("§eÉcris le nouveau nom de ta ville dans le chat :");

                CityEditInputManager.waitingForCityNameInput.put(player.getUniqueId(), true);
            }

            if (itemName.contains("Description de la ville")) {
                player.closeInventory();
                player.sendMessage("§eÉcris la nouvelle description de ta ville dans le chat.");

                CityEditInputManager.waitingForCityDescriptionInput.put(player.getUniqueId(), true);
            }

            if (itemName.contains("Adjoint de la ville")) {
                player.closeInventory();
                player.sendMessage("§eÉcris le pseudo du joueur que tu veux nommer adjoint dans le chat.");
                CityEditInputManager.waitingForDeputyInput.put(player.getUniqueId(), true);
            }

            if (clickedItem.getType() == Material.LIME_WOOL && clickedItem.getItemMeta().getDisplayName().contains("Ajouter un citoyen")) {
                player.closeInventory();
                player.sendMessage("§eÉcris le pseudo du joueur que tu veux inviter dans ta ville :");

                CityEditInputManager.waitingForCitizenInvite.put(player.getUniqueId(), true); // Ajoute cette map ci-dessous
            }

            if (itemName.contains("Exclure un citoyen")) {
                player.closeInventory();
                player.sendMessage("§eÉcris le pseudo du joueur que tu veux exclure de ta ville :");
                CityEditInputManager.waitingForCitizenRemoval.put(player.getUniqueId(), true);
            }

            if (itemName.contains("Annuler")) {
                player.closeInventory();
            }

            if (itemName.contains("Supprimer la ville")) {
                player.closeInventory();

                Inventory inventory = Bukkit.createInventory(null, 27, "Supprimer la ville");

                //annuler
                inventory.setItem(11, ItemUtils.getItem(Material.LIME_WOOL, 1, "Annuler", "Clique sur le bloc pour annuler la suppression de ta ville."));

                //supprimer
                inventory.setItem(15, ItemUtils.getItem(Material.TNT, 1, "Supprimer la ville", "Clique sur le bloc pour supprimer ta ville DÉFINITIVEMENT."));

                player.openInventory(inventory);
            }
        }

        if(event.getView().getTitle().equals("Supprimer la ville")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.contains("Annuler")) {
                player.closeInventory();
                player.sendMessage("§aSuppression annulée.");
            }

            if (itemName.contains("Supprimer la ville")) {
                player.closeInventory();

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    boolean success = database.deleteCity(player.getUniqueId().toString());

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if(success) {
                            player.sendMessage("§cTa ville a bien été supprimée.");
                        } else {
                            player.sendMessage("§cErreur lors de la suppression de la ville.");
                        }
                    });
                });
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (CityEditInputManager.waitingForCityNameInput.containsKey(player.getUniqueId())) {

            event.setCancelled(true);
            String newName = event.getMessage();

            Bukkit.getScheduler().runTask(plugin, () -> {
                boolean updated = database.updateCityName(player.getUniqueId().toString(), newName);
                if (updated) {
                    player.sendMessage("§aLe nom de la ville a été mis à jour avec succès en " + newName + ".");
                } else {
                    player.sendMessage("§cErreur lors de la mise à jour du nom de la ville.");
                }

                CityEditInputManager.waitingForCityNameInput.remove(player.getUniqueId());
            });
        }

        if (CityEditInputManager.waitingForCityDescriptionInput.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String message = event.getMessage();
            String newDescription = message.trim();

            if (newDescription.length() > 200) {
                player.sendMessage("§cLa description est trop longue (max 200 caractères).");
                return;
            }

            boolean updated = database.updateCityDescription(player.getUniqueId().toString(), newDescription);

            if (updated) {
                player.sendMessage("§aLa description de ta ville a été mise à jour !");
            } else {
                player.sendMessage("§cUne erreur est survenue lors de la mise à jour.");
            }

            CityEditInputManager.waitingForCityDescriptionInput.remove(player.getUniqueId());
            return;
        }

        if (CityEditInputManager.waitingForDeputyInput.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String message = event.getMessage();
            String deputyName = message.trim();

            Player deputy = Bukkit.getPlayerExact(deputyName);

            if (deputy == null) {
                player.sendMessage("§cCe joueur est introuvable ou non connecté.");
                CityEditInputManager.waitingForDeputyInput.remove(player.getUniqueId());
                return;
            }

            boolean success = database.setCityDeputy(player.getUniqueId().toString(), deputy.getUniqueId().toString());

            if (success) {
                player.sendMessage("§a" + deputyName + " est maintenant l'adjoint de ta ville !");
                deputy.sendMessage("§eTu as été nommé adjoint de la ville de " + player.getName() + ".");
            } else {
                player.sendMessage("§cErreur lors de la nomination de l'adjoint.");
            }

            CityEditInputManager.waitingForDeputyInput.remove(player.getUniqueId());
            return;
        }

        if (CityEditInputManager.waitingForCitizenInvite.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String message = event.getMessage();
            String targetName = message.trim();
            Player target = Bukkit.getPlayerExact(targetName);

            if (target == null) {
                player.sendMessage("§cJoueur introuvable.");
                CityEditInputManager.waitingForCitizenInvite.remove(player.getUniqueId());
                return;
            }

            if (CityInviteManager.hasInvite(target.getUniqueId())) {
                player.sendMessage("§cCe joueur a déjà une invitation en attente.");
                CityEditInputManager.waitingForCitizenInvite.remove(player.getUniqueId());
                return;
            }

            if(CityInviteManager.invitePlayer(target.getUniqueId(), player.getUniqueId())) {

                player.sendMessage("§aInvitation envoyée à " + target.getName() + ".");
                target.sendMessage("§e" + player.getName() + " t’a invité dans sa ville. Fais §a/cityaccept §epour accepter ou §c/citydeny §epour refuser.");

                CityEditInputManager.waitingForCitizenInvite.remove(player.getUniqueId());
                return;
            }
        }

        if (CityEditInputManager.waitingForCitizenRemoval.containsKey(player.getUniqueId())) {
            event.setCancelled(true);

            String message = event.getMessage();
            String targetName = message.trim();
            Player target = Bukkit.getPlayerExact(targetName);

            int cityId = database.getCityIdByMayor(player.getUniqueId().toString());
            String targetUUID = target.getUniqueId().toString();

            if (target == null) {
                player.sendMessage("§cCe joueur est introuvable ou non connecté.");
                CityEditInputManager.waitingForCitizenRemoval.remove(player.getUniqueId());
                return;
            }

            boolean success = database.removeCitizen(cityId, targetUUID);

            if (success) {
                player.sendMessage("§a" + target.getName() + " a été exclu de ta ville.");
                target.sendMessage("§cTu as été exclu de la ville de " + player.getName() + ".");
            } else {
                player.sendMessage("§cImpossible d’exclure ce joueur (peut-être qu’il ne fait pas partie de ta ville ?).");
            }

            CityEditInputManager.waitingForCitizenRemoval.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        String world = chunk.getWorld().getName();
        int x = chunk.getX();
        int z = chunk.getZ();

        int cityId = database.getCityIdByChunk(world, x, z);
        if (cityId == -1) return;

        if (!database.isCitizen(player.getUniqueId().toString(), cityId)) {
            event.setCancelled(true);
            player.sendMessage("§cTu ne peux pas casser ici. Ce territoire appartient à une ville.");
        }
    }
}
