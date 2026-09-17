package com.lunchpick.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndMenuId(Long userId, Long menuId);
    void deleteByUserIdAndMenuId(Long userId, Long menuId);
}
