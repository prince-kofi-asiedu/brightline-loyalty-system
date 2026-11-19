package com.brightline.loyalty.points;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brightline.loyalty.member.MemberNotFoundException;
import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.Tier;
import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.InMemoryMemberRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PointsCalculator with InMemoryMemberRepository")
class PointsCalculatorIT {

    private static final Member STANDARD_MEMBER = new Member("standard-member", "Kofi Mensah", Tier.STANDARD);
    private static final Member SILVER_MEMBER = new Member("silver-member", "Yaa Boateng", Tier.SILVER);
    private static final Member GOLD_MEMBER = new Member("gold-member", "Ama Owusu", Tier.GOLD);

    private InMemoryMemberRepository repository;
    private PointsCalculator calculator;

    @BeforeEach
    void setUp() throws DuplicateMemberException {
        repository = new InMemoryMemberRepository();
        repository.save(STANDARD_MEMBER);
        repository.save(SILVER_MEMBER);
        repository.save(GOLD_MEMBER);
        calculator = new PointsCalculator(repository);
    }

    @ParameterizedTest(name = "{0} at GHS {1} -> {2} points")
    @CsvSource({
            "standard-member, 100, 10",
            "silver-member,   150, 22",
            "gold-member,     250, 50",
            "gold-member,     600, 170"
    })
    @DisplayName("awards the scheme's points for each stored tier")
    void calculatePointsForEachSavedTierReturnsSchemeValue(String memberId, String amount, int expected)
            throws MemberNotFoundException {
        PointsAward award = calculator.calculatePoints(memberId, new BigDecimal(amount));

        assertEquals(expected, award.points());
    }

    @Test
    @DisplayName("the award carries the member stored in the repository and the amount given")
    void calculatePointsReturnsAwardWithStoredMemberAndAmount() throws MemberNotFoundException {
        BigDecimal amount = new BigDecimal("250.00");

        PointsAward award = calculator.calculatePoints("gold-member", amount);

        assertEquals(new PointsAward(GOLD_MEMBER, amount, 50), award);
        assertSame(GOLD_MEMBER, award.member());
    }

    @Test
    @DisplayName("an unknown member raises MemberNotFoundException instead of 0 points")
    void calculatePointsForUnknownMemberThrowsMemberNotFoundException() {
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> calculator.calculatePoints("unknown-member", new BigDecimal("100")));

        assertTrue(exception.getMessage().contains("unknown-member"),
                () -> "message should name the missing id but was: " + exception.getMessage());
    }

    @Test
    @DisplayName("members saved after the calculator was created are found")
    void calculatePointsSeesMembersSavedAfterCalculatorCreated()
            throws DuplicateMemberException, MemberNotFoundException {
        Member lateJoiner = new Member("late-joiner", "Esi Asante", Tier.SILVER);
        repository.save(lateJoiner);

        PointsAward award = calculator.calculatePoints("late-joiner", new BigDecimal("100"));

        assertEquals(15, award.points());
    }

    @Test
    @DisplayName("a rejected duplicate save does not change the member's tier")
    void calculatePointsAfterRejectedDuplicateSaveUsesOriginalTier() throws MemberNotFoundException {
        Member impostor = new Member("standard-member", "Someone Else", Tier.GOLD);
        assertThrows(DuplicateMemberException.class, () -> repository.save(impostor));

        PointsAward award = calculator.calculatePoints("standard-member", new BigDecimal("100"));

        assertEquals(10, award.points());
        assertSame(STANDARD_MEMBER, award.member());
    }

    @Test
    @DisplayName("a blank id is rejected by the calculator before it reaches the repository")
    void calculatePointsWithBlankIdFailsInCalculatorBeforeRepository() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calculator.calculatePoints("  ", new BigDecimal("100")));

        // The repository's own message is "Member id cannot be null or blank"
        assertEquals("Member id is required", exception.getMessage());
    }
}