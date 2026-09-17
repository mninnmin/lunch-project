package com.lunchpick.recommendation;

import com.lunchpick.menu.Menu;
import java.util.List;

public record RecommendationResponse(MenuView menu, String reason, List<MenuSummary> alternatives) {
    public record MenuView(Long id, String name, String emoji, String description, String category,
                           String categoryLabel, String price, String priceLabel, String spice, String spiceLabel) {
        public static MenuView from(Menu menu) {
            return new MenuView(menu.getId(), menu.getName(), menu.getEmoji(), menu.getDescription(),
                    menu.getCategory().name(), Labels.category(menu), menu.getPriceLevel().name(),
                    Labels.price(menu), menu.getSpiceLevel().name(), Labels.spice(menu));
        }
    }
    public record MenuSummary(Long id, String name, String emoji) {
        public static MenuSummary from(Menu menu) { return new MenuSummary(menu.getId(), menu.getName(), menu.getEmoji()); }
    }
}
