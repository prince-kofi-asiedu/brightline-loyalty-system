package com.brightline.loyalty;

import java.lang.ArithmeticException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;
import com.brightline.loyalty.member.Tier;
import com.brightline.loyalty.PointsAward;

public class PointsCalculator {

    private static final BigDecimal BASE_POINT_DENOMINATOR = BigDecimal.TEN;
    private static final BigDecimal LOYALTY_BONUS_THRESHOLD = new BigDecimal("500");
    private static final int LOYALTY_BONUS_POINTS = 50;

    private final MemberRepository repository;

    public PointsCalculator(MemberRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository is required");
    }

    private void validate(String memberId, BigDecimal amount) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member id is required");
        }

        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must be zero or more " + amount);
        }
    }

    private BigDecimal basePoints(BigDecimal amount) {
        return amount.divide(BASE_POINT_DENOMINATOR, 0, RoundingMode.DOWN);
    }

    private int applyTierMultiplier(BigDecimal basePoints, Tier tier) throws ArithmeticException {
        return basePoints
                .multiply(tier.multiplier())
                .setScale(0, RoundingMode.DOWN)
                .intValueExact();
    }

    private int addBonusIfEligible(int points, BigDecimal amount) {
        return qualifiesForBonus(amount) ? Math.addExact(points, LOYALTY_BONUS_POINTS) : points;
    }

    private boolean qualifiesForBonus(BigDecimal amount) {
        return amount.compareTo(LOYALTY_BONUS_THRESHOLD) > 0;
    }


    public PointsAward calculatePoints(String memberId, BigDecimal amount) throws MemberNotFoundException, ArithmeticException {
        validate(memberId, amount);
        Member member = repository.findById(memberId);

        int points = applyTierMultiplier(basePoints(amount), member.getTier());
        points = addBonusIfEligible(points, amount);
        return new PointsAward(member, amount, points);
    }
}