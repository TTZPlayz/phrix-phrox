package com.ttzplayz.phrixphrox.items;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PhrixPhrox.MOD_ID);

    public static final DeferredItem<Item> BOUND_DEFIXION = ITEMS.registerSimpleItem("bound_defixion");
    public static final DeferredItem<Item> CURSED_STYLUS = ITEMS.registerSimpleItem("cursed_stylus");
    public static final DeferredItem<Item> DEFIXION = ITEMS.registerSimpleItem("defixion");
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");
    public static final DeferredItem<Item> LEAD_SCRAP = ITEMS.registerSimpleItem("lead_scrap");
    public static final DeferredItem<Item> LEAD_TABLET = ITEMS.registerSimpleItem("lead_tablet");
    public static final DeferredItem<Item> PEWTER_INGOT = ITEMS.registerSimpleItem("pewter_ingot");
    public static final DeferredItem<Item> PEWTER_SABRE = ITEMS.registerSimpleItem("pewter_sabre");
    public static final DeferredItem<Item> PLUMBOUS_MIXTURE = ITEMS.registerSimpleItem("plumbous_mixture");

    public static final List<DeferredItem<Item>> ALL_ITEMS = Arrays.asList(
        BOUND_DEFIXION,
        CURSED_STYLUS,
        DEFIXION,
        LEAD_INGOT,
        LEAD_SCRAP,
        LEAD_TABLET,
        PEWTER_INGOT,
        PEWTER_SABRE,
        PLUMBOUS_MIXTURE
    );

    public static final List<DeferredItem<Item>> FLAT_ITEMS = Arrays.asList(
        BOUND_DEFIXION,
        DEFIXION,
        LEAD_INGOT,
        LEAD_SCRAP,
        LEAD_TABLET,
        PEWTER_INGOT,
        PLUMBOUS_MIXTURE
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
