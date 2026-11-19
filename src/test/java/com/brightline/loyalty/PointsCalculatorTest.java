package com.brightline.loyalty;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;
import com.brightline.loyalty.member.Tier;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointsCalculator")
class PointsCalculatorTest {

    private static final String MEMBER_ID = "standard-member";
    private static final String MEMBER_NAME = "Kofi Mensah";

    @Mock
    private MemberRepository repository;

    private PointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PointsCalculator(repository);
    }

    private Member givenMember(String id, Tier tier) throws MemberNotFoundException {
        Member member = new Member(id, MEMBER_NAME, tier);
        when(repository.findById(id)).thenReturn(member);
        return member;
    }

    private int pointsFor(Tier tier, String amount) throws MemberNotFoundException {
        givenMember(MEMBER_ID, tier);
        return calculator.calculatePoints(MEMBER_ID, new BigDecimal(amount)).points();
    }

    @Nested
    @DisplayName("input validation")
    class Validation {

        @ParameterizedTest(name = "memberId = \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t"})
        @DisplayName("rejects a null or blank member id before looking anything up")
        void withNullOrBlankMemberIdThrowsIllegalArgumentException(String memberId) {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculatePoints(memberId, new BigDecimal("100")));

            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("rejects a null amount before looking anything up")
        void withNullAmountThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculatePoints(MEMBER_ID, null));

            verifyNoInteractions(repository);
        }

        @ParameterizedTest(name = "amount = {0}")
        @ValueSource(strings = {"-0.01", "-100"})
        @DisplayName("rejects a negative amount before looking anything up")
        void withNegativeAmountThrowsIllegalArgumentException(String amount) {
            assertThrows(IllegalArgumentException.class,
                    () -> calculator.calculatePoints(MEMBER_ID, new BigDecimal(amount)));

            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("accepts a zero amount and awards nothing")
        void withZeroAmountReturnsZero() throws MemberNotFoundException {
            assertEquals(0, pointsFor(Tier.STANDARD, "0"));
        }
    }

    @Nested
    @DisplayName("base points")
    class BasePoints {

        @ParameterizedTest(name = "GHS {0} -> {1} points")
        @CsvSource({
                "9.99,   0",
                "10,     1",
                "10.01,  1",
                "99.99,  9",
                "100,    10",
                "250,    25",
                "499.99, 49"
        })
        @DisplayName("awards one point per GHS 10, rounded down")
        void forStandardMemberAwardsOnePointPerTenCedisRoundedDown(String amount, int expected)
                throws MemberNotFoundException {
            assertEquals(expected, pointsFor(Tier.STANDARD, amount));
        }
    }

    @Nested
    @DisplayName("tier multiplier")
    class TierMultiplier {

        @ParameterizedTest(name = "{0} at GHS {1} -> {2} points")
        @CsvSource({
                "STANDARD, 100, 10",
                "SILVER,   100, 15",
                "GOLD,     100, 20",
                "SILVER,   200, 30",
                "GOLD,     250, 50"
        })
        @DisplayName("multiplies base points by the member's tier rate")
        void appliesTierMultiplier(Tier tier, String amount, int expected) throws MemberNotFoundException {
            assertEquals(expected, pointsFor(tier, amount));
        }

        @Test
        @DisplayName("drops fractional points for SILVER (GHS 150 -> 22, not 22.5)")
        void forSilverTruncatesFractionalPoints() throws MemberNotFoundException {
            assertEquals(22, pointsFor(Tier.SILVER, "150"));
        }

        @Test
        @DisplayName("rounds the base down before applying the multiplier")
        void truncatesBaseBeforeApplyingMultiplier() throws MemberNotFoundException {
            // base 1 x 1.5 = 1.5 -> 1; multiplying first (1.999 x 1.5) would give 2
            assertEquals(1, pointsFor(Tier.SILVER, "19.99"));
        }
    }

    @Nested
    @DisplayName("big-spender bonus")
    class Bonus {

        @ParameterizedTest(name = "GHS {0}")
        @ValueSource(strings = {"500", "500.00", "500.000"})
        @DisplayName("is not added at exactly GHS 500, whatever the scale")
        void atExactlyThresholdDoesNotAddBonus(String amount) throws MemberNotFoundException {
            assertEquals(50, pointsFor(Tier.STANDARD, amount));
        }

        @Test
        @DisplayName("is added just above GHS 500")
        void justAboveThresholdAddsBonus() throws MemberNotFoundException {
            assertEquals(100, pointsFor(Tier.STANDARD, "500.01"));
        }

        @Test
        @DisplayName("is not added just below GHS 500")
        void justBelowThresholdDoesNotAddBonus() throws MemberNotFoundException {
            assertEquals(49, pointsFor(Tier.STANDARD, "499.99"));
        }

        @Test
        @DisplayName("is added after the multiplier, not multiplied by it")
        void bonusIsAddedAfterMultiplierNotMultiplied() throws MemberNotFoundException {
            // (60 x 2) + 50 = 170; (60 + 50) x 2 would be 220
            assertEquals(170, pointsFor(Tier.GOLD, "600"));
        }

        @ParameterizedTest(name = "{0} at GHS 1000 -> {1} points")
        @CsvSource({
                "STANDARD, 150",
                "SILVER,   200",
                "GOLD,     250"
        })
        @DisplayName("is a flat 50 points for every tier")
        void aboveThresholdAddsFlat50ForEveryTier(Tier tier, int expected) throws MemberNotFoundException {
            assertEquals(expected, pointsFor(tier, "1000"));
        }
    }

    @Nested
    @DisplayName("points award")
    class Award {

        @Test
        @DisplayName("carries the member returned by the repository")
        void awardCarriesLookedUpMember() throws MemberNotFoundException {
            Member member = givenMember(MEMBER_ID, Tier.GOLD);

            PointsAward award = calculator.calculatePoints(MEMBER_ID, new BigDecimal("250"));

            assertSame(member, award.member());
        }

        @Test
        @DisplayName("carries the purchase amount exactly as given, scale included")
        void awardCarriesOriginalAmount() throws MemberNotFoundException {
            givenMember(MEMBER_ID, Tier.STANDARD);
            BigDecimal amount = new BigDecimal("250.00");

            PointsAward award = calculator.calculatePoints(MEMBER_ID, amount);

            assertSame(amount, award.amount());
        }

        @Test
        @DisplayName("carries the calculated points, bonus included")
        void awardCarriesCalculatedPoints() throws MemberNotFoundException {
            Member member = givenMember(MEMBER_ID, Tier.GOLD);
            BigDecimal amount = new BigDecimal("600");

            PointsAward award = calculator.calculatePoints(MEMBER_ID, amount);

            assertEquals(new PointsAward(member, amount, 170), award);
        }
    }

    @Nested
    @DisplayName("repository interaction")
    class RepositoryInteraction {

        @Test
        @DisplayName("looks the member up exactly once and never saves")
        void looksUpMemberExactlyOnce() throws MemberNotFoundException {
            pointsFor(Tier.STANDARD, "100");

            verify(repository).findById(MEMBER_ID);
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("lets MemberNotFoundException reach the caller instead of returning 0")
        void whenMemberNotFoundPropagatesMemberNotFoundException() throws MemberNotFoundException {
            MemberNotFoundException notFound = new MemberNotFoundException("Member (unknown-member) does not exist");
            when(repository.findById("unknown-member")).thenThrow(notFound);

            MemberNotFoundException thrown = assertThrows(MemberNotFoundException.class,
                    () -> calculator.calculatePoints("unknown-member", new BigDecimal("100")));

            assertSame(notFound, thrown);
        }

        @Test
        @DisplayName("lets unexpected repository failures reach the caller")
        void whenRepositoryThrowsRuntimeExceptionPropagatesIt() throws MemberNotFoundException {
            when(repository.findById(MEMBER_ID)).thenThrow(new IllegalStateException("store unavailable"));

            assertThrows(IllegalStateException.class,
                    () -> calculator.calculatePoints(MEMBER_ID, new BigDecimal("100")));
        }
    }

    @Nested
    @DisplayName("int overflow")
    class Overflow {

        @Test
        @DisplayName("throws when tier points no longer fit in an int")
        void whenPointsExceedIntRangeThrowsArithmeticException() throws MemberNotFoundException {
            givenMember(MEMBER_ID, Tier.STANDARD);

            // base = 2,147,483,648 = Integer.MAX_VALUE + 1
            assertThrows(ArithmeticException.class,
                    () -> calculator.calculatePoints(MEMBER_ID, new BigDecimal("21474836480")));
        }

        @Test
        @DisplayName("throws instead of wrapping negative when the bonus pushes past Integer.MAX_VALUE")
        void whenBonusPushesPastIntMaxDoesNotWrapNegative() throws MemberNotFoundException {
            givenMember(MEMBER_ID, Tier.STANDARD);

            // base = Integer.MAX_VALUE exactly, then + 50 bonus
            assertThrows(ArithmeticException.class,
                    () -> calculator.calculatePoints(MEMBER_ID, new BigDecimal("21474836470")));
        }
    }

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("fails fast when given a null repository")
        void constructorWithNullRepositoryFailsFast() {
            assertThrows(NullPointerException.class, () -> new PointsCalculator(null));
        }
    }
}