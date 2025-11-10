package io.github.viimeinen1.ainventory;

import org.jetbrains.annotations.NotNull;

public class aGUIInventory <T extends Enum<?>> extends aInventory {
    
    public final T inventoryEnum;

    private aGUIInventory(aGUIInventoryBuilder<T> builder) {
        super(builder);
        this.inventoryEnum = builder.inventoryEnum;
    }

    public static <T extends Enum<T>> aGUIInventoryBuilder<T> InventoryBuilder(@NotNull T inventoryEnum) {
        return new aGUIInventoryBuilder<T>(inventoryEnum);
    }

    public static final class aGUIInventoryBuilder <T extends Enum<?>> extends aInventoryBuilder<aGUIInventoryBuilder<T>, aGUIInventory<T>> {

        final T inventoryEnum;

        public aGUIInventoryBuilder(@NotNull T inventoryEnum) {
            this.inventoryEnum = inventoryEnum;
        }

        @Override
        public aGUIInventoryBuilder<T> getThis() {
            return this;
        }

        /**
         * Build inventory.
         * 
         * Will also initialize the inventory.
         * 
         * @return new {@link CustomInventory}
         */
        @Override
        public aGUIInventory<T> build() {
            return new aGUIInventory<>(this);
        }
    }

}
