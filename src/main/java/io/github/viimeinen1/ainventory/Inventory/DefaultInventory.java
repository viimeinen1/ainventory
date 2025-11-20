package io.github.viimeinen1.ainventory.Inventory;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;

import io.github.viimeinen1.ainventory.InventoryBuilder.DefaultInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

/**
 * Default inventory instance
 */
public final class DefaultInventory extends AbstractInventory<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, DefaultInventoryBuilder, DefaultInventory> {

    public DefaultInventory(DefaultInventoryBuilder builder) {
        super(builder);
    }

    @Override
    public DefaultInventoryView createView(@Nullable HumanEntity player) {
        DefaultInventoryView view = new DefaultInventoryView(
            builder.size,
            builder.title,
            builder.initialization,
            builder.openFunction,
            builder.closeFunction,
            builder.requirementFunction,
            builder.defaultClickAction,
            builder.owner);
        initialize(player);
        return view;
    }

    @Override
    public DefaultInventory getThis() {return this;}

    public static DefaultInventoryBuilder builder() {
        return new DefaultInventoryBuilder();
    }
    
}
