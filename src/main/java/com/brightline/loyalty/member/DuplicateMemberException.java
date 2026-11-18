package com.brightline.loyalty.member;

public class DuplicateMemberException extends Exception {
    public DuplicateMemberException(String errMessage) {
        super(errMessage);
    }
}
