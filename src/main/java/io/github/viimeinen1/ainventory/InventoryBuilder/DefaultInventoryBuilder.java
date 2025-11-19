package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.DefaultInventory;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public final class DefaultInventoryBuilder extends AbstractInventoryBuilder<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, DefaultInventoryBuilder, DefaultInventory> {

    @Override
    public DefaultInventoryBuilder getThis() {return this;}

    @Override
    public DefaultInventory build() {
        return new DefaultInventory(getThis());
    }
    
}
