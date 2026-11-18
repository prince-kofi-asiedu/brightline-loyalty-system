package com.brightline.loyalty;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.Tier;

public class PointsCalculator {

    private final MemberRepository repository;

    public PointsCalculator(MemberRepository repository) {
        this.repository = repository;
    }

    public int calculatePoints(String memberId, double amount) {
        try {
            Member member = repository.findById(memberId);

            // base points: one point for every GHS 10 spent
            int points = (int) (amount / 10);

            // apply the member's tier multiplier
            if (member.getTier().equals(Tier.STANDARD)) {
                points = points * 1;
            } else if (member.getTier().equals(Tier.SILVER)) {
                points = (int) (points * 1.5);
            } else if (member.getTier().equals(Tier.GOLD)) {
                points = points * 2;
            }

            // loyalty bonus for big spenders
            if (amount > 500) {
                points = points + 50;
            }

            System.out.println("Awarded " + points + " points to " + member.getName());
            return points;
        } catch (Exception e) {
            // something went wrong (for example the member was not found) - award nothing
            return 0;
        }
    }
}
