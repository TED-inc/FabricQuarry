package net.quarrymod.blockentity.machine.tier3;

public enum ExcavationState {
    InProgress,
    Complete,

    NoEnergyIncome,

    NoOresInCurrentDepth,
    NoOreInCurrentPos,

    CannotOutputMineDrop,
    CannotOutputDrillTube,
    NotEnoughDrillTube;

    public boolean isError() {
        return ordinal() >= CannotOutputMineDrop.ordinal();
    }
}
