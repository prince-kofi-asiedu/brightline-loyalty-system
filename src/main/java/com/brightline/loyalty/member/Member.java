package com.brightline.loyalty.member;


public class Member {
    private final String id;
    private final String name;
    private final Tier tier;

    public Member(String id, String name, Tier tier) {
        this.id = id;
        this.name = name;
        this.tier = tier;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Tier getTier() { return tier; }
}
