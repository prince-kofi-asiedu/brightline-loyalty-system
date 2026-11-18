package com.brightline.loyalty;

import com.brightline.loyalty.member.DuplicateMemberException;
import com.brightline.loyalty.member.Member;
import com.brightline.loyalty.member.MemberNotFoundException;

public interface MemberRepository {
    Member findById(String id) throws MemberNotFoundException;

    void save(Member member) throws DuplicateMemberException;
}
