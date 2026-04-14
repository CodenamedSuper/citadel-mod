package com.citadel.entity.pebblet;

import com.citadel.Citadel;
import com.citadel.entity.pebblet.ai.PebbletAi;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Pebblet extends PathfinderMob {
    public static final ResourceLocation ROLL_STEP_HEIGHT_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(
            Citadel.MOD_ID,
            "roll_step_height"
    );
    public static final AttributeModifier ROLL_STEP_HEIGHT_MODIFIER = new AttributeModifier(
            ROLL_STEP_HEIGHT_MODIFIER_ID,
            1.0f,
            AttributeModifier.Operation.ADD_VALUE
    );
    public static final int ROLL_UP_ANIMATION_DURATION = 10;
    public static final int ROLLING_PARTICLES_COUNT = 30;

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(Pebblet.class, EntityDataSerializers.INT);

    public final AnimationState rollUpAnimationState = new AnimationState();
    public final AnimationState rollOutAnimationState = new AnimationState();

    private boolean friendly = false;

    public Pebblet(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);

        this.xpReward = Enemy.XP_REWARD_MEDIUM;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(STATE, PebbletState.IDLE.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putInt("state", this.getState().getId());
        compound.putBoolean("friendly", this.friendly);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        PebbletState state = PebbletState.BY_ID.apply(compound.getInt("state"));
        this.setState(state);

        this.setFriendly(compound.getBoolean("friendly"));
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

                    this.clientRollingParticles();
                }
                case ROLL_OUT -> {
                    this.rollUpAnimationState.stop();

                    this.rollOutAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        else {
            this.handleStepModifier();
        }

        super.tick();
    }

    private void handleStepModifier() {
        AttributeInstance stepHeightAttribute = this.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeightAttribute == null) return;

        if (this.getState() == PebbletState.ROLL && !stepHeightAttribute.hasModifier(ROLL_STEP_HEIGHT_MODIFIER_ID)) {
            stepHeightAttribute.addTransientModifier(ROLL_STEP_HEIGHT_MODIFIER);
        }

        if (this.getState() != PebbletState.ROLL && stepHeightAttribute.hasModifier(ROLL_STEP_HEIGHT_MODIFIER_ID)) {
            stepHeightAttribute.removeModifier(ROLL_STEP_HEIGHT_MODIFIER_ID);
        }
    }

    private void clientRollingParticles() {
        if (!this.onGround()) return;

        Vec3 deltaMovement = this.getDeltaMovement();
        if (deltaMovement.length() < 0.2d) return;

        BlockState groundState = this.getBlockStateOn();
        if (groundState.getRenderShape() == RenderShape.INVISIBLE) return;

        RandomSource random = this.getRandom();

        Vec3 particleOrigin = this.blockPosition().getBottomCenter();
        Vec3 horizontalParticleDelta = new Vec3(
                deltaMovement.x,
                0,
                deltaMovement.z
        ).normalize().scale(-0.2);

        for (int i = 0; i < ROLLING_PARTICLES_COUNT; i++) {
            this.level().addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, groundState),
                    particleOrigin.x + Mth.randomBetween(random, -0.5f, 0.5f),
                    particleOrigin.y,
                    particleOrigin.z + Mth.randomBetween(random, -0.5f, 0.5f),
                    horizontalParticleDelta.x,
                    0.2f,
                    horizontalParticleDelta.z
            );
        }
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

    public boolean isFriendly() {
        return this.friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return this.getTargetFromBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();

        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player) {
            if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            return !this.isFriendly();
        }
        if (target instanceof Enemy) {
            return this.isFriendly();
        }
        return false;
    }

    public static AttributeSupplier.Builder createPebbletAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.12f)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    public static boolean checkPebbletSpawnRules(EntityType<Pebblet> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (spawnType == MobSpawnType.TRIAL_SPAWNER) {
            return true;
        }

        if (pos.getY() >= level.getSeaLevel()) {
            return false;
        }

        int maxLocalRawBrightness = level.getMaxLocalRawBrightness(pos);

        return maxLocalRawBrightness <= random.nextInt(4) && checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }
}
