package com.brightline.loyalty.member;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Member")
class MemberTest {

    private static final String VALID_ID = "standard-member";
    private static final String VALID_NAME = "Kofi Mensah";
    private static final Tier VALID_TIER = Tier.STANDARD;

    @Test
    @DisplayName("getId returns the id given to the constructor")
    void getIdReturnsIdGivenToConstructor() {
        Member member = new Member(VALID_ID, VALID_NAME, VALID_TIER);

        String id = member.getId();

        // VALID_ID and VALID_NAME differ, so a swapped field is caught
        assertEquals(VALID_ID, id);
    }

    @Test
    @DisplayName("getName returns the name given to the constructor")
    void getNameReturnsNameGivenToConstructor() {
        Member member = new Member(VALID_ID, VALID_NAME, VALID_TIER);

        String name = member.getName();

        assertEquals(VALID_NAME, name);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Tier.class)
    @DisplayName("getTier returns the tier given to the constructor")
    void getTierReturnsTierGivenToConstructor(Tier tier) {
        Member member = new Member(VALID_ID, VALID_NAME, tier);

        Tier actual = member.getTier();

        assertEquals(tier, actual);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Tier.class)
    @DisplayName("accepts every tier")
    void constructorAcceptsEveryTier(Tier tier) {
        Member member = assertDoesNotThrow(() -> new Member(VALID_ID, VALID_NAME, tier));

        assertEquals(tier, member.getTier());
    }

    @Test
    @DisplayName("rejects a null tier")
    void constructorWithNullTierThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Member(VALID_ID, VALID_NAME, null));

        assertEquals("Tier cannot be null or blank", exception.getMessage());
    }

    @ParameterizedTest(name = "id = \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("rejects a null or blank id")
    void constructorWithNullOrBlankIdThrowsIllegalArgumentException(String id) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Member(id, VALID_NAME, VALID_TIER));

        assertEquals("id cannot be null or blank", exception.getMessage());
    }

    @ParameterizedTest(name = "name = \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("rejects a null or blank name")
    void constructorWithNullOrBlankNameThrowsIllegalArgumentException(String name) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Member(VALID_ID, name, VALID_TIER));

        assertEquals("name cannot be null or blank", exception.getMessage());
    }
}