package io.github.viimeinen1.ainventory.GUI;

import org.jetbrains.annotations.NotNull;

import io.github.viimeinen1.ainventory.Inventory.NamedUniqueInventory;
import io.github.viimeinen1.ainventory.InventoryBuilder.NamedUniqueInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public final class UniqueGUI <T extends Enum<T>> extends AbstractGUI<T, DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, NamedUniqueInventoryBuilder<T>, NamedUniqueInventory<T>> {
    
    public UniqueGUI(@NotNull Class<T> enumClass) {
        super(enumClass);
    }

    public NamedUniqueInventoryBuilder<T> builder(T name) {return new NamedUniqueInventoryBuilder<T>(name);};

}
