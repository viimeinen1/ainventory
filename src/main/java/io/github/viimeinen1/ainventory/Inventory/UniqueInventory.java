package io.github.viimeinen1.ainventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.viimeinen1.ainventory.InventoryBuilder.UniqueInventoryBuilder;
import io.github.viimeinen1.ainventory.InventoryView.DefaultInventoryView;
import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;

public class UniqueInventory extends AbstractInventory<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView, UniqueInventoryBuilder, UniqueInventory> {

    public Map<UUID, DefaultInventoryView> views = new HashMap<>();

    public UniqueInventory(UniqueInventoryBuilder builder) {
        super(builder);
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
            views.values().forEach(view -> {
                view.initialize();
            });
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
    public UniqueInventory getThis() {return this;}

    @Override
    public void open(@NotNull HumanEntity player) {
        if (!views.containsKey(player.getUniqueId())) {

        }
        view.open(player);
    }

    @Override
    public void reload() {
        view.reload();
    }

    @Override
    public void reload(@Nullable HumanEntity player) {
        view.reload(player);
    }

    @Override
    public void reload(Integer... slots) {
        reload(null, slots);
    }

    @Override
    public void reload(@Nullable HumanEntity player, Integer... slots) {
        view.reload(player, slots);
    }

    public static UniqueInventoryBuilder builder() {
        return new UniqueInventoryBuilder();
    }

}
