package com.lunchpick.recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Long> {}
