package com.brightline.loyalty;

import java.util.List;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.Tier;

// A tiny runner so you can see the calculator work before you change anything.
public class Demo {

    public static void main(String[] args) {
        InMemoryMemberRepository memoryMemberRepository = new InMemoryMemberRepository();

        List<Member> members = List.of(
                new Member("m1", "Kofi Mensah", Tier.STANDARD),
                new Member("m2", "Ama Owusu", Tier.GOLD),
                new Member("m3", "Yaa Boateng", Tier.SILVER)
        );

        for(Member member: members) {
            memoryMemberRepository.save(member);
        }

        PointsCalculator calculator = new PointsCalculator(memoryMemberRepository);

        System.out.println("Result m1 (STANDARD), GHS 100  -> " + calculator.calculatePoints("m1", 100));
        System.out.println("Result m2 (GOLD), GHS 250      -> " + calculator.calculatePoints("m2", 250));
        System.out.println("Result unknown member, GHS 100 -> " + calculator.calculatePoints("unknown", 100));
    }
}
