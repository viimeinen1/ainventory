package io.github.viimeinen1.ainventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class aInventoryListener implements Listener {
    
    @EventHandler
    public static void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        // return if we don't have this plugin's inv
        if (inv == null || !(inv.getHolder(false) instanceof aInventory inventory)) {
            if (event.getView().getTopInventory().getHolder(false) instanceof aInventory transferInventory && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                transferInventory.onInventoryTransfer(event);
            }
            return;
        }
        inventory.onInventoryClick(event);
    }

}
