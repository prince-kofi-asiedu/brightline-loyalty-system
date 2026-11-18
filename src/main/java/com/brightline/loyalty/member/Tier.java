package com.brightline.loyalty.member;

import java.math.BigDecimal;

public enum Tier {
    STANDARD("1"), SILVER("1.5"), GOLD("2");
    private final BigDecimal multiplier;

    Tier(String multiplier) {
        this.multiplier = new BigDecimal(multiplier);
    }

    public BigDecimal multiplier() {
        return this.multiplier;
    }
}
