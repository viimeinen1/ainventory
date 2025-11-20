package io.github.viimeinen1.ainventory.InventoryBuilder;

import io.github.viimeinen1.ainventory.Inventory.AbstractNamedInventory;
import io.github.viimeinen1.ainventory.InventoryView.AbstractInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.AbstractItemBuilder;

public abstract class AbstractNamedInventoryBuilder <T extends Enum<T>, A extends AbstractItemBuilder<A, C>, C extends AbstractInventoryView<A, C>, E extends AbstractNamedInventoryBuilder<T, A, C, E, F>, F extends AbstractNamedInventory<T, A, C, E, F>> extends AbstractInventoryBuilder<A, C, E, F> {

    public final T name;

    public AbstractNamedInventoryBuilder(T name) {
        this.name = name;
    }

}
