package com.brightline.loyalty;

import java.util.HashMap;
import java.util.Map;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.DuplicateMemberException;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> members = new HashMap<>();

    @Override
    public Member findById(String id) {
        return members.get(id); // returns null when the id is not found
    }

    @Override 
    public void save(Member member) throws IllegalArgumentException, DuplicateMemberException {
        String memberId = member.getId();
        if (findById(memberId) != null) {
            throw new DuplicateMemberException("Member with id " + memberId + " already exists");
        }
        members.put(memberId, member);
    }
}
