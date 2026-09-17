package com.lunchpick.menu;

import jakarta.persistence.*;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "menus")
public class Menu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String name;
    @Column(nullable = false, length = 8)
    private String emoji;
    @Column(nullable = false, length = 240)
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Category category;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private PriceLevel priceLevel;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private SpiceLevel spiceLevel;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "menu_moods", joinColumns = @JoinColumn(name = "menu_id"))
    @Enumerated(EnumType.STRING) @Column(name = "mood", nullable = false)
    private Set<Mood> moods = EnumSet.noneOf(Mood.class);
    @Column(nullable = false)
    private boolean groupFriendly;

    protected Menu() {}
    public Menu(String name, String emoji, String description, Category category, PriceLevel priceLevel,
                SpiceLevel spiceLevel, Set<Mood> moods, boolean groupFriendly) {
        this.name = name; this.emoji = emoji; this.description = description; this.category = category;
        this.priceLevel = priceLevel; this.spiceLevel = spiceLevel; this.moods = moods; this.groupFriendly = groupFriendly;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public PriceLevel getPriceLevel() { return priceLevel; }
    public SpiceLevel getSpiceLevel() { return spiceLevel; }
    public Set<Mood> getMoods() { return moods; }
    public boolean isGroupFriendly() { return groupFriendly; }
}
