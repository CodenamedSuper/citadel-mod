package com.citadel.entity.pebblet;

import com.citadel.entity.pebblet.ai.PebbletAi;
import com.mojang.serialization.Dynamic;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Pebblet extends PathfinderMob {
    public static final int ROLL_UP_ANIMATION_DURATION = 10;

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(Pebblet.class, EntityDataSerializers.INT);

    public final AnimationState rollUpAnimationState = new AnimationState();
    public final AnimationState rollOutAnimationState = new AnimationState();

    public Pebblet(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(STATE, PebbletState.IDLE.getId());
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

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            PebbletState state = this.getState();

            switch (state) {
                case ROLL_UP -> {
                    this.rollOutAnimationState.stop();
                    this.rollUpAnimationState.startIfStopped(this.tickCount);
                }
                case ROLL -> {
                    this.rollOutAnimationState.stop();
                    this.rollUpAnimationState.start(this.tickCount);
                    this.rollUpAnimationState.fastForward(ROLL_UP_ANIMATION_DURATION, 1.0f);
                }
                case ROLL_OUT -> {
                    this.rollUpAnimationState.stop();

                    this.rollOutAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        super.tick();
    }

    public Optional<LivingEntity> getHurtBy() {
        var brain = this.getBrain();
        var hurtByMemory = brain.getMemory(MemoryModuleType.HURT_BY);

        return hurtByMemory
                .map(DamageSource::getEntity)
                .filter((entity -> entity instanceof LivingEntity))
                .map(entity -> (LivingEntity) entity);
    }

    public PebbletState getState() {
        int stateId = this.entityData.get(STATE);

        return PebbletState.BY_ID.apply(stateId);
    }

    public void setState(PebbletState state) {
        this.entityData.set(STATE, state.getId());
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return this.getTargetFromBrain();
    }

    public static AttributeSupplier.Builder createPebbletAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.12f)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    public void addDe(double v, float v1, double v2) {

    }
}
