package com.brightline.loyalty;

import java.util.HashMap;
import java.util.Map;

import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.Member;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> members = new HashMap<>();

    @Override
    public Member findById(String id) {
        return members.get(id); // returns null when the id is not found
    }

    @Override
    public void save(Member member) throws IllegalArgumentException, DuplicateMemberException {
        String memberId = member.getId();
        if (memberExists(member)) {
            throw new DuplicateMemberException("Member with id " + memberId + " already exists");
        }
        members.put(memberId, member);
    }

    boolean memberExists(Member member) {
        if (findById(member.getId()) != null) {
            return true;
        }
        return false;
    }
}
