# aInventory
Inventory GUI library with focus on having the whole configuration on one file (instead of multiple).

The library was made for usage in my own plugins, so some features may be missing.

Plan is to add more functions as I requre them (like animations, pages, or other stuff).

## Add as dependency

To use the library in your project, add it as a dependency.

The dependency can be seen at https://central.sonatype.com/artifact/io.github.viimeinen1.ainventory/aInventory

build.gradle.kts
```java
dependencies (
    implementation("io.github.viimeinen1.ainventory:aInventory:1.0.1")
)
```

## Usage

Creating single inventory
```java

// basic usage

int counter = 0;

aInventory ainventory = aInventory.builder() // create new builder
    .size(INVENTORY_SIZE.CHEST_9x1) // set inventory size to 9x1
    .title("<gold>Example inventory") // set inventory title (also accepts components)
    .require(player -> player.isOp() || player.hasPermission("example.admin")) // require player to be OP or have permission "example.admin" to open or use inventory.
    .defaultAction(event -> event.setCancelled(true)) // cancel action every time inventory is clicked (or something is moved to it)
    .initialization(inventory -> { // create initialization function

        // Quit item
        inventory.ItemBuilder(0) // create new ItemBuilder to slot 0
            .material(Material.IRON_DOOR) // set material of item
            .amount(30) //set item amount
            .name("<red>QUIT", false) // set name to QUIT (with no italics)
            .lore(List.of( // add lore
                "",
                "<red>Close inventory",
                ""
            ), false) // no italics
            .setData( // enchant with mending 3
                DataComponentTypes.ENCHANTMENTS, 
                ItemEnchantments.itemEnchantments(Map.of(Enchantment.MENDING, 3))
            )
            .setData( // hide enchants from tooltip
                DataComponentTypes.TOOLTIP_DISPLAY, 
                TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.ENCHANTMENTS).build()
            )
            .function(event -> { // create function that gets run when item is clicked
                // no need to cancel event, because it was done in defaultActions
                Bukkit.getScheduler().runTask(null, () -> event.getWhoClicked().closeInventory()); // close player's inventory on next tick
            })
            .build(); // add built item(s) to inventory

        // Counter
        inventory.ItemBuilder(1) // create itembuilder for slot 1
            .material(Material.GREEN_CONCRETE)
            .lore(List.of(
                "",
                Component.text("click here to increase count"),
                ""
            ), false)
            .function(event -> {
                counter++; // add 1 to counter
                inventory.reload(1); // reload this item. Will also update inventory for it's viewers.
            })
            .reload(builder -> { // specify reload function
                // instead of setting name before, we specify it in reload function
                // that way the name gets reloaded every time inventory is opened, or when the slot is reloaded manually
                builder.name("Counter: " + count, false) // set name
                    .build(); // apply changes
            })
            .build(); // apply item to inventory

        // Items that gets removed on click
        inventory.ItemBuilder(List.of(2, 3, 4, 5, 6, 7)) // specify multiple places for item
            .addSlot(8) // add more items
            .name("<yellow>Click to remove")
            .function(event -> {
                inventory.ItemBuilder(event.getSlot()) // create new ItemBuilder for this slot
                    //.removeReloadFunction(true) // this would remove reload funtion
                    //.removeSlotFuntion(true) // this would remove click function
                    .clear() // clear the item. Will also clear reload and click functions
                    .build(); // apply changes to the inventory

                inventory.update(); // update inventory for it's viewers to show changes
            })
    })
    .build(); // create the inventory

// using inventory
ainventory.openInventory(player); // open inventory for player (will also reload inventory)

ainventory.reload() // reload inventory manually

ainventory.initialize() // run initialization function again (where we set the items)


// alternatively we can create items while not in initialization function, 
// but these can't be recreated when .initialize() is called
ainventory.ItemBuilder(0) // will make a copy of the item in slot 0
    .clear() // clear required since we already have item in the slot
    .material(Material.RED_CONCRETE)
    .build(); // apply changes

// aInventory also exposes the following values for more robust modification:
ainventory.inventory // -> [Inventory] get the underlying inventory for modification
ainventory.clickFunctions // -> [Map<Integer, itemClickEvent>] map of click functions
ainventory.reloadFunctions // -> Map<Integer, itemReloadFunction>] map of reload functions
```

Creating GUIs
```java

// basic aGUI usage

public enum GUI_INVENTORY { // all gui inventories
    MAIN,
    CONFIRM_EXIT
    // etc
}

aGUI<GUI_INVENTORY> agui = new aGUI<>(GUI_INVENTORY.class); // create new gui

agui.putInventory(aGUIInventory.builder(GUI_INVENTORY.MAIN) // define MAIN inventory to the gui
    .size(INVENTORY_SIZE.CHEST_9x3)
    .initialization(inventory -> {
        // initialization of items here
    })
    .build()); // create inventory


// another way of defining inventories
aGUIInventory confirmExitInventory = aGUIInventory.builder(GUI_INVENTORY.CONFIRM_EXIT) // create CONFIRM_EXIT inventory
    // other inventory settings
    .build();

agui.putInventory(confirmExitInventory); // put inventory to gui

// now that we have both inventories, we can start using the gui
agui.openInventory(GUI_INVENTORY.MAIN, player); // open inventory for player
agui.reloadInventory(GUI_INVENTORY.MAIN); // reload inventory
agio.initializeInventory(GUI_INVENTORY.CONFIRM_EXIT); // re-run initialization logic


// to open other gui inventories from inside the gui, do it in functions for items

// ...
.function(event -> Bukkit.getScheduler().runTask(plugin, () -> gui.openInventory(GUI_INENTORY.CONFIRM_EXIT, event.getWhoClicked())))
// ...
```