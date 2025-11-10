package io.github.viimeinen1.ainventory;

import org.jetbrains.annotations.NotNull;

/**
 * Inventory that has addinational information for GUI
 */
public class aGUIInventory <T extends Enum<?>> extends aInventory {
    
    /**
     * Enum value of this inventory.
     */
    public final T inventoryEnum;

    private aGUIInventory(Builder<T> builder) {
        super(builder);
        this.inventoryEnum = builder.inventoryEnum;
    }

    /**
     * Creating new inventory.
     * 
     * @param <T> Enum if inventories in GUI
     * @param inventoryEnum enum value of this inventory.
     * @return new {@link aGUIInventoryBuilder}
     */
    public static <T extends Enum<T>> Builder<T> builder(@NotNull T inventoryEnum) {
        return new Builder<T>(inventoryEnum);
    }

    /**
     * Inventory builder for GUIs
     */
    public static final class Builder <T extends Enum<?>> extends aInventory.Builder<Builder<T>, aGUIInventory<T>> {

        final T inventoryEnum;

        /**
         * Create new builder
         * 
         * @param inventoryEnum enum value of this inventory
         */
        public Builder(@NotNull T inventoryEnum) {
            this.inventoryEnum = inventoryEnum;
        }

        /**
         * Get this builder.
         */
        @Override
        public Builder<T> getThis() {
            return this;
        }

        /**
         * Build new {@link aGUIInventory} with the set details.
         * 
         * Will also initialize the inventory.
         * 
         * @return new {@link aGUIInventory}
         */
        @Override
        public aGUIInventory<T> build() {
            return new aGUIInventory<>(this);
        }
    }

}
