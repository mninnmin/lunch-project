package com.lunchpick.recommendation;

import com.lunchpick.menu.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.recommendation.provider", havingValue = "rules", matchIfMissing = true)
public class RulesRecommendationProvider implements RecommendationProvider {
    private final Random random;
    public RulesRecommendationProvider() { this(new Random()); }
    RulesRecommendationProvider(Random random) { this.random = random; }
    @Override public RecommendationDecision recommend(RecommendationRequest request, List<Menu> menus) {
        if (menus.isEmpty()) throw new IllegalStateException("추천할 메뉴가 없습니다.");
        var ranked = menus.stream().map(menu -> new ScoredMenu(menu, score(menu, request)))
                .sorted(Comparator.comparingInt(ScoredMenu::score).reversed()).toList();
        int bestScore = ranked.getFirst().score();
        var shortlist = ranked.stream().filter(item -> item.score() >= bestScore - 2).limit(5).toList();
        Menu selected = shortlist.get(random.nextInt(shortlist.size())).menu();
        var alternatives = ranked.stream().map(ScoredMenu::menu).filter(menu -> !menu.getId().equals(selected.getId())).limit(3).toList();
        return new RecommendationDecision(selected, reason(selected, request), alternatives);
    }
    int score(Menu menu, RecommendationRequest request) {
        int score = 1;
        if (request.parsedCategory() != null) score += menu.getCategory() == request.parsedCategory() ? 8 : -20;
        if (request.parsedPrice() != null) score += menu.getPriceLevel() == request.parsedPrice() ? 5 : -4;
        if (request.parsedSpice() != null) score += menu.getSpiceLevel() == request.parsedSpice() ? 5 : -5;
        if (request.parsedMood() != null && menu.getMoods().contains(request.parsedMood())) score += 6;
        if (request.safeParty() == PartyType.GROUP) score += menu.isGroupFriendly() ? 3 : -2;
        if (request.excludeMenuId() != null && Objects.equals(request.excludeMenuId(), menu.getId())) score -= 50;
        return score;
    }
    private String reason(Menu menu, RecommendationRequest request) {
        List<String> matches = new ArrayList<>();
        if (request.parsedMood() != null && menu.getMoods().contains(request.parsedMood())) matches.add(Labels.mood(request.parsedMood()));
        if (request.parsedPrice() != null && menu.getPriceLevel() == request.parsedPrice()) matches.add(Labels.price(menu));
        if (request.parsedSpice() != null && menu.getSpiceLevel() == request.parsedSpice()) matches.add(Labels.spice(menu));
        return matches.isEmpty() ? "오늘 점심으로 부담 없이 즐기기 좋은 메뉴예요." : String.join(" · ", matches) + " 취향에 잘 맞아요.";
    }
    private record ScoredMenu(Menu menu, int score) {}
}
