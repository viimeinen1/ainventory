package io.github.viimeinen1.ainventory.Common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Create paged map for list of values.
 */
public class PagedSlotMap <T> {
    
    public static record SlotCoordinate(int page, int slot) {};

    public final Map<SlotCoordinate, T> values = new HashMap<>();

    public PagedSlotMap(@NotNull Collection<Integer> slots, @NotNull Collection<T> values) {
        IndexStream.toStream(values).forEach(val -> {
            int page = val.i / slots.size();
            int slot = val.i % slots.size();
            this.values.put(new SlotCoordinate(page, slot), val.value);
        });
    }

}
