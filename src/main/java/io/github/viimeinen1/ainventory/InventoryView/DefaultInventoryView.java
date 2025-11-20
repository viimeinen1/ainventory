package io.github.viimeinen1.ainventory.InventoryView;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.viimeinen1.ainventory.ItemBuilder.DefaultItemBuilder;
import net.kyori.adventure.text.Component;

/**
 * Default inventory view.
 * 
 * Everything possible is final.
 * Only reload and update is permitted.
 */
public final class DefaultInventoryView extends AbstractInventoryView<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView> {

    /**
     * Create new aInventoryView with all parameters.
     * 
     * All parameters are final, if change is required, create new view.
     * 
     * @param size
     * @param title
     * @param openFn
     * @param closeFn
     * @param requirementFn
     * @param defaultClickFn
     * @param clickFns
     * @param itemReloads
     * @param owner
     */
    public DefaultInventoryView(
        @NotNull INVENTORY_SIZE size,
        @Nullable Component title,
        @Nullable inventoryFunction<DefaultItemBuilder<DefaultInventoryView>, DefaultInventoryView> initFn,
        @Nullable inventoryOpenFunction openFn,
        @Nullable inventoryCloseFunction closeFn,
        @Nullable requirementFunction requirementFn,
        @Nullable itemClickFunction defaultClickFn,
        @Nullable UUID owner
    ) {
        super(
            size,
            title,
            initFn,
            openFn,
            closeFn,
            requirementFn,
            defaultClickFn,
            owner
        );
    }

    @Override
    protected void initPage(Integer page, @Nullable HumanEntity player) {
        clear();
        initFn.ifPresent(fn -> fn.run(this, Optional.ofNullable(player)));
        if (pageInits.containsKey(page)) {
            pageInits.get(page).run(this, Optional.ofNullable(player));
        }
        update();
    }

    @Override
    public Inventory getInventory() {return this.inventory;}

    @Override
    public DefaultItemBuilder<DefaultInventoryView> ItemBuilder(Integer slot) {
        return new DefaultItemBuilder<DefaultInventoryView>(this, slot);
    }

    @Override
    DefaultItemBuilder<DefaultInventoryView> ItemBuilder(Collection<Integer> slots) {
        return new DefaultItemBuilder<DefaultInventoryView>(this, slots);
    }
    
}
