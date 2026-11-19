package com.brightline.loyalty.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Domain exceptions")
class DomainExceptionsTest {

    @Test
    @DisplayName("MemberNotFoundException keeps the message it was given")
    void memberNotFoundExceptionKeepsMessage() {
        String message = "Member (unknown-member) does not exist";

        MemberNotFoundException exception = new MemberNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("DuplicateMemberException keeps the message it was given")
    void duplicateMemberExceptionKeepsMessage() {
        String message = "Member (standard-member) already exists";

        DuplicateMemberException exception = new DuplicateMemberException(message);

        assertEquals(message, exception.getMessage());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(classes = {MemberNotFoundException.class, DuplicateMemberException.class})
    @DisplayName("is a checked exception, so callers must handle it")
    void domainExceptionsAreChecked(Class<?> exceptionType) {
        assertTrue(Exception.class.isAssignableFrom(exceptionType),
                () -> exceptionType.getSimpleName() + " should extend Exception");
        assertFalse(RuntimeException.class.isAssignableFrom(exceptionType),
                () -> exceptionType.getSimpleName() + " should not be a RuntimeException");
    }
}
