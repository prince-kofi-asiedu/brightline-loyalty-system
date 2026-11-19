package com.brightline.loyalty.member;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> members = new HashMap<>();

    @Override
    public Member findById(String id) throws MemberNotFoundException {
        validateMemberId(id);
        if (!memberExists(id)) {
            throw new MemberNotFoundException("Member (" + id + ") does not exist");
        }
        Member member = members.get(id);
        return member;
    }

    @Override
    public void save(Member member) throws DuplicateMemberException {
        Objects.requireNonNull(member, "member is required");
        String memberId = member.getId();
        if (memberExists(memberId)) {
            throw new DuplicateMemberException("Member (" + memberId + ") already exists");
        }
        members.put(memberId, member);
    }

    private void validateMemberId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Member id is required");
        }
    }

    private boolean memberExists(String id) {
        return members.containsKey(id);
    }
}
