package com.brightline.loyalty.member;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("InMemoryMemberRepository")
class InMemoryMemberRepositoryTest extends MemberRepositoryContractTest {

    @Override
    protected MemberRepository createRepository() {
        return new InMemoryMemberRepository();
    }

    @ParameterizedTest(name = "id = \"{0}\"")
    @ValueSource(strings = {"standard-member", "m1", " padded-id "})
    @DisplayName("validateMemberId accepts any non-blank id")
    void validateMemberIdAcceptsNonBlankId(String id) {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();

        assertDoesNotThrow(() -> repository.validateMemberId(id));
    }

    @ParameterizedTest(name = "id = \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("validateMemberId rejects a null or blank id")
    void validateMemberIdRejectsNullOrBlankId(String id) {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repository.validateMemberId(id));

        assertEquals("Member id cannot be null or blank", exception.getMessage());
    }
}