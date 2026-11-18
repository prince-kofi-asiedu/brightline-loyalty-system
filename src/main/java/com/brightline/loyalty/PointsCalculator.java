package com.brightline.loyalty;

import java.lang.ArithmeticException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;
import com.brightline.loyalty.member.Tier;

public class PointsCalculator {

    private static final BigDecimal BASE_POINT_DENOMINATOR = BigDecimal.TEN;
    private static final BigDecimal LOYALTY_BONUS_THRESHOLD = new BigDecimal("500");
    private static final int LOYALTY_BONUS_POINTS = 50;

    private final MemberRepository repository;

    public PointsCalculator(MemberRepository repository) {
        this.repository = repository;
    }

    private void validateMemberAndAmount(String memberId, BigDecimal amount) {
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
        return qualifiesForBonus(amount) ? points + LOYALTY_BONUS_POINTS : points;
    }

    private boolean qualifiesForBonus(BigDecimal amount) {
        return amount.compareTo(LOYALTY_BONUS_THRESHOLD) > 0;
    }


    public int calculatePoints(String memberId, BigDecimal amount) throws MemberNotFoundException, ArithmeticException {
        validateMemberAndAmount(memberId, amount);
        Member member = repository.findById(memberId);

        int points = applyTierMultiplier(basePoints(amount), member.getTier());
        return addBonusIfEligible(points, amount);
    }

    public void displayPoints(String memberId, BigDecimal amount) throws MemberNotFoundException, ArithmeticException{
        int points = calculatePoints(memberId, amount);
        Member member = repository.findById(memberId);
        System.out.printf("Awarded %d points to %s%n", points, member.getName());

        String label = String.format("Result %s (%s), GHS %s",
                member.getId(), member.getTier(), amount);
        System.out.printf("%-31s-> %d%n", label, points);
    }
}
