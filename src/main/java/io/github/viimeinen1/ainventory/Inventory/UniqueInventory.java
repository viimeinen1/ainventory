package io.github.viimeinen1.ainventory.Inventory;

import io.github.viimeinen1.ainventory.InventoryBuilder.UniqueInventoryBuilder;

public class UniqueInventory extends AbstractUniqueInventory<UniqueInventoryBuilder, UniqueInventory> {

    public UniqueInventory(UniqueInventoryBuilder builder) {
        super(builder);
    }

    @Override
    public UniqueInventory getThis() {return this;}

    public static UniqueInventoryBuilder builder() {
        return new UniqueInventoryBuilder();
    }

}
