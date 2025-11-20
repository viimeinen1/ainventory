package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.UniqueInventory;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public final class UniqueInventoryBuilder extends AbstractInventoryBuilder<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, UniqueInventoryBuilder, UniqueInventory> {
    
    @Override
    public UniqueInventoryBuilder getThis() {return this;}

    @Override
    public UniqueInventory build() {
        return new UniqueInventory(getThis());
    }

}
