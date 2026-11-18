package com.brightline.loyalty;

import com.brightline.loyalty.member.Member;

public interface MemberRepository {
    Member findById(String id);
}
