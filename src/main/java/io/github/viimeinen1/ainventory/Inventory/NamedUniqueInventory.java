package io.github.viimeinen1.ainventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;

import io.github.viimeinen1.ainventory.InventoryBuilder.DefaultInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryBuilder.NamedUniqueInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public final class NamedUniqueInventory <T extends Enum<T>> extends AbstractNamedInventory<T, DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, NamedUniqueInventoryBuilder<T>, NamedUniqueInventory<T>> {
    
    public Map<UUID, DefaultInventoryView> views = new HashMap<>();

    public NamedUniqueInventory(NamedUniqueInventoryBuilder<T> builder) {
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
            builder.owner
        );
        initialize(player);
        return view;
    }

    @Override
    public void initialize(@Nullable HumanEntity player) {
        if (player == null) {
            return;
        }

        if (!views.containsKey(player.getUniqueId())) {
            views.put(
                player.getUniqueId(), 
                new DefaultInventoryView(
                    builder.size,
                    builder.title,
                    builder.initialization,
                    builder.openFunction,
                    builder.closeFunction,
                    builder.requirementFunction,
                    builder.defaultClickAction,
                    builder.owner
                )
            );
        }

        view.page(0, player);
    }

    @Override
    public NamedUniqueInventory<T> getThis() {return this;}

    public static DefaultInventoryBuilder builder() {
        return new DefaultInventoryBuilder();
    }

}
