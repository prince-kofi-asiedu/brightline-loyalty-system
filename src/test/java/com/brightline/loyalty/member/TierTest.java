package com.brightline.loyalty.member;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Tier")
class TierTest {

    @ParameterizedTest(name = "{0} earns x{1}")
    @CsvSource({
            "STANDARD, 1",
            "SILVER,   1.5",
            "GOLD,     2"
    })
    @DisplayName("multiplier matches the loyalty scheme's rate")
    void multiplierMatchesSchemeRate(Tier tier, BigDecimal expectedMultiplier) {
        // Act
        BigDecimal actual = tier.multiplier();

        // Assert: compareTo ignores scale, so 2 and 2.0 are treated as the same rate
        assertEquals(0, expectedMultiplier.compareTo(actual),
                () -> tier + " multiplier should be " + expectedMultiplier + " but was " + actual);
    }

    @Test
    @DisplayName("defines exactly STANDARD, SILVER and GOLD, in that order")
    void definesExactlyThreeTiers() {
        // If this fails because a tier was added, also add its row to multiplierMatchesSchemeRate
        assertArrayEquals(
                new Tier[] {Tier.STANDARD, Tier.SILVER, Tier.GOLD},
                Tier.values());
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Tier.class)
    @DisplayName("every tier has a positive multiplier")
    void multiplierIsPositiveForEveryTier(Tier tier) {
        BigDecimal multiplier = tier.multiplier();

        assertNotNull(multiplier, () -> tier + " has no multiplier");
        assertTrue(multiplier.signum() > 0,
                () -> tier + " multiplier must be positive but was " + multiplier);
    }

    @Test
    @DisplayName("a higher tier always earns more than a lower one")
    void multipliersIncreaseWithTierRank() {
        BigDecimal standard = Tier.STANDARD.multiplier();
        BigDecimal silver = Tier.SILVER.multiplier();
        BigDecimal gold = Tier.GOLD.multiplier();

        assertTrue(standard.compareTo(silver) < 0, "STANDARD should earn less than SILVER");
        assertTrue(silver.compareTo(gold) < 0, "SILVER should earn less than GOLD");
    }

    @ParameterizedTest(name = "\"{0}\" is rejected")
    @ValueSource(strings = {"gold", "Gold", "silver", "standard", " GOLD", "GOLD ", "PLATINUM", ""})
    @DisplayName("valueOf rejects lowercase, padded and unknown tier names")
    void valueOfIsCaseSensitive(String name) {
        // The old String-based tier let "gold" silently earn x1; the enum must refuse it
        assertThrows(IllegalArgumentException.class, () -> Tier.valueOf(name));
    }
}