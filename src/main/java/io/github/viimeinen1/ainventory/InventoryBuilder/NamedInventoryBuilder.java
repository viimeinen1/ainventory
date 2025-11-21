package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Common.Named;
import io.github.viimeinen1.ainventory.Inventory.NamedInventory;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public class NamedInventoryBuilder <T extends Enum<T>> extends AbstractInventoryBuilder<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, NamedInventoryBuilder<T>, NamedInventory<T>> implements Named<T> {
    
    private final T name;
    public T name() {return name;}

    public NamedInventoryBuilder(T name) {
        this.name = name;
    }

    public NamedInventoryBuilder<T> getThis() {return this;}

    public NamedInventory<T> build() {
        return new NamedInventory<T>(getThis());
    }

}
