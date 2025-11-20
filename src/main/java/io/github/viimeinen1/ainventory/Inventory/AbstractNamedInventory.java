package io.github.viimeinen1.ainventory.Inventory;

import io.github.viimeinen1.ainventory.InventoryBuilder.AbstractNamedInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.AbstractInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.AbstractItemBuilder;

public abstract class AbstractNamedInventory <T extends Enum<T>, A extends AbstractItemBuilder<A, C>, C extends AbstractInventoryView<A, C>, E extends AbstractNamedInventoryBuilder<T, A, C, E, F>, F extends AbstractNamedInventory<T, A, C, E, F>> extends AbstractInventory<A, C, E, F> {
    public final T name;

    public AbstractNamedInventory(E builder) {
        super(builder);
        this.name = builder.name;
    }
}
