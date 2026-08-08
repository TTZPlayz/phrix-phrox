package com.ttzplayz.phrixphrox;

import com.ttzplayz.phrixphrox.datagen.PPBlockTagsProvider;
import com.ttzplayz.phrixphrox.datagen.PPModelProvider;
import com.ttzplayz.phrixphrox.datagen.PPRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PhrixPhrox.MOD_ID)
public class PhrixPhroxDatagen {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new PPModelProvider(packOutput));
        generator.addProvider(true, new PPBlockTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new PPRecipeProvider.Runner(packOutput, lookupProvider));
    }
}
