package com.brightline.loyalty.member;

public interface MemberRepository {
    Member findById(String id) throws MemberNotFoundException;

    void save(Member member) throws DuplicateMemberException;
}