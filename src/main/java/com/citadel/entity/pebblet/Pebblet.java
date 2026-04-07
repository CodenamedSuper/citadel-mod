package com.citadel.entity.pebblet;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class Pebblet extends PathfinderMob {

    public Pebblet(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Brain.Provider<Pebblet> brainProvider() {
        return Brain.provider(PebbletAi.MEMORIES, PebbletAi.SENSORS);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return PebbletAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public Brain<Pebblet> getBrain() {
        return (Brain<Pebblet>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("pebbletBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();

        this.level().getProfiler().push("pebbletActivityUpdate");
        PebbletAi.updateActivity(this);
        this.level().getProfiler().pop();

        super.customServerAiStep();
    }

    public static AttributeSupplier.Builder createPebbletAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.22f)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.FOLLOW_RANGE, 32);
    }
}
