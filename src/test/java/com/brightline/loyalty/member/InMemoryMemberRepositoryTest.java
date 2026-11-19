package com.brightline.loyalty.member;

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
    @DisplayName("findById accepts any non-blank id and goes on to look it up")
    void findByIdWithNonBlankIdReachesLookup(String id) {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();

        // Not found (rather than IllegalArgumentException) shows the id passed validation
        assertThrows(MemberNotFoundException.class, () -> repository.findById(id));
    }

    @ParameterizedTest(name = "id = \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("findById rejects a null or blank id with a clear message")
    void findByIdWithNullOrBlankIdThrowsWithMessage(String id) {
        InMemoryMemberRepository repository = new InMemoryMemberRepository();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repository.findById(id));

        assertEquals("Member id cannot be null or blank", exception.getMessage());
    }
}