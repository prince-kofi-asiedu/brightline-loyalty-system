package com.brightline.loyalty.member;

public class MemberNotFoundException extends Exception {

    public MemberNotFoundException(String errMessage) {
        super(errMessage);
    }
}
