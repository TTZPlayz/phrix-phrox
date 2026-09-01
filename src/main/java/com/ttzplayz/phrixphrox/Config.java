package com.ttzplayz.phrixphrox;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define(
        "logDirtBlock",
        true
    );

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment(
        "What you want the introduction message to be for the magic number"
    ).define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment(
        "A list of items to log on common setup."
    ).defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    public static final ModConfigSpec.DoubleValue BLUNDER_STRIKE_CHANCE = BUILDER.comment(
        "Chance that a Blunder-Strike attack lands on the cursed player instead of their target"
    ).defineInRange("blunderStrikeChance", 0.20, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue BLUNDER_STRIKE_CHANCE_ESCALATED = BUILDER.comment(
        "The same chance once the curse has worsened"
    ).defineInRange("blunderStrikeChanceEscalated", 0.50, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue BLUNDER_STRIKE_DAMAGE = BUILDER.comment(
        "Damage a Blunder-Strike deals to the cursed player"
    ).defineInRange("blunderStrikeDamage", 2.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue RUNE_COMPLETION = BUILDER.comment(
        "Fraction of the rune that must be carved before a tablet counts as inscribed"
    ).defineInRange("runeCompletion", 0.97, 0.1, 1.0);

    public static final ModConfigSpec.BooleanValue EASY_CURSING = BUILDER.comment(
        "Allow a name tag renamed to a player as a defixion focus, instead of that player's head"
    ).define("EASY_CURSING", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName));
    }
}
