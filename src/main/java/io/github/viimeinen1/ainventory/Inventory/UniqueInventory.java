package io.github.viimeinen1.ainventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;

import io.github.viimeinen1.ainventory.InventoryBuilder.DefaultInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryBuilder.UniqueInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public class UniqueInventory extends AbstractInventory<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, UniqueInventoryBuilder, UniqueInventory> {
    
    public Map<UUID, DefaultInventoryView> views = new HashMap<>();

    public UniqueInventory(UniqueInventoryBuilder builder) {
        super(builder);
    }

    @Override
    public DefaultInventoryView createView(@Nullable HumanEntity player) {
        DefaultInventoryView view = new DefaultInventoryView(
            builder.size,
            builder.title,
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
                    builder.openFunction,
                    builder.closeFunction,
                    builder.requirementFunction,
                    builder.defaultClickAction,
                    builder.owner
                )
            );
        }

        initFn.run(view, Optional.ofNullable(player));
    }

    @Override
    public UniqueInventory getThis() {return this;}

    public static DefaultInventoryBuilder builder() {
        return new DefaultInventoryBuilder();
    }

}
