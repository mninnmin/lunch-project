package com.lunchpick.recommendation;

import com.lunchpick.menu.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class RulesRecommendationProviderTest {
    private final RulesRecommendationProvider provider = new RulesRecommendationProvider(new Random(1));
    @Test void matchingPreferencesReceiveHigherScore() {
        Menu matching = menu("마라탕", Category.CHINESE, PriceLevel.NORMAL, SpiceLevel.HOT, Mood.STRESS);
        Menu other = menu("샐러드", Category.WESTERN, PriceLevel.NORMAL, SpiceLevel.NONE, Mood.LIGHT);
        RecommendationRequest request = new RecommendationRequest("STRESS", "CHINESE", "NORMAL", "HOT", PartyType.SOLO, null);
        assertThat(provider.score(matching, request)).isGreaterThan(provider.score(other, request));
    }
    @Test void wildcardValuesAreAccepted() {
        RecommendationRequest request = new RecommendationRequest("ANY", "ALL", "ALL", "ALL", PartyType.SOLO, null);
        assertThat(provider.score(menu("비빔밥", Category.KOREAN, PriceLevel.VALUE, SpiceLevel.MILD, Mood.LIGHT), request)).isEqualTo(1);
    }
    private Menu menu(String name, Category category, PriceLevel price, SpiceLevel spice, Mood mood) {
        return new Menu(name, "🍽️", "설명", category, price, spice, Set.of(mood), true);
    }
}
