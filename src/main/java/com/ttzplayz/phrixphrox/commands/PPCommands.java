package com.ttzplayz.phrixphrox.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.ttzplayz.phrixphrox.curse.CurseEnforcement;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

public class PPCommands {

    private static final String TARGET = "target";
    private static final String CURSE = "curse";

    private static final DynamicCommandExceptionType UNKNOWN_CURSE =
            new DynamicCommandExceptionType(name -> Component.literal("Unknown curse: " + name));

    private static final SuggestionProvider<CommandSourceStack> CURSE_NAMES = (context, builder) ->
            SharedSuggestionProvider.suggest(
                    Arrays.stream(CurseInstance.Curse.values())
                            .filter(CurseInstance.Curse::selectable)
                            .map(CurseInstance.Curse::path), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("curse")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument(TARGET, EntityArgument.player())
                        .then(Commands.argument(CURSE, StringArgumentType.word())
                                .suggests(CURSE_NAMES)
                                .executes(PPCommands::inflict))));

        dispatcher.register(Commands.literal("decurse")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument(TARGET, EntityArgument.player())
                        .then(Commands.argument(CURSE, StringArgumentType.word())
                                .suggests(CURSE_NAMES)
                                .executes(PPCommands::lift))));
    }

    private static int inflict(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer target = EntityArgument.getPlayer(context, TARGET);
        CurseInstance.Curse kind = curseArgument(context);

        CurseEnforcement.inflict(source.getLevel(), target, kind, source.getPlayer());

        source.sendSuccess(() -> Component.literal("Cursed ")
                .append(target.getDisplayName())
                .append(" with ")
                .append(Component.translatable(kind.nameKey()).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    private static int lift(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer target = EntityArgument.getPlayer(context, TARGET);
        CurseInstance.Curse kind = curseArgument(context);

        int lifted = CurseEnforcement.liftCurses(source.getLevel(), target, kind);

        if (lifted == 0) {
            source.sendFailure(Component.literal("")
                    .append(target.getDisplayName())
                    .append(" is not afflicted by ")
                    .append(Component.translatable(kind.nameKey())));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Lifted " + lifted + " x ")
                .append(Component.translatable(kind.nameKey()).withStyle(ChatFormatting.GOLD))
                .append(" from ")
                .append(target.getDisplayName()), true);
        return lifted;
    }

    private static CurseInstance.Curse curseArgument(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, CURSE);
        CurseInstance.Curse kind = CurseInstance.Curse.byPath(name);
        if (kind == null || !kind.selectable()) throw UNKNOWN_CURSE.create(name);
        return kind;
    }
}
