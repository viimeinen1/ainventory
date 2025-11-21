package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.UniqueInventory;

public class UniqueInventoryBuilder extends AbstractUniqueInventoryBuilder<UniqueInventoryBuilder, UniqueInventory> {
    
    @Override
    public UniqueInventoryBuilder getThis() {return this;}

    @Override
    public UniqueInventory build() {
        return new UniqueInventory(getThis());
    }

}
