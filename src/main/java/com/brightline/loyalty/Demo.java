package com.brightline.loyalty;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;
import com.brightline.loyalty.member.InMemoryMemberRepository;
import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.Tier;
import com.brightline.loyalty.points.PointsAward;
import com.brightline.loyalty.points.PointsCalculator;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Demo {

    private static final Logger LOG = Logger.getLogger(Demo.class.getName());

    static {
        if (System.getProperty("java.util.logging.SimpleFormatter.format") == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%1$tF %1$tT %4$s %3$s - %5$s%6$s%n");
        }
    }

    public static void main(String[] args) {
        InMemoryMemberRepository memoryMemberRepository = new InMemoryMemberRepository();

        try {
            List<Member> members = List.of(
                    new Member("m1", "Kofi Mensah", Tier.STANDARD),
                    new Member("m2", "Ama Owusu", Tier.GOLD),
                    new Member("m3", "Yaa Boateng", Tier.SILVER));

            for (Member member : members) {
                memoryMemberRepository.save(member);
            }
            PointsCalculator calculator = new PointsCalculator(memoryMemberRepository);

            displayMemberPoints(calculator.calculatePoints("m1", BigDecimal.valueOf(100)));
            displayMemberPoints(calculator.calculatePoints("m2", BigDecimal.valueOf(250)));
            displayMemberPoints(calculator.calculatePoints("unknown", BigDecimal.valueOf(100)));

        } catch (ArithmeticException | MemberNotFoundException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        } catch (IllegalArgumentException | DuplicateMemberException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void displayMemberPoints(PointsAward award) {
        Member member = award.member();
        System.out.printf("Awarded %d points to %s%n", award.points(), member.getName());

        String label = String.format("Result %s (%s), GHS %s",
                member.getId(), member.getTier(), award.amount());
        System.out.printf("%-31s-> %d%n", label, award.points());
    }
}
