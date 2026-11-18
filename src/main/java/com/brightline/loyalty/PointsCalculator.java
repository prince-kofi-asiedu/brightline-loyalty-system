package com.brightline.loyalty;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.brightline.loyalty.member.Member;

public class PointsCalculator {

    private static final BigDecimal BASE_POINT_DENOMINATOR = BigDecimal.TEN;
    private static final BigDecimal LOYALTY_BONUS_THRESHOLD = new BigDecimal("500");
    private static final int LOYALTY_BONUS_POINTS = 50;

    private final MemberRepository repository;
    

    public PointsCalculator(MemberRepository repository) {
        this.repository = repository;
    }

    public int calculatePoints(String memberId, BigDecimal amount) {
        try {
            Member member = repository.findById(memberId);

            // base points: one point for every GHS 10 spent
            BigDecimal basePoints = amount.divide(BASE_POINT_DENOMINATOR, 0, RoundingMode.DOWN);
            
            int points = basePoints
                        .multiply(member.getTier().multiplier())
                        .setScale(0, RoundingMode.DOWN).intValueExact();
            
            // loyalty bonus for big spenders
            if (amount.compareTo(LOYALTY_BONUS_THRESHOLD) > 0) {
                points += LOYALTY_BONUS_POINTS;
            }

            System.out.println("Awarded " + points + " points to " + member.getName());
            return points;
        } catch (Exception e) {
            // something went wrong (for example the member was not found) - award nothing
            return 0;
        }
    }
}
