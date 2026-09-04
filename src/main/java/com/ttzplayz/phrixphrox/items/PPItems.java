package com.ttzplayz.phrixphrox.items;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import java.util.Arrays;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
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

    public static final DeferredItem<Item> PEWTER_UPGRADE_TEMPLATE = ITEMS.registerItem("pewter_upgrade_template",
            properties -> createPewterUpgradeTemplate(properties.rarity(Rarity.UNCOMMON)));

    private static SmithingTemplateItem createPewterUpgradeTemplate(Item.Properties properties) {
        return new SmithingTemplateItem(
            Component.translatable("item.phrixphrox.smithing_template.pewter_upgrade.applies_to").withStyle(ChatFormatting.BLUE),
            Component.translatable("item.phrixphrox.smithing_template.pewter_upgrade.ingredients").withStyle(ChatFormatting.BLUE),
            Component.translatable("item.phrixphrox.smithing_template.pewter_upgrade.base_slot_description"),
            Component.translatable("item.phrixphrox.smithing_template.pewter_upgrade.additions_slot_description"),

            List.of(
                Identifier.withDefaultNamespace("container/slot/sword"),
                Identifier.withDefaultNamespace("container/slot/pickaxe"),
                Identifier.withDefaultNamespace("container/slot/axe"),
                Identifier.withDefaultNamespace("container/slot/shovel"),
                Identifier.withDefaultNamespace("container/slot/hoe"),
                Identifier.withDefaultNamespace("container/slot/spear")
            ),
            List.of(Identifier.withDefaultNamespace("container/slot/ingot")),
            properties
        );
    }



    // CURSING
    public static final DeferredItem<Item> BOUND_DEFIXION = ITEMS.registerItem("bound_defixion", (properties) -> new BoundDefixion(properties));
    public static final DeferredItem<Item> CURSED_STYLUS = ITEMS.registerSimpleItem("cursed_stylus");
    public static final DeferredItem<Item> DEFIXION = ITEMS.registerItem("defixion", (properties) -> new Defixion(properties));
    public static final DeferredItem<Item> LEAD_TABLET = ITEMS.registerSimpleItem("lead_tablet");
    public static final DeferredItem<Item> LEADEN_FEATHER = ITEMS.registerSimpleItem("leaden_feather");

    public static final List<DeferredItem<Item>> ALL_ITEMS = Arrays.asList(



        CURSED_STYLUS,
        LEAD_TABLET,
        DEFIXION,
        BOUND_DEFIXION,

        LEAD_SCRAP,
        LEAD_INGOT,
        PLUMBOUS_MIXTURE,
        PEWTER_INGOT,


        PEWTER_SWORD,
        PEWTER_AXE,
        PEWTER_PICKAXE,
        PEWTER_SHOVEL,
        PEWTER_HOE,
        PEWTER_SPEAR,

        PEWTER_UPGRADE_TEMPLATE
    );

    public static final List<DeferredItem<Item>> FLAT_ITEMS = Arrays.asList(
        DEFIXION,
        BOUND_DEFIXION,
        CURSED_STYLUS,
        LEAD_TABLET,

        LEAD_INGOT,
        LEAD_SCRAP,
        PEWTER_INGOT,
        PLUMBOUS_MIXTURE,

        PEWTER_UPGRADE_TEMPLATE,
        LEADEN_FEATHER
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
