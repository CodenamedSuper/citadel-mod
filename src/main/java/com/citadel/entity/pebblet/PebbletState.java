package com.citadel.entity.pebblet;

import net.minecraft.util.ByIdMap;
import java.util.function.IntFunction;

public enum PebbletState {
    IDLE(0, true),
    ROLL_UP(1, false),
    ROLL(2, true),
    ROLL_OUT(3, false);

    public static final IntFunction<PebbletState> BY_ID = ByIdMap.continuous(PebbletState::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
    private final boolean canMove;

    PebbletState(int id, boolean canMove) {
        this.id = id;
        this.canMove = canMove;
    }

    public int getId() {
        return this.id;
    }

    public boolean canMove() {
        return this.canMove;
    }
}
