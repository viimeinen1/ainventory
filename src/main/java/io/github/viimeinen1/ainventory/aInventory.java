package io.github.viimeinen1.ainventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class aInventory implements InventoryHolder {
    
    /**
     * Possible inventory sizes
     */
    public static enum INVENTORY_SIZE {
        CHEST_9x1(9),
        CHEST_9x2(18),
        CHEST_9x3(27),
        CHEST_9x4(36),
        CHEST_9x5(45),
        CHEST_9x6(54);

        private final int size;
        private INVENTORY_SIZE(int size) {
            this.size = size;
        }
        public int size() {return this.size;}
    }

    /**
     * Gui click function
     */
    @FunctionalInterface
    public static interface guiItemClickEvent {
        void run(InventoryClickEvent event);
    }

    /**
     * Default click actions
     */
    @FunctionalInterface
    public static interface defaultGuiClickAction {
        void run(InventoryClickEvent event);
    }

    /**
     * Initializing inventory
     */
    @FunctionalInterface
    public static interface inventoryInitializationFunction {
        void run(aInventory customInventory);
    }

    /**
     * Item reload function
     */
    @FunctionalInterface
    public static interface itemReloadFunction {
        void run(aItemBuilder builder);
    }

    /**
     * Inventory use requirement function
     */
    @FunctionalInterface
    public static interface guiRequirementFunction {
        boolean run(HumanEntity player);
    }

    public static final class Messages {
        public static final String NO_OPEN_PERMISSION = "<red>You don't have permission to open this inventory!";
        public static final String NO_USE_PERMISSION = "<red>You don't have permission to use this inventory!";
    }

    public final Inventory inventory;
    public UUID owner;
    private final Map<Integer, guiItemClickEvent> slotFunctions = new HashMap<>();
    private final Map<Integer, itemReloadFunction> itemReloads = new HashMap<>();
    private inventoryInitializationFunction initialization;
    private defaultGuiClickAction defatulGuiItemAction;
    private guiRequirementFunction requirementFunction;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public boolean openInventory(@NotNull HumanEntity player) {
        if (requirementFunction != null && !requirementFunction.run(player)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_OPEN_PERMISSION));
            return false;
        }
        reload(); // reload inventory
        player.openInventory(inventory);
        return true;
    }

    /**
     * Clears inventory, Re-runs initialization function and updates inventory to it's viewers.
     */
    public aInventory initialize() {
        // clear inventory and functions so there isn't any previous items left from last initialization
        inventory.clear();
        slotFunctions.clear();
        itemReloads.clear();
        initialization.run(this);
        updateInventory();
        return this;
    }

    /**
     * Runs reload function of all slots.
     */
    public aInventory reload() {
        itemReloads.forEach((slot, reloadFn) -> {
            if (reloadFn == null) {return;}
            reloadFn.run(new aItemBuilder(this, slot));
        });
        return this;
    }

    /**
     * Runs reload function for specific slot.
     * 
     * @param slot slot to run reload on
     */
    public aInventory reload(@NotNull Integer slot) {
        itemReloads.get(slot).run(new aItemBuilder(this, slot));
        return this;
    }

    public aInventory onInventoryClick(@NotNull InventoryClickEvent event) {
        if (requirementFunction != null && !requirementFunction.run(event.getWhoClicked())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_USE_PERMISSION));
            return this;
        }

        if (defatulGuiItemAction != null) {
            defatulGuiItemAction.run(event);
        }

        if (slotFunctions.containsKey(event.getSlot()) && slotFunctions.get(event.getSlot()) != null) {
            slotFunctions.get(event.getSlot()).run(event);
        }

        return this;
    }

    public aInventory onInventoryTransfer(@NotNull InventoryClickEvent event) {
        if (requirementFunction != null && !requirementFunction.run(event.getWhoClicked())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_USE_PERMISSION));
            return this;
        }

        if (defatulGuiItemAction != null) {
            defatulGuiItemAction.run(event);
        }

        return this;
    }

    /**
     * Update inventory for all it's viewers.
     */
    public aInventory updateInventory() {
        this.inventory.getViewers().forEach(entity -> {
            if (entity instanceof Player pl) {
                pl.updateInventory();
            }
        });
        return this;
    }

    public static aDefaultInventoryBuilder InventoryBuilder() {
        return new aDefaultInventoryBuilder();
    }

    public aInventory(aInventoryBuilder<?, ?> builder) {
        this.owner = builder.owner;
        this.defatulGuiItemAction = builder.defaultGuiItemAction;
        this.initialization = builder.initialization;
        this.requirementFunction = builder.requirementFunction;

        if (builder.size == null) {
            this.inventory = Bukkit.createInventory(this, 27, builder.name);
        } else {
            this.inventory = Bukkit.createInventory(this, builder.size, builder.name);
        }

        initialize();
        updateInventory(); // just in case someone has the inv open for some unforseen reason
    }

    public static class aDefaultInventoryBuilder extends aInventoryBuilder<aDefaultInventoryBuilder, aInventory> {

        @Override
        public aDefaultInventoryBuilder getThis() {
            return this;
        }

        @Override
        public aInventory build() {
            return new aInventory(this);
        }

    }

    /**
     * Custom inventory builder
     */
    public static abstract class aInventoryBuilder <T extends aInventoryBuilder<T, K>, K extends aInventory> {
        UUID owner;
        inventoryInitializationFunction initialization;
        defaultGuiClickAction defaultGuiItemAction;
        Integer size;
        Component name;
        guiRequirementFunction requirementFunction;

        // for subclasses to override
        public abstract T getThis();
        public abstract K build();

        public T initialization(inventoryInitializationFunction fn) {
            this.initialization = fn;
            return getThis();
        }

        public T defaultAction(@NotNull defaultGuiClickAction fn) {
            this.defaultGuiItemAction = fn;
            return getThis();
        }

        public T size(INVENTORY_SIZE size) {
            this.size = size.size();
            return getThis();
        }

        public <R> T name(R name) {
            if (name instanceof Component component) {
                this.name = component;
            } else if (name instanceof String txt) {
                this.name = MiniMessage.miniMessage().deserialize(txt);
            }
            return getThis();
        }

        public T owner(UUID owner) {
            this.owner = owner;
            return getThis();
        }

        /**
         * Function that is ran every time inventory is opened, or slot is clicked.
         * 
         * If function returns false, all futher execution is blocked.
         * 
         * @param requirementFunction function
         */
        public T require(guiRequirementFunction requirementFunction) {
            this.requirementFunction = requirementFunction;
            return getThis();
        }
    }

    /**
     * Modify slot.
     * 
     * @param slot
     * @param material
     */
    public aItemBuilder ItemBuilder(@NotNull int slot) {
        return new aItemBuilder(this, slot);
    }

    /**
     * Modify slots.
     * 
     * All parameters will be taken from the first item in the list and copied to all slots.
     * 
     * @param slots
     * @param material
     */
    public aItemBuilder ItemBuilder(@NotNull Collection<Integer> slots) {
        return new aItemBuilder(this, slots);
    }

    private aInventory buildItem(@NotNull aItemBuilder builder) {
        builder.slots.forEach(slot -> {
            if (builder.reloadFn != null) {
                this.itemReloads.put(slot, builder.reloadFn);
            } else if (builder.removeReloadFunction) {
                this.itemReloads.remove(slot);
            }
            if (builder.slotFn != null) {
                    this.slotFunctions.put(slot, builder.slotFn);
            } else if (builder.removeSlotFuntion) {
                this.slotFunctions.remove(slot);
            }
            this.inventory.setItem(slot, builder.item);
        });
        return this;
    }

    /**
     * Custom inventory item builder
     */
    public static class aItemBuilder {
        aInventory inventory;
        guiItemClickEvent slotFn;
        ItemStack item;
        List<Integer> slots = new ArrayList<>();
        itemReloadFunction reloadFn;
        boolean removeReloadFunction = false;
        boolean removeSlotFuntion = false;

        /**
         * Modify slot.
         * 
         * @param inventory
         * @param slot
         * @param material
         */
        public aItemBuilder(@NotNull aInventory inventory, @NotNull int slot) {
            this.inventory = inventory;
            this.slots.add(slot);
            this.item = inventory.inventory.getItem(slot);
            if (this.item == null) {
                this.item = ItemStack.of(Material.AIR);
            } else {
                this.item = this.item.clone();
            }
            this.slotFn = inventory.slotFunctions.get(slot);
        }

        /**
         * Modify multiple slots.
         * 
         * All previous parameters will be taken from the first item in the list.
         * 
         * @param inventory
         * @param slots
         */
        public aItemBuilder(@NotNull aInventory inventory, @NotNull Collection<Integer> slots) {
            this.inventory = inventory;
            this.slots.addAll(slots);
            this.item = inventory.inventory.getItem(slots.iterator().next());
            if (this.item == null) {
                this.item = ItemStack.of(Material.AIR);
            } else {
                this.item = this.item.clone();
            }
            this.slotFn = inventory.slotFunctions.get(slots.iterator().next());
        }

        /**
         * Add slot that this item will be copied to.
         * 
         * @param slot
         */
        public aItemBuilder addSlot(@NotNull Integer slot) {
            this.slots.add(slot);
            return this;
        }

        /**
         * Add slots that this item will be copied to.
         * 
         * @param slot
         */
        public aItemBuilder addSlot(@NotNull Collection<Integer> slots) {
            this.slots.addAll(slots);
            return this;
        }

        /**
         * Set the underlying item.
         * 
         * This will discard all item related values added before.
         * 
         * @param item
         */
        public aItemBuilder setItem(@NotNull ItemStack item) {
            this.item = item.clone();
            return this;
        }

        /**
         * Get the custominventory the item will be added to.
         */
        public aInventory getInventory() {
            return inventory;
        }

        /**
         * Get slots that this item will be copied to.
         */
        public List<Integer> getSlots() {
            return slots;
        }

        /**
         * If slot Function should be removed.
         * 
         * Will be ignored if new slot function is set.
         * 
         * @param removeSlotFuntion
         */
        public aItemBuilder removeSlotFuntion(boolean removeSlotFuntion) {
            this.removeSlotFuntion = removeSlotFuntion;
            return this;
        }

        /**
         * If reload function should be removed.
         * 
         * Will be ignored if new reload function is set.
         * 
         * @param removeReloadFunction
         */
        public aItemBuilder removeReloadFunction(boolean removeReloadFunction) {
            this.removeReloadFunction = removeReloadFunction;
            return this;
        }

        /**
         * Set material of the item.
         * May cause data loss, so use as early as possible.
         * 
         * @param material
         */
        public aItemBuilder material(@NotNull Material material) {
            if (this.item == null || this.item.getType().equals(Material.AIR)) {
                this.item = ItemStack.of(material);
            } else {
                this.item = this.item.withType(material);
            }

            // this works as well
            // ItemStack newItem = ItemStack.of(material);
            // if (this.item != null) {
            //     newItem.copyDataFrom(this.item, this.item.getDataTypes().stream().filter((type) -> {
            //         return !type.equals(DataComponentTypes.ITEM_MODEL);
            //     }).collect(Collectors.toSet())::contains);
            // }
            // this.item = newItem;

            return this;
        }

        /**
         * Set amount of item.
         * 
         * @param amount
         */
        public aItemBuilder amount(@NotNull Integer amount) {
            this.item.setAmount(amount);
            return this;
        }

        /**
         * Clear itembuilder completely.
         * Sets material to air, so use {@link ItemBuilder#material(Material)} again.
         */
        public aItemBuilder clear() {
            item = ItemStack.of(Material.AIR);
            slotFn = null;
            reloadFn = null;
            return this;
        }

        /**
         * Item reload function.
         * The function will be called every time inventory is opened.
         * 
         * Using null will not change the original function. 
         * Use {@link ItemBuilder#removeReloadFunction(boolean)} to remove reload function.
         * 
         * @param fn reload function
         */
        public aItemBuilder reload(@Nullable itemReloadFunction fn) {
            this.reloadFn = fn;
            return this;
        }

        /**
         * Displayname of item.
         * Using null will do nothing.
         * 
         * Accepts both String and Component.
         * Other types will not be set.
         * 
         * @param name name of item
         * @param italic if name should be italic (default true)
         * @return builder
         */
        public <K> aItemBuilder name(@Nullable K name, @NotNull boolean italic) {
            if (name == null) {
                return this;
            } else if (name instanceof String str) {
                item.setData(DataComponentTypes.CUSTOM_NAME, MiniMessage.miniMessage().deserialize(str).decoration(TextDecoration.ITALIC, italic));
            } else if (name instanceof Component component) {
                item.setData(DataComponentTypes.CUSTOM_NAME, component.decoration(TextDecoration.ITALIC, italic));
            }
            return this;
        }

        /**
         * Lore of item.
         * Using null will do nothing.
         * 
         * Accepts both String and Component.
         * If other types are used, those lines will be skipped.
         * 
         * @param <K> {@link String} or {@link Component}
         * @param loreLines lines
         * @param italic if lore should be italic
         * @return builder
         */

        public <K> aItemBuilder lore(@Nullable Collection<K> loreLines, @NotNull boolean italic) {
            if (loreLines == null) {
                return this;
            }
            ItemLore.Builder loreBuilder = ItemLore.lore();
            loreLines.forEach(line -> {
                if (line == null) {
                    loreBuilder.addLine(Component.empty());
                } else if (line instanceof String str) {
                    loreBuilder.addLine(MiniMessage.miniMessage().deserialize(str).decoration(TextDecoration.ITALIC, italic));
                } else if (line instanceof Component component) {
                    loreBuilder.addLine(component.decoration(TextDecoration.ITALIC, italic));
                }
            });
            item.setData(DataComponentTypes.LORE, loreBuilder.build());
            return this;
        }

        /**
         * Function that is run when item is clicked.
         * 
         * Using null will keep original function.
         * Use {@link ItemBuilder#removeSlotFuntion(boolean)} to remove slot function.
         * 
         * @param slotFn function
         */
        public aItemBuilder function(@Nullable guiItemClickEvent slotFn) {
            this.slotFn = slotFn;
            return this;
        }

        /**
         * Set data component to item.
         * 
         * @param type component type
         * @param value component value
         */
        public <K> aItemBuilder setData(@NotNull DataComponentType.Valued<K> type, @NotNull K value) {
            item.setData(type, value);
            return this;
        }

        /**
         * Sets item to linked inventory.
         * If not called, all changes to builder will be discarded.
         */
        public aInventory build() {
            inventory.buildItem(this);
            return inventory;
        }
    }

    /**
     * Initialize listener for aInventory.
     * Without initializing the listener, the slot functions will not work.
     * 
     * @param plugin {@link JavaPlugin} that the listener will be listed for.
     */
    public static void initializeListener(@NotNull JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new aInventoryListener(), plugin);
    }

}
