package com.lunchpick.favorite;

import com.lunchpick.auth.SessionUser;
import com.lunchpick.menu.MenuRepository;
import com.lunchpick.recommendation.RecommendationResponse;
import com.lunchpick.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteRepository favorites;
    private final AppUserRepository users;
    private final MenuRepository menus;
    public FavoriteController(FavoriteRepository favorites, AppUserRepository users, MenuRepository menus) {
        this.favorites = favorites; this.users = users; this.menus = menus;
    }
    @GetMapping @Transactional(readOnly = true)
    public List<RecommendationResponse.MenuView> list(@AuthenticationPrincipal SessionUser user) {
        return favorites.findByUserIdOrderByCreatedAtDesc(user.id()).stream()
                .map(Favorite::getMenu).map(RecommendationResponse.MenuView::from).toList();
    }
    @PostMapping("/{menuId}") @ResponseStatus(HttpStatus.CREATED) @Transactional
    public RecommendationResponse.MenuView add(@AuthenticationPrincipal SessionUser user, @PathVariable Long menuId) {
        var menu = menus.findById(menuId).orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없어요."));
        if (!favorites.existsByUserIdAndMenuId(user.id(), menuId))
            favorites.save(new Favorite(users.getReferenceById(user.id()), menu));
        return RecommendationResponse.MenuView.from(menu);
    }
    @DeleteMapping("/{menuId}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional
    public void remove(@AuthenticationPrincipal SessionUser user, @PathVariable Long menuId) {
        favorites.deleteByUserIdAndMenuId(user.id(), menuId);
    }
}
