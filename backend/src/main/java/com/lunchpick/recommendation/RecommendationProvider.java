package com.lunchpick.recommendation;

import com.lunchpick.menu.Menu;
import java.util.List;

/** Gemini 연동 시 이 인터페이스의 새 구현체를 추가합니다. */
public interface RecommendationProvider {
    RecommendationDecision recommend(RecommendationRequest request, List<Menu> menus);
    record RecommendationDecision(Menu selected, String reason, List<Menu> alternatives) {}
}
