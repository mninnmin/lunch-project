package com.lunchpick.favorite;

import com.lunchpick.menu.Menu;
import com.lunchpick.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(name = "uk_favorite_user_menu", columnNames = {"user_id", "menu_id"}))
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private AppUser user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "menu_id") private Menu menu;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    protected Favorite() {}
    public Favorite(AppUser user, Menu menu) { this.user = user; this.menu = menu; this.createdAt = Instant.now(); }
    public Menu getMenu() { return menu; }
    public Instant getCreatedAt() { return createdAt; }
}
