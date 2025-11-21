package io.github.viimeinen1.ainventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.Nullable;

import io.github.viimeinen1.ainventory.Common.Named;
import io.github.viimeinen1.ainventory.InventoryBuilder.NamedUniqueInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;

public final class NamedUniqueInventory <T extends Enum<T>> extends UniqueInventory implements Named<T> {
    
    private final T name;
    public T name() {return name;}

    public Map<UUID, DefaultInventoryView> views = new HashMap<>();

    public NamedUniqueInventory(NamedUniqueInventoryBuilder<T> builder) {
        super(builder);
        this.name = builder.name();
    }

    @Override
    public DefaultInventoryView createView() {
        return new DefaultInventoryView(
            builder.size,
            builder.title,
            builder.initialization,
            builder.openFunction,
            builder.closeFunction,
            builder.requirementFunction,
            builder.defaultClickAction,
            builder.owner,
            builder.pages
        );
    }

    @Override
    public void initialize(@Nullable HumanEntity player) {
        if (player == null) {
            return;
        }

        if (!views.containsKey(player.getUniqueId())) {
            views.put(
                player.getUniqueId(), 
                createView()
            );
        }

        view.page(0, player);
    }

    @Override
    public NamedUniqueInventory<T> getThis() {return this;}

    public static <T extends Enum<T>> NamedUniqueInventoryBuilder<T> builder(T name) {
        return new NamedUniqueInventoryBuilder<T>(name);
    }

}
