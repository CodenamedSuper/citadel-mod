package com.citadel.entity.pebblet.ai.behavior;

import com.citadel.entity.pebblet.Pebblet;
import com.citadel.entity.pebblet.PebbletState;
import com.citadel.registry.CitadelMemoryModuleTypes;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class RollTowardsTarget extends Behavior<Pebblet> {
    private Vec3 knockbackDir = Vec3.ZERO;

    public RollTowardsTarget() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                CitadelMemoryModuleTypes.ROLL_TARGET.get(), MemoryStatus.VALUE_PRESENT,
                CitadelMemoryModuleTypes.ROLL_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT
        ), 100);
    }

    @Override
    protected void start(ServerLevel level, Pebblet entity, long gameTime) {
        Brain<Pebblet> brain = entity.getBrain();

        Optional<Vec3> rollTargetMemory = brain.getMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get());
        if (rollTargetMemory.isEmpty()) return;

        Vec3 entityPos = entity.position();
        Vec3 rollPos = rollTargetMemory.get();

        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(
                rollPos,
                5.0f,
                0
        ));
        this.knockbackDir = new Vec3(entityPos.x - rollPos.x, 0.0f, entityPos.z - rollPos.z).normalize();
    }

    @Override
    protected void tick(ServerLevel level, Pebblet owner, long gameTime) {
        Brain<Pebblet> brain = owner.getBrain();

        this.tryHurtCollidingEntities(level, owner);

        if (this.canFinishRolling(brain)) {
            this.finishRolling(brain);
        }
    }

    private void tryHurtCollidingEntities(ServerLevel level, Pebblet owner) {
        List<LivingEntity> collidingEntities = level.getNearbyEntities(
                LivingEntity.class,
                TargetingConditions.forNonCombat(),
                owner,
                owner.getBoundingBox().inflate(0.5f)
        );

        if (!collidingEntities.isEmpty()) {
            LivingEntity entity = collidingEntities.getFirst();
            if (entity instanceof Pebblet) return;

            DamageSource damageSource = level.damageSources().mobAttack(owner);

            if (entity.hurt(damageSource, (float) owner.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                EnchantmentHelper.doPostAttackEffects(level, owner, damageSource);
            }

            float knockbackForce = Mth.clamp(owner.getSpeed() * 1.5f, 0.2f, 3.0f);
            float blockedFactor = entity.isDamageSourceBlocked(level.damageSources().mobAttack(owner)) ? 0.5f : 1.0f;

            entity.knockback(blockedFactor * knockbackForce, this.knockbackDir.x, this.knockbackDir.z);
        }
    }

    private boolean canFinishRolling(Brain<Pebblet> brain) {
        Optional<WalkTarget> walkTargetMemory = brain.getMemory(MemoryModuleType.WALK_TARGET);
        Optional<Vec3> rollTargetMemory = brain.getMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get());

        return walkTargetMemory.isEmpty() || rollTargetMemory.isEmpty();
    }

    private void finishRolling(Brain<Pebblet> brain) {
        brain.eraseMemory(CitadelMemoryModuleTypes.ROLL_TARGET.get());
        brain.setMemoryWithExpiry(CitadelMemoryModuleTypes.ROLL_COOLDOWN.get(), Unit.INSTANCE, 30L);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Pebblet entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(CitadelMemoryModuleTypes.ROLL_TARGET.get());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Pebblet owner) {
        return owner.getState() == PebbletState.ROLL;
    }
}
