package com.citadel.entity.pebblet;

import net.minecraft.util.ByIdMap;
import java.util.function.IntFunction;

public enum PebbletState {
    IDLE(0),
    ROLL_UP(1),
    ROLL(2),
    ROLL_OUT(3);

    public static final IntFunction<PebbletState> BY_ID = ByIdMap.continuous(PebbletState::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;

    PebbletState(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
