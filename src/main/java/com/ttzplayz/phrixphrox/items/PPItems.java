package com.ttzplayz.phrixphrox.items;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.ttzplayz.phrixphrox.items.PPToolTiers.PEWTER;

public class PPItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PhrixPhrox.MOD_ID);
    // ORES
    public static final DeferredItem<Item> LEAD_SCRAP = ITEMS.registerSimpleItem("lead_scrap");
    public static final DeferredItem<Item> PEWTER_INGOT = ITEMS.registerSimpleItem("pewter_ingot");
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");
    public static final DeferredItem<Item> PLUMBOUS_MIXTURE = ITEMS.registerSimpleItem("plumbous_mixture");
    // PEWTER SET
    public static final DeferredItem<Item> PEWTER_SWORD = ITEMS.registerItem("pewter_sabre", properties -> new Item(properties.sword(PEWTER, 3, -2.4f)));
    public static final DeferredItem<Item> PEWTER_AXE = ITEMS.registerItem("pewter_cleaver", properties -> new AxeItem(PEWTER, 6, -3.2f, properties));
    public static final DeferredItem<Item> PEWTER_PICKAXE = ITEMS.registerItem("pewter_mattock", properties -> new Item(properties.pickaxe(PEWTER, 1, -2.8f)));
    public static final DeferredItem<Item> PEWTER_SHOVEL = ITEMS.registerItem("pewter_spade", properties -> new ShovelItem(PEWTER, 1.5f, -3.0f, properties));
    public static final DeferredItem<Item> PEWTER_HOE = ITEMS.registerItem("pewter_sickle", properties -> new HoeItem(PEWTER, 2, -3.0f, properties)); //extra damage bc scythe :)
    public static final DeferredItem<Item> PEWTER_SPEAR = ITEMS.registerItem("pewter_skewer", properties -> new Item(properties.spear(PEWTER, 0.95f, 0.7f, 0.7f,
            3.5f, 13f, 8.5f, 5.1f, 13.37f, 4.67f))); //edit once we figure out what values mean

    // CURSING
    public static final DeferredItem<Item> BOUND_DEFIXION = ITEMS.registerSimpleItem("bound_defixion");
    public static final DeferredItem<Item> CURSED_STYLUS = ITEMS.registerSimpleItem("cursed_stylus");
    public static final DeferredItem<Item> DEFIXION = ITEMS.registerSimpleItem("defixion");
    public static final DeferredItem<Item> LEAD_TABLET = ITEMS.registerSimpleItem("lead_tablet");

    public static final List<DeferredItem<Item>> ALL_ITEMS = Arrays.asList(
        BOUND_DEFIXION,
        CURSED_STYLUS,
        DEFIXION,
        LEAD_TABLET,

        LEAD_INGOT,
        LEAD_SCRAP,
        PEWTER_INGOT,
        PLUMBOUS_MIXTURE,

        PEWTER_SWORD,
        PEWTER_AXE,
        PEWTER_PICKAXE,
        PEWTER_SHOVEL,
        PEWTER_HOE,
        PEWTER_SPEAR
    );

    public static final List<DeferredItem<Item>> FLAT_ITEMS = Arrays.asList(
        DEFIXION,
        BOUND_DEFIXION,
        CURSED_STYLUS,
        LEAD_TABLET,

        LEAD_INGOT,
        LEAD_SCRAP,
        PEWTER_INGOT,
        PLUMBOUS_MIXTURE
    );

    public static final List<DeferredItem<Item>> TOOL_ITEMS = Arrays.asList(
        PEWTER_SWORD,
        PEWTER_AXE,
        PEWTER_PICKAXE,
        PEWTER_SHOVEL,
        PEWTER_HOE,
        PEWTER_SPEAR
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
