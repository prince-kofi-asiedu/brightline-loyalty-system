package com.brightline.loyalty.points;

import com.brightline.loyalty.member.Member;
import java.math.BigDecimal;

public record PointsAward(Member member, BigDecimal amount, int points) { }