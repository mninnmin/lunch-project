package com.lunchpick.recommendation;

import com.lunchpick.menu.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {
    private final MenuRepository menuRepository;
    private final RecommendationHistoryRepository historyRepository;
    private final RecommendationProvider provider;
    public RecommendationService(MenuRepository menuRepository, RecommendationHistoryRepository historyRepository,
                                 RecommendationProvider provider) {
        this.menuRepository = menuRepository; this.historyRepository = historyRepository; this.provider = provider;
    }
    @Transactional public RecommendationResponse recommend(RecommendationRequest request) {
        var decision = provider.recommend(request, menuRepository.findAll());
        historyRepository.save(new RecommendationHistory(decision.selected(), request, provider.getClass().getSimpleName()));
        return new RecommendationResponse(RecommendationResponse.MenuView.from(decision.selected()), decision.reason(),
                decision.alternatives().stream().map(RecommendationResponse.MenuSummary::from).toList());
    }
}
