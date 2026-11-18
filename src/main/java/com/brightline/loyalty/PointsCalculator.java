package com.brightline.loyalty;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.Tier;

public class PointsCalculator {

    private static final int BASE_POINT_DENOMINATOR = 10;
    private static final int LOYALTY_BONUS_THRESHOLD = 500;
    private static final int LOYALTY_BONUS_POINTS = 50;

    private final MemberRepository repository;
    

    public PointsCalculator(MemberRepository repository) {
        this.repository = repository;
    }

    public int calculatePoints(String memberId, double amount) {
        try {
            Member member = repository.findById(memberId);

            // base points: one point for every GHS 10 spent
            int points = (int) (amount / BASE_POINT_DENOMINATOR);

            // apply the member's tier multiplier
            if (member.getTier().equals(Tier.STANDARD)) {
                points = points * Tier.SILVER.multiplier();
            } else if (member.getTier().equals(Tier.SILVER)) {
                points = (int) (points * Tier.SILVER.multiplier());
            } else if (member.getTier().equals(Tier.GOLD)) {
                points = points * Tier.GOLD.multiplier();
            }

            // loyalty bonus for big spenders
            if (amount > LOYALTY_BONUS_THRESHOLD) {
                points = points + LOYALTY_BONUS_POINTS;
            }

            System.out.println("Awarded " + points + " points to " + member.getName());
            return points;
        } catch (Exception e) {
            // something went wrong (for example the member was not found) - award nothing
            return 0;
        }
    }
}
