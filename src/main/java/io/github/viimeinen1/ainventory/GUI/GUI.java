package io.github.viimeinen1.ainventory.GUI;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import io.github.viimeinen1.ainventory.Inventory.NamedInventory;

/**
 * a GUI that stores multiple inventories.
 */
public class GUI <T extends Enum<T>> {

    private Map<T, NamedInventory<T>> inventories;

    /**
     * create new GUI.
     * 
     * @param inventoryEnumClass enum with all possible inventories in the GUI
     */
    public GUI(@NotNull Class<T> inventoryEnumClass) {
        inventories = new EnumMap<>(inventoryEnumClass);
    }

    /**
     * Add inventory to the gui.
     * 
     * @param inventory {@link NamedInventory}
     * @return the inventory that was added.
     */
    public @NotNull NamedInventory<T> put(@NotNull NamedInventory<T> inventory) {
        inventories.put(inventory.name, inventory);
        return inventory;
    }

    /**
     * Check if GUI contains inventory.
     * 
     * @param name Enum value of the inventory.
     * @return true if GUI contains the inventory.
     */
    public boolean has(@NotNull T name) {
        return inventories.containsKey(name);
    }

    /**
     * Open inventory for player.
     * 
     * @param name Enum value of the inventory.
     * @param player Player who the inventory will be opened to.
     * @throws NoSuchElementException if the inventory has not been set.
     */
    public void open(@NotNull T name, @NotNull HumanEntity player) throws NoSuchElementException {
        if (!inventories.containsKey(name)) {
            throw new NoSuchElementException("This inventory has not been set.");
        }
        inventories.get(name).open(player);
    }

    /**
     * Re-Run initialization function of the inventory.
     * 
     * If the inventory is not set, will fail silently.
     * 
     * @param name Enum value of the inventory.
     */
    public void initialize(@NotNull T name) {
        if (!inventories.containsKey(name)) {
            return;
        }
        inventories.get(name).initialize(null);
    }

    /**
     * Run reload function of the inventory.
     * 
     * If the inventory is not set, will fail silently.
     * 
     * @param name Enum value of the inventory.
     */
    public void reload(@NotNull T name) {
        if (!inventories.containsKey(name)) {
            return;
        }
        inventories.get(name).reload(null);
    }

}
