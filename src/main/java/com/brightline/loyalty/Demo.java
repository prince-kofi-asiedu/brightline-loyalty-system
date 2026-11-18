package com.brightline.loyalty;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.Tier;

// A tiny runner so you can see the calculator work before you change anything.
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
                    new Member("m3", "Yaa Boateng", Tier.SILVER)
            );

            for (Member member : members) {
                memoryMemberRepository.save(member);
            }
            PointsCalculator calculator = new PointsCalculator(memoryMemberRepository);

            System.out.println("Result m1 (STANDARD), GHS 100  -> " + calculator.calculatePoints("m1", BigDecimal.valueOf(100)));
            System.out.println("Result m2 (GOLD), GHS 250      -> " + calculator.calculatePoints("m2", BigDecimal.valueOf(250)));
            System.out.println("Result unknown member, GHS 100 -> " + calculator.calculatePoints("unknown", BigDecimal.valueOf(100)));

        } catch (IllegalArgumentException | DuplicateMemberException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
