package io.github.viimeinen1.ainventory;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class aGUI <T extends Enum<T>> {
    
    private Map<T, aGUIInventory<T>> inventories;

    public aGUI(@NotNull Class<T> inventoryEnumClass) {
        inventories = new EnumMap<>(inventoryEnumClass);
    }

    public @NotNull aGUIInventory<T> putInventory(@NotNull aGUIInventory<T> inventory) {
        inventories.put(inventory.inventoryEnum, inventory);
        return inventory;
    }

    public boolean hasInventory(@NotNull T inventoryEnum) {
        return inventories.containsKey(inventoryEnum);
    }

    public void openInventory(@NotNull T inventoryEnum, @NotNull HumanEntity player) {
        inventories.get(inventoryEnum).openInventory(player);
    }

    public void initializeInventory(@NotNull T inventoryEnum) {
        inventories.get(inventoryEnum).initialize();
    }

    public void reloadInventory(@NotNull T inventoryEnum) {
        inventories.get(inventoryEnum).reload();
    }

}
