package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.NamedInventory;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public class NamedInventoryBuilder <T extends Enum<T>> extends AbstractNamedInventoryBuilder<T, DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, NamedInventoryBuilder<T>, NamedInventory<T>> {
    
    public NamedInventoryBuilder(T name) {
        super(name);
    }

    public NamedInventoryBuilder<T> getThis() {return this;}

    public NamedInventory<T> build() {
        return new NamedInventory<T>(getThis());
    }

}
