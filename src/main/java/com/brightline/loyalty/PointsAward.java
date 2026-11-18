package com.brightline.loyalty;
import com.brightline.loyalty.member.Member;

import java.math.BigDecimal;

public record PointsAward(Member member, BigDecimal amount, int points) { }