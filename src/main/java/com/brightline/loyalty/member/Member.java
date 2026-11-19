package com.brightline.loyalty.member;


public class Member {
    private final String id;
    private final String name;
    private final Tier tier;

    public Member(String id, String name, Tier tier) {
        if (tier == null) {
            throw new IllegalArgumentException("Tier is required");
        }

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id cannot be null or blank");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank");
        }
        this.id = id;
        this.name = name;
        this.tier = tier;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Tier getTier() { return tier; }
}
