package com.lunchpick.recommendation;

import com.lunchpick.menu.Menu;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "recommendation_history")
public class RecommendationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Menu menu;
    @Column(nullable = false, length = 60) private String provider;
    @Column(length = 30) private String mood;
    @Column(length = 30) private String category;
    @Column(nullable = false) private Instant createdAt;
    protected RecommendationHistory() {}
    RecommendationHistory(Menu menu, RecommendationRequest request, String provider) {
        this.menu = menu; this.provider = provider; this.mood = request.mood(); this.category = request.category(); this.createdAt = Instant.now();
    }
}
