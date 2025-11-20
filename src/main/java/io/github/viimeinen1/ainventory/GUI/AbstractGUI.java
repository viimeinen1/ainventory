package io.github.viimeinen1.ainventory.GUI;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import io.github.viimeinen1.ainventory.Inventory.AbstractNamedInventory;
import io.github.viimeinen1.ainventory.Inventory.NamedInventory;
import io.github.viimeinen1.ainventory.InventoryBuilder.AbstractNamedInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.AbstractInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.AbstractItemBuilder;

public abstract class AbstractGUI <T extends Enum<T>, A extends AbstractItemBuilder<A, C>, C extends AbstractInventoryView<A, C>, E extends AbstractNamedInventoryBuilder<T, A, C, E, F>, F extends AbstractNamedInventory<T, A, C, E, F>> {

    @FunctionalInterface
    public static interface GUIInventoryGetter <T extends Enum<T>, A extends AbstractItemBuilder<A, C>, C extends AbstractInventoryView<A, C>, E extends AbstractNamedInventoryBuilder<T, A, C, E, F>, F extends AbstractNamedInventory<T, A, C, E, F>> {
        public F build(E builder);
    }

    public final Map<T, F> inventories;

    protected abstract E builder(T name);

    /**
     * create new GUI.
     * 
     * @param inventoryEnumClass enum with all possible inventories in the GUI
     */
    public AbstractGUI(@NotNull Class<T> enumClass) {
        inventories = new EnumMap<>(enumClass);
    }

    /**
     * Add inventory to the gui.
     * 
     * @param inventory {@link NamedInventory}
     * @return the inventory that was added.
     */
    public @NotNull F put(@NotNull T name, @NotNull GUIInventoryGetter<T, A, C, E, F> fn) {
        F inventory = fn.build(builder(name));
        inventories.put(inventory.name, inventory);
        return inventory;
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
     * Get inventory from GUI.
     * 
     * @param name name of the inventory.
     * @return Optional with the inventory.
     */
    public Optional<F> get(@NotNull T name) {
        return Optional.ofNullable(inventories.get(name));
    }

}
