package com.citadel.event;

import com.citadel.Citadel;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.Random;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Citadel.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class StructureEvents {

    @SubscribeEvent
    private static void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        RegistryAccess.Frozen registryAccess = server.registryAccess();

        Registry<StructureTemplatePool> templatePools = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorLists = registryAccess.registryOrThrow(Registries.PROCESSOR_LIST);
        Registry<Structure> structures = registryAccess.registryOrThrow(Registries.STRUCTURE);

        addTargetToRandomPoolAlias(
                structures,
                ResourceLocation.withDefaultNamespace("trial_chambers"),
                ResourceLocation.withDefaultNamespace("trial_chambers/spawner/contents/small_melee"),
                ResourceLocation.fromNamespaceAndPath(Citadel.MOD_ID, "trial_chambers/spawner/small_melee/pebblet"),
                1
        );
    }

    private static void addStructureToPool(Registry<StructureTemplatePool> templatePools, Registry<StructureProcessorList> processorLists, ResourceLocation targetId, String structure, int weight) {
        StructureTemplatePool pool = templatePools.get(targetId);
        if (pool == null) return;

        ResourceLocation emptyProcessor = ResourceLocation.withDefaultNamespace("empty");
        Holder<StructureProcessorList> processorHolder = processorLists.getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, emptyProcessor));

        SinglePoolElement piece = SinglePoolElement.single(structure, processorHolder).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> pieceEntries = new ArrayList<>(pool.rawTemplates);
        pieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = pieceEntries;
    }

    private static void addTargetToRandomPoolAlias(Registry<Structure> structures, ResourceLocation structureToTarget, ResourceLocation aliasToTarget, ResourceLocation newTargetPool, int weight) {
        Structure structure = structures.get(structureToTarget);
        if (!(structure instanceof JigsawStructure jigsaw)) return;

        Citadel.LOGGER.info("Adding new target {} to pool alias {} in structure {}", newTargetPool, aliasToTarget, structureToTarget);

        var poolAliases = jigsaw.poolAliases;

        jigsaw.poolAliases = poolAliases.stream().peek(binding -> {
            if (binding instanceof Random(
                    ResourceKey<StructureTemplatePool> alias,
                    net.minecraft.util.random.SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> targets
            )) {
                if (!alias.location().equals(aliasToTarget)) return;

                Citadel.LOGGER.info("Found it :D");

                var newList = ImmutableList.<WeightedEntry.Wrapper<ResourceKey<StructureTemplatePool>>>builder()
                        .addAll(targets.items)
                        .add(new WeightedEntry.Wrapper<>(
                                ResourceKey.create(Registries.TEMPLATE_POOL, newTargetPool),
                                Weight.of(weight)
                        ));

                targets.items = newList.build();
                targets.totalWeight = WeightedRandom.getTotalWeight(targets.items);
            }
        }).toList();
    }
}
