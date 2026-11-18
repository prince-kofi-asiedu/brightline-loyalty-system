package com.brightline.loyalty;

import java.util.HashMap;
import java.util.Map;

import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> members = new HashMap<>();

    @Override
    public Member findById(String id) throws MemberNotFoundException {
        validateMemberId(id);
        boolean memberDoesNotExist = !memberExists(id);
        if (memberDoesNotExist) {
            throw new MemberNotFoundException("Member " + id + "does not exist");
        }
        Member member = members.get(id);
        return member;
    }

    @Override
    public void save(Member member) throws DuplicateMemberException {
        String memberId = member.getId();
        if (memberExists(memberId)) {
            throw new DuplicateMemberException("Member with id " + memberId + " already exists");
        }
        members.put(memberId, member);
    }

    void validateMemberId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be null or blank");
        }
    }

    boolean memberExists(String id) {
        return members.containsKey(id);
    }
}
