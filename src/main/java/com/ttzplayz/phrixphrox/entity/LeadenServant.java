package com.ttzplayz.phrixphrox.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LeadenServant extends PathfinderMob {

    private static final float LOOK_RANGE = 12.0F;
    private static final int MAX_TURN = 20;
    private static final float FLYING_SPEED = 0.1F;
    private static final float MOVEMENT_SPEED = 0.1F;
    private static final double MAX_HEALTH = 20.0;
    private static final float PATH_LENGTH = 48.0F;

    private static final int MIN_LOOK_DURATION = 45;
    private static final int MAX_LOOK_DURATION = 90;
    private static final float DRIFT_SPEED = 1.0F;
    private static final int CLOSE_ENOUGH_TO_LOOK_TARGET = 3;
    private static final int MIN_WAIT_DURATION = 30;
    private static final int MAX_WAIT_DURATION = 60;

    private static final Brain.Provider<LeadenServant> BRAIN_PROVIDER = Brain.provider(
            List.of(SensorType.NEAREST_LIVING_ENTITIES),
            body -> List.of(coreActivity(), idleActivity()));

    public LeadenServant(EntityType<? extends LeadenServant> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl<>(this, MAX_TURN, true);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.FLYING_SPEED, FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    private static ActivityData<LeadenServant> coreActivity() {
        return ActivityData.<LeadenServant>create(
                Activity.CORE,
                0,
                ImmutableList.of(new LookAtTargetSink(MIN_LOOK_DURATION, MAX_LOOK_DURATION), new MoveToTargetSink()));
    }

    private static ActivityData<LeadenServant> idleActivity() {
        return ActivityData.<LeadenServant>create(
                Activity.IDLE,
                0,
                ImmutableList.of(
                        SetEntityLookTargetSometimes.create(
                                EntityTypes.PLAYER, LOOK_RANGE, UniformInt.of(MIN_WAIT_DURATION, MAX_WAIT_DURATION)),
                        new RunOne<>(
                                ImmutableList.of(
                                        Pair.of(RandomStroll.fly(DRIFT_SPEED), 2),
                                        Pair.of(SetWalkTargetFromLookTarget.create(DRIFT_SPEED, CLOSE_ENOUGH_TO_LOOK_TARGET), 2),
                                        Pair.of(new DoNothing(MIN_WAIT_DURATION, MAX_WAIT_DURATION), 1)))));
    }

    @Override
    protected Brain<LeadenServant> makeBrain(Brain.Packed packedBrain) {
        return BRAIN_PROVIDER.makeBrain(this, packedBrain);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Brain<LeadenServant> getBrain() {
        return (Brain<LeadenServant>) super.getBrain();
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("leadenServantBrain");
        this.getBrain().tick(level, this);
        profiler.pop();
        super.customServerAiStep(level);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setRequiredPathLength(PATH_LENGTH);
        return navigation;
    }

    @Override
    public void travel(Vec3 input) {
        this.travelFlying(input, this.getSpeed());
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
        return false;
    }
}
