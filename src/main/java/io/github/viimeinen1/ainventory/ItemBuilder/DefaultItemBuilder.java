package io.github.viimeinen1.ainventory.ItemBuilder;

import java.util.Collection;

/**
 * Default item builder without any modifications.
 */
public final class DefaultItemBuilder <B extends ItemBuildable<DefaultItemBuilder<B>, B>> extends AbstractItemBuilder<DefaultItemBuilder<B>, B> {

    public DefaultItemBuilder(B inventory, Integer slot) {
        super(inventory, slot);
    }

    public DefaultItemBuilder(B inventory, Collection<Integer> slots) {
        super(inventory, slots);
    }

    @Override
    public DefaultItemBuilder<B> getThis() {return this;}

}
