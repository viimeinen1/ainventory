package io.github.viimeinen1.ainventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.jetbrains.annotations.ApiStatus.Internal;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Custom inventory with addinational features like reloading individual slots.
 */
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
     * Default click actions that are run every time inventory is clicked.
     */
    @FunctionalInterface
    public static interface defaultClickAction {
        void run(InventoryClickEvent event);
    }

    /**
     * Inventory initialization function.
     */
    @FunctionalInterface
    public static interface inventoryInitializationFunction {
        void run(aInventory customInventory);
    }

    /**
     * Single item reload function
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

    /**
     * Messages of inventory.
     */
    public final class Messages {
        public static final String NO_OPEN_PERMISSION = "<red>You don't have permission to open this inventory!";
        public static final String NO_USE_PERMISSION = "<red>You don't have permission to use this inventory!";
    }

    /**
     * {@link Inventory} of this aInventory.
     * 
     * Modify this if more control is needed.
     */
    public final Inventory inventory;

    /**
     * Owner of the inventory.
     * 
     * If set, no other player can open or use the inventory.
     */
    public UUID owner;

    private final Map<Integer, guiItemClickEvent> slotFunctions = new HashMap<>();
    private final Map<Integer, itemReloadFunction> itemReloads = new HashMap<>();
    private inventoryInitializationFunction initialization;
    private defaultClickAction defaultClickAction;
    private guiRequirementFunction requirementFunction;

    /**
     * Get {@link Inventory}
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Open inventory for player.
     * 
     * Will send {@link Messages#NO_OPEN_PERMISSION} if no permission to open inventory.
     * 
     * Permission will be determined by:
     * - Checking if player {@link UUID} matches the owner of the inventory.
     * - Running {@link guiRequirementFunction} linked to this inventory.
     * 
     * Will also reload the inventory before it's openend.
     * 
     * @param player Who the inventory will be opened to.
     * @return true if inventory was opened for player.
     */
    public boolean openInventory(@NotNull HumanEntity player) {
        if (owner != null && !owner.equals(player.getUniqueId())) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_OPEN_PERMISSION));
            return false;
        }
        if (requirementFunction != null && !requirementFunction.run(player)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_OPEN_PERMISSION));
            return false;
        }
        reload(); // reload inventory
        player.openInventory(inventory);
        return true;
    }

    /**
     * Re-Initialize inventory.
     * 
     * Will:
     * - clear inventory.
     * - run initialization function again.
     * - update inventory to all it's viewers.
     * 
     * @return {@link aInventory}
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
     * Runs reload function of all slots in the inventory.
     * 
     * @return {@link aInventory}
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
     * @return {@link aInventory}
     */
    public aInventory reload(@NotNull Integer slot) {
        itemReloads.get(slot).run(new aItemBuilder(this, slot));
        return this;
    }

    @Internal
    public aInventory onInventoryClick(@NotNull InventoryClickEvent event) {
        if (requirementFunction != null && !requirementFunction.run(event.getWhoClicked())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_USE_PERMISSION));
            return this;
        }

        if (defaultClickAction != null) {
            defaultClickAction.run(event);
        }

        if (slotFunctions.containsKey(event.getSlot()) && slotFunctions.get(event.getSlot()) != null) {
            slotFunctions.get(event.getSlot()).run(event);
        }

        return this;
    }

    @Internal
    public aInventory onInventoryTransfer(@NotNull InventoryClickEvent event) {
        if (requirementFunction != null && !requirementFunction.run(event.getWhoClicked())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(MiniMessage.miniMessage().deserialize(Messages.NO_USE_PERMISSION));
            return this;
        }

        if (defaultClickAction != null) {
            defaultClickAction.run(event);
        }

        return this;
    }

    /**
     * Update inventory for all it's viewers.
     * 
     * @return {@link aInventory}
     */
    public aInventory updateInventory() {
        this.inventory.getViewers().forEach(entity -> {
            if (entity instanceof Player pl) {
                pl.updateInventory();
            }
        });
        return this;
    }

    /**
     * Create new {@link aDefaultInventoryBuilder} for building new inventory.
     * 
     * @return new {@link aDefaultInventoryBuilder}
     */
    public static aDefaultInventoryBuilder InventoryBuilder() {
        return new aDefaultInventoryBuilder();
    }

    /**
     * Create new {@link aInventory}.
     * 
     * Prefer {@link aInventoryBuilder#build()} to build new inventory.
     * 
     * @param builder extends {@link aInventoryBuilder}
     */
    @Internal
    public aInventory(aInventoryBuilder<?, ?> builder) {
        this.owner = builder.owner;
        this.defaultClickAction = builder.defaultClickAction;
        this.initialization = builder.initialization;
        this.requirementFunction = builder.requirementFunction;

        if (builder.size == null) {
            this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE.CHEST_9x3.size(), builder.title);
        } else {
            this.inventory = Bukkit.createInventory(this, builder.size, builder.title);
        }

        initialize();
        updateInventory(); // just in case someone has the inv open for some unforseen reason
    }

    /**
     * Default inventory builder.
     * 
     * Use {@link aGUIInventory} for GUI inventories.
     */
    public static class aDefaultInventoryBuilder extends aInventoryBuilder<aDefaultInventoryBuilder, aInventory> {

        /**
         * Get the builder.
         */
        @Override
        public aDefaultInventoryBuilder getThis() {
            return this;
        }

        /**
         * Build new {@link aInventory} with the set details.
         * 
         * Will also initialize the inventory.
         * 
         * @return new {@link aInventory}
         */
        @Override
        public aInventory build() {
            return new aInventory(this);
        }

    }

    /**
     * Custom inventory builder.
     */
    public static abstract class aInventoryBuilder <T extends aInventoryBuilder<T, K>, K extends aInventory> {
        UUID owner;
        inventoryInitializationFunction initialization;
        defaultClickAction defaultClickAction;
        Integer size;
        Component title;
        guiRequirementFunction requirementFunction;

        // for subclasses to override
        public abstract T getThis();
        public abstract K build();

        /**
         * Set initialization function for the inventory.
         * 
         * The function can be called later with {@link aInventory#initialize()}.
         * 
         * @param fn {@link inventoryInitializationFunction}
         * @return builder
         */
        public T initialization(inventoryInitializationFunction fn) {
            this.initialization = fn;
            return getThis();
        }

        /**
         * Function that will be ran every time inventory is clicked.
         * 
         * @param fn {@link defaultClickAction}
         * @return builder
         */
        public T defaultAction(@NotNull defaultClickAction fn) {
            this.defaultClickAction = fn;
            return getThis();
        }

        /**
         * Set size of the inventory.
         * 
         * @param size {@link INVENTORY_SIZE}
         * @return builder
         */
        public T size(INVENTORY_SIZE size) {
            this.size = size.size();
            return getThis();
        }

        /**
         * Set the title of the inventory.
         * 
         * Accepts both {@link String} and {@link Component}.
         * Giving object with other types will fail silently.
         * 
         * If {@link String}, the string will be deserialized as minimessage.
         * 
         * @param <R> {@link String} or {@link Component}
         * @param title title that will be set as the inventory title.
         * @return builder
         */
        public <R> T title(R title) {
            switch (title) {
                case Component component -> {
                    this.title = component;
                }
                case String txt -> {
                    this.title = MiniMessage.miniMessage().deserialize(txt);
                }
                default -> {}
            }
            return getThis();
        }

        /**
         * Set owner of the inventory.
         * 
         * Setting owner of the inventory will restrict it's usage to only it's owner.
         * 
         * @param owner {@link UUID} of the owner.
         * @return builder
         */
        public T owner(UUID owner) {
            this.owner = owner;
            return getThis();
        }

        /**
         * Function that is ran every time inventory is opened, or slot is clicked.
         * 
         * If function returns false, all futher execution is blocked.
         * 
         * @param requirementFunction {@link guiRequirementFunction}
         */
        public T require(guiRequirementFunction requirementFunction) {
            this.requirementFunction = requirementFunction;
            return getThis();
        }
    }

    /**
     * Modify slot.
     * 
     * @param slot slot number
     * @return new item builder
     */
    public aItemBuilder ItemBuilder(@NotNull int slot) {
        return new aItemBuilder(this, slot);
    }

    /**
     * Modify multiple slots.
     * 
     * All previous parameters will be taken from the first item in the list and copied to all slots. (name, lore, amount, material etc.)
     * 
     * @param slots slot numbers
     * @return new item builder
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
     * Item builder for {@link aInventory}. Supports reload functions.
     */
    public static class aItemBuilder {
        aInventory inventory;
        guiItemClickEvent slotFn;
        ItemStack item;
        Set<Integer> slots = new HashSet<>();
        itemReloadFunction reloadFn;
        boolean removeReloadFunction = false;
        boolean removeSlotFuntion = false;

        /**
         * Modify single slot.
         * 
         * If slot already exists, the item will copy it's values.
         * 
         * Prefer {@link aInventory#ItemBuilder(int)}
         * 
         * @param inventory Inventory that the item(s) will be set to.
         * @param slot slot number
         * @return builder
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
        }

        /**
         * Modify multiple slots.
         * 
         * All previous parameters will be taken from the first item in the list.
         * 
         * Prefer {@link aInventory#ItemBuilder(Collection)}
         * 
         * @param inventory Inventory that the item(s) will be set to.
         * @param slot slot numbers
         * @return builder
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
        }

        /**
         * Add slot that the item(s) will be copied to.
         * 
         * @param slot slot number
         * @return builder
         */
        public aItemBuilder addSlot(@NotNull Integer slot) {
            this.slots.add(slot);
            return this;
        }

        /**
         * Add multiple slots that this item(s) will be copied to.
         * 
         * @param slot slot numbers
         * @return builder
         */
        public aItemBuilder addSlot(@NotNull Collection<Integer> slots) {
            this.slots.addAll(slots);
            return this;
        }

        /**
         * Replace item in the builder.
         * 
         * This will discard all item related values added before.
         * 
         * @param item {@link ItemStack}
         * @return builder
         */
        public aItemBuilder setItem(@NotNull ItemStack item) {
            this.item = item.clone();
            return this;
        }

        /**
         * Get {@link aInventory} the item(s) will be set to.
         * 
         * @return the {@link aInventory} the items will be set to.
         */
        public aInventory getInventory() {
            return inventory;
        }

         /**
          * Get slots that item(s) will be copied to.
          * 
          * @return {@link List} of slot numbers
          */
        public Set<Integer> getSlots() {
            return slots;
        }

        /**
         * If slot Function should be removed.
         * 
         * Will be ignored if new slot function is added.
         * 
         * @param removeSlotFuntion boolean if slot function should be removed.
         * @return builder
         */
        public aItemBuilder removeSlotFuntion(boolean removeSlotFuntion) {
            this.removeSlotFuntion = removeSlotFuntion;
            return this;
        }

        /**
         * If reload function should be removed.
         * 
         * Will be ignored if new reload function is added.
         * 
         * @param removeReloadFunction boolean if reload function should be removed.
         * @return builder
         */
        public aItemBuilder removeReloadFunction(boolean removeReloadFunction) {
            this.removeReloadFunction = removeReloadFunction;
            return this;
        }

        /**
         * Set the type of the item(s).
         * 
         * @param material new {@link Material} for item(s).
         * @return builder
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
         * Set amount for item(s).
         * 
         * @param amount amount for item(s).
         * @return builder
         */
        public aItemBuilder amount(@NotNull Integer amount) {
            this.item.setAmount(amount);
            return this;
        }

        /**
         * Clear item(s) completely.
         * Material will be set to AIR.
         * 
         * @return builder
         */
        public aItemBuilder clear() {
            item = ItemStack.of(Material.AIR);
            slotFn = null;
            reloadFn = null;
            removeReloadFunction = true;
            removeSlotFuntion = true;
            return this;
        }

        /**
         * Reload function.
         * The function will be called every time inventory is opened.
         * 
         * Using null will cause the function to not change.
         * Use {@link ItemBuilder#removeReloadFunction(boolean)} to remove reload function.
         * 
         * @param fn reload function
         * @return builder
         */
        public aItemBuilder reload(@Nullable itemReloadFunction fn) {
            this.reloadFn = fn;
            return this;
        }

        /**
         * Set displayname of item(s).
         * Using null will retain old displayname.
         * 
         * Accepts both {@link String} and {@link Component}.
         * Giving object with other types will fail silently.
         * 
         * If {@link String}, the string will be deserialized as minimessage.
         * 
         * @param <R> {@link String} or {@link Component}
         * @param name displayname of item(s)
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
         * Set lore of item(s).
         * Using null will do nothing.
         * 
         * Accepts both {@link String} and {@link Component}.
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
         * Function that is run when item(s) is clicked.
         * 
         * Using null will keep original function.
         * Use {@link ItemBuilder#removeSlotFuntion(boolean)} to remove slot function.
         * 
         * @param slotFn {@link guiItemClickEvent}
         * @return builder
         */
        public aItemBuilder function(@Nullable guiItemClickEvent slotFn) {
            this.slotFn = slotFn;
            return this;
        }

        /**
         * Set data component to item(s).
         * 
         * @param type component type
         * @param value component value
         * @return builder
         */
        public <K> aItemBuilder setData(@NotNull DataComponentType.Valued<K> type, @NotNull K value) {
            item.setData(type, value);
            return this;
        }

        /**
         * Apply item(s) to linked inventory.
         * If not called, all changes to builder will be discarded.
         * 
         * @return {@link aInventory} that the item(s) were set to.
         */
        public aInventory build() {
            inventory.buildItem(this);
            return inventory;
        }
    }

    /**
     * Initialize listener for aInventory.
     * Without initializing the listener, the click functions will not work.
     * 
     * @param plugin {@link JavaPlugin} that the listener will be listed for.
     */
    public static void initializeListener(@NotNull JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new aInventoryListener(), plugin);
    }

}
