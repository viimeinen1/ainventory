package io.github.viimeinen1.ainventory.Inventory;

import io.github.viimeinen1.ainventory.Common.Named;
import io.github.viimeinen1.ainventory.InventoryBuilder.NamedUniqueInventoryBuilder;

public final class NamedUniqueInventory <T extends Enum<T>> extends AbstractUniqueInventory<NamedUniqueInventoryBuilder<T>, NamedUniqueInventory<T>> implements Named<T> {
    
    private final T name;
    public T name() {return name;}

    public NamedUniqueInventory(NamedUniqueInventoryBuilder<T> builder) {
        super(builder);
        this.name = builder.name();
    }

    @Override
    public NamedUniqueInventory<T> getThis() {return this;}

    public static <T extends Enum<T>> NamedUniqueInventoryBuilder<T> builder(T name) {
        return new NamedUniqueInventoryBuilder<T>(name);
    }

}
