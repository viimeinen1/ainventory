package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.NamedInventory;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public final class NamedInventoryBuilder <T extends Enum<?>> extends AbstractInventoryBuilder<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, NamedInventoryBuilder<T>, NamedInventory<T>> {

    T name;

    public NamedInventoryBuilder(T name) {
        this.name = name;
    }

    @Override
    public NamedInventoryBuilder<T> getThis() {return this;}

    @Override
    public NamedInventory<T> build() {
        return new NamedInventory<T>(getThis(), name);
    }
    
}
