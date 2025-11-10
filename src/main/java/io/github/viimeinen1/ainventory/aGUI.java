package io.github.viimeinen1.ainventory;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

/**
 * a GUI that stores multiple inventories.
 */
public class aGUI <T extends Enum<T>> {

    private Map<T, aGUIInventory<T>> inventories;

    /**
     * create new GUI.
     * 
     * @param inventoryEnumClass enum with all possible inventories in the GUI
     */
    public aGUI(@NotNull Class<T> inventoryEnumClass) {
        inventories = new EnumMap<>(inventoryEnumClass);
    }

    /**
     * Add inventory to the gui.
     * 
     * @param inventory {@link aGUIInventory}
     * @return the inventory that was added.
     */
    public @NotNull aGUIInventory<T> putInventory(@NotNull aGUIInventory<T> inventory) {
        inventories.put(inventory.inventoryEnum, inventory);
        return inventory;
    }

    /**
     * Check if GUI contains inventory.
     * 
     * @param inventoryEnum Enum value of the inventory.
     * @return true if GUI contains the inventory.
     */
    public boolean hasInventory(@NotNull T inventoryEnum) {
        return inventories.containsKey(inventoryEnum);
    }

    /**
     * Open inventory for player.
     * 
     * @param inventoryEnum Enum value of the inventory.
     * @param player Player who the inventory will be opened to.
     */

    /**
     * Open inventory for player.
     * 
     * @param inventoryEnum Enum value of the inventory.
     * @param player Player who the inventory will be opened to.
     * @throws NoSuchElementException if the inventory has not been set.
     */
    public void openInventory(@NotNull T inventoryEnum, @NotNull HumanEntity player) throws NoSuchElementException {
        if (!inventories.containsKey(inventoryEnum)) {
            throw new NoSuchElementException("This inventory has not been set.");
        }
        inventories.get(inventoryEnum).openInventory(player);
    }

    /**
     * Re-Run initialization function of the inventory.
     * 
     * If the inventory is not set, will fail silently.
     * 
     * @param inventoryEnum Enum value of the inventory.
     */
    public void initializeInventory(@NotNull T inventoryEnum) {
        if (!inventories.containsKey(inventoryEnum)) {
            return;
        }
        inventories.get(inventoryEnum).initialize();
    }

    /**
     * Run reload function of the inventory.
     * 
     * If the inventory is not set, will fail silently.
     * 
     * @param inventoryEnum Enum value of the inventory.
     */
    public void reloadInventory(@NotNull T inventoryEnum) {
        if (!inventories.containsKey(inventoryEnum)) {
            return;
        }
        inventories.get(inventoryEnum).reload();
    }

}
