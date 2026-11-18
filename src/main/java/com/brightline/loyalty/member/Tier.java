package com.brightline.loyalty.member;

public enum Tier {
    STANDARD(1.0), SILVER(1.5), GOLD(2.0);
    private double multiplier;
    Tier(double multplier) {
        this.multiplier = multiplier;
    }
    public double multiplier() {
        return this.multiplier;
    }
}
