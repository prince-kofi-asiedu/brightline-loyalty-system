package com.brightline.loyalty;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;
import com.brightline.loyalty.member.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Behaviour every {@link MemberRepository} implementation must honour.
 * Each implementation gets its own subclass that only supplies {@link #createRepository()}.
 */
abstract class MemberRepositoryContractTest {

    private static final Member STANDARD_MEMBER = new Member("standard-member", "Kofi Mensah", Tier.STANDARD);
    private static final Member SILVER_MEMBER = new Member("silver-member", "Yaa Boateng", Tier.SILVER);
    private static final Member GOLD_MEMBER = new Member("gold-member", "Ama Owusu", Tier.GOLD);

    private MemberRepository repository;

    protected abstract MemberRepository createRepository();

    @BeforeEach
    void setUp() {
        repository = createRepository();
    }

    @Test
    @DisplayName("findById returns the member that was saved")
    void findByIdAfterSaveReturnsSameMember() throws DuplicateMemberException, MemberNotFoundException {
        repository.save(STANDARD_MEMBER);

        Member found = repository.findById("standard-member");

        assertSame(STANDARD_MEMBER, found);
    }

    @Test
    @DisplayName("findById throws MemberNotFoundException for an id that was never saved")
    void findByIdForUnknownIdThrowsMemberNotFoundException() throws DuplicateMemberException {
        repository.save(STANDARD_MEMBER);

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> repository.findById("unknown-member"));

        assertTrue(exception.getMessage().contains("unknown-member"),
                () -> "message should name the missing id but was: " + exception.getMessage());
    }

    @Test
    @DisplayName("findById on an empty repository throws MemberNotFoundException")
    void findByIdOnEmptyRepositoryThrowsMemberNotFoundException() {
        assertThrows(MemberNotFoundException.class, () -> repository.findById("standard-member"));
    }

    @ParameterizedTest(name = "id = \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("findById rejects a null or blank id")
    void findByIdWithNullOrBlankIdThrowsIllegalArgumentException(String id) {
        assertThrows(IllegalArgumentException.class, () -> repository.findById(id));
    }

    @Test
    @DisplayName("save throws DuplicateMemberException when the id is already taken")
    void saveWithDuplicateIdThrowsDuplicateMemberException() throws DuplicateMemberException {
        repository.save(STANDARD_MEMBER);
        Member sameId = new Member("standard-member", "Someone Else", Tier.GOLD);

        DuplicateMemberException exception = assertThrows(DuplicateMemberException.class,
                () -> repository.save(sameId));

        assertTrue(exception.getMessage().contains("standard-member"),
                () -> "message should name the duplicate id but was: " + exception.getMessage());
    }

    @Test
    @DisplayName("a rejected duplicate save leaves the original member in place")
    void saveWithDuplicateIdKeepsOriginalMember() throws DuplicateMemberException, MemberNotFoundException {
        repository.save(STANDARD_MEMBER);
        Member sameId = new Member("standard-member", "Someone Else", Tier.GOLD);

        assertThrows(DuplicateMemberException.class, () -> repository.save(sameId));

        Member found = repository.findById("standard-member");
        assertSame(STANDARD_MEMBER, found);
        assertEquals(Tier.STANDARD, found.getTier());
    }

    @Test
    @DisplayName("several saved members can each be found by their own id")
    void saveMultipleMembersEachRetrievableById() throws DuplicateMemberException {
        repository.save(STANDARD_MEMBER);
        repository.save(SILVER_MEMBER);
        repository.save(GOLD_MEMBER);

        assertAll(
                () -> assertSame(STANDARD_MEMBER, repository.findById("standard-member")),
                () -> assertSame(SILVER_MEMBER, repository.findById("silver-member")),
                () -> assertSame(GOLD_MEMBER, repository.findById("gold-member")));
    }

    @Test
    @DisplayName("ids are case-sensitive")
    void findByIdIsCaseSensitive() throws DuplicateMemberException {
        repository.save(STANDARD_MEMBER);

        assertThrows(MemberNotFoundException.class, () -> repository.findById("STANDARD-MEMBER"));
    }

    @Test
    @DisplayName("save rejects a null member")
    void saveWithNullMemberIsRejected() {
        // Records current behaviour; a guard such as Objects.requireNonNull would make this explicit
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }
}