package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Common.Named;
import io.github.viimeinen1.ainventory.Inventory.NamedUniqueInventory;

public class NamedUniqueInventoryBuilder <T extends Enum<T>> extends UniqueInventoryBuilder implements Named<T> {
    
    private final T name;
    public T name() {return name;}

    public NamedUniqueInventoryBuilder(T name) {
        super();
        this.name = name;
    }

    @Override
    public NamedUniqueInventoryBuilder<T> getThis() {return this;}

    @Override
    public NamedUniqueInventory<T> build() {
        return new NamedUniqueInventory<T>(getThis());
    }

}
