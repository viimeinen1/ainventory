package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.NamedUniqueInventory;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public class NamedUniqueInventoryBuilder <T extends Enum<T>> extends AbstractNamedInventoryBuilder<T, DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, NamedUniqueInventoryBuilder<T>, NamedUniqueInventory<T>> {
    
    public NamedUniqueInventoryBuilder(T name) {
        super(name);
    }

    @Override
    public NamedUniqueInventoryBuilder<T> getThis() {return this;}

    @Override
    public NamedUniqueInventory<T> build() {
        return new NamedUniqueInventory<T>(getThis());
    }

}
