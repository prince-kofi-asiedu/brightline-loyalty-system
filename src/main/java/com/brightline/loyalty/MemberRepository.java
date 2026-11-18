package com.brightline.loyalty;

import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.DuplicateMemberException;

public interface MemberRepository {
    Member findById(String id);
    void save(Member member) throws DuplicateMemberException;
}
