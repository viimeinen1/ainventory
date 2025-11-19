package io.github.viimeinen1.ainventory.ItemBuilder;

import java.util.Map;

import org.bukkit.inventory.Inventory;

import io.github.viimeinen1.ainventory.InventoryView.AbstractInventoryView.itemClickFunction;
import io.github.viimeinen1.ainventory.InventoryView.AbstractInventoryView.itemReloadFunction;

/**
 * Inventory that items can be built on using something that extends {@link AbstractItemBuilder}
 */
public interface ItemBuildable <A extends AbstractItemBuilder<A, B>, B extends ItemBuildable<A, B>> {

    // public void clickFn(Integer slot, itemClickFunction fn);
    // public void reloadFn(Integer slot, itemReloadFunction<B, A> fn);
    public Map<Integer, itemClickFunction> clickFns();
    public Map<Integer, itemReloadFunction<A, B>> itemReloads();
    public Inventory getInventory();
}
