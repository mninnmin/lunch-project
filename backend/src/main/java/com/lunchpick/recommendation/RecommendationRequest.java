package com.lunchpick.recommendation;

import com.lunchpick.menu.*;

public record RecommendationRequest(String mood, String category, String price, String spice, PartyType party, Long excludeMenuId) {
    public Mood parsedMood() { return parseNullable(Mood.class, mood, "ANY"); }
    public Category parsedCategory() { return parseNullable(Category.class, category, "ALL"); }
    public PriceLevel parsedPrice() { return parseNullable(PriceLevel.class, price, "ALL"); }
    public SpiceLevel parsedSpice() { return parseNullable(SpiceLevel.class, spice, "ALL"); }
    public PartyType safeParty() { return party == null ? PartyType.SOLO : party; }
    private static <E extends Enum<E>> E parseNullable(Class<E> type, String value, String wildcard) {
        if (value == null || value.isBlank() || wildcard.equalsIgnoreCase(value)) return null;
        try { return Enum.valueOf(type, value.toUpperCase()); }
        catch (IllegalArgumentException exception) { throw new IllegalArgumentException("지원하지 않는 선택값입니다: " + value); }
    }
}
