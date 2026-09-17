package com.lunchpick.menu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

@Component
public class MenuDataInitializer implements CommandLineRunner {
    private final MenuRepository repository;
    public MenuDataInitializer(MenuRepository repository) { this.repository = repository; }
    @Override public void run(String... args) {
        if (repository.count() > 0) return;
        repository.saveAll(List.of(
            menu("김치찌개", "🍲", "칼칼한 국물과 든든한 한 끼가 필요할 때", Category.KOREAN, PriceLevel.VALUE, SpiceLevel.HOT, true, Mood.WARM, Mood.QUICK),
            menu("비빔밥", "🥗", "다채로운 나물로 가볍고 균형 있게", Category.KOREAN, PriceLevel.VALUE, SpiceLevel.MILD, true, Mood.LIGHT),
            menu("불고기 정식", "🍚", "달큰한 불고기와 정갈한 반찬의 조합", Category.KOREAN, PriceLevel.NORMAL, SpiceLevel.NONE, true, Mood.WARM, Mood.COMFORT),
            menu("돼지국밥", "🥣", "진한 국물로 속까지 든든하게", Category.KOREAN, PriceLevel.NORMAL, SpiceLevel.NONE, true, Mood.WARM, Mood.QUICK),
            menu("제육볶음", "🔥", "매콤한 양념으로 오후까지 힘차게", Category.KOREAN, PriceLevel.NORMAL, SpiceLevel.HOT, true, Mood.STRESS, Mood.WARM),
            menu("돈카츠", "🍱", "바삭한 튀김옷과 촉촉한 고기의 정석", Category.JAPANESE, PriceLevel.NORMAL, SpiceLevel.NONE, true, Mood.COMFORT, Mood.QUICK),
            menu("초밥", "🍣", "산뜻하고 기분 좋은 한 점의 여유", Category.JAPANESE, PriceLevel.PREMIUM, SpiceLevel.NONE, true, Mood.LIGHT, Mood.SPECIAL),
            menu("메밀소바", "🍜", "깔끔한 육수로 산뜻하게 즐기는 점심", Category.JAPANESE, PriceLevel.VALUE, SpiceLevel.NONE, false, Mood.LIGHT, Mood.QUICK),
            menu("탄탄멘", "🥢", "고소하고 얼큰한 국물이 당기는 날", Category.JAPANESE, PriceLevel.NORMAL, SpiceLevel.HOT, false, Mood.STRESS, Mood.WARM),
            menu("토마토 파스타", "🍝", "상큼한 토마토 소스의 실패 없는 선택", Category.WESTERN, PriceLevel.NORMAL, SpiceLevel.NONE, true, Mood.COMFORT, Mood.SPECIAL),
            menu("그릴드 치킨 샐러드", "🥬", "담백한 단백질까지 챙긴 산뜻한 한 그릇", Category.WESTERN, PriceLevel.NORMAL, SpiceLevel.NONE, false, Mood.LIGHT),
            menu("수제 버거", "🍔", "육즙 가득한 패티로 제대로 즐기는 점심", Category.WESTERN, PriceLevel.PREMIUM, SpiceLevel.NONE, true, Mood.COMFORT, Mood.SPECIAL),
            menu("짜장면", "🍜", "빠르고 맛있게 즐기는 익숙한 클래식", Category.CHINESE, PriceLevel.VALUE, SpiceLevel.NONE, true, Mood.QUICK, Mood.COMFORT),
            menu("마라탕", "🌶️", "취향대로 담아 얼얼하게 스트레스 해소", Category.CHINESE, PriceLevel.NORMAL, SpiceLevel.HOT, true, Mood.STRESS, Mood.WARM),
            menu("쌀국수", "🍜", "향긋한 육수와 부드러운 면으로 편안하게", Category.ASIAN, PriceLevel.NORMAL, SpiceLevel.MILD, true, Mood.LIGHT, Mood.WARM),
            menu("버터 치킨 커리", "🍛", "부드럽고 향긋한 커리로 색다른 한 끼", Category.ASIAN, PriceLevel.NORMAL, SpiceLevel.MILD, true, Mood.COMFORT, Mood.WARM),
            menu("치킨 부리토", "🌯", "한 손에 꽉 찬 맛과 영양을 간편하게", Category.ASIAN, PriceLevel.NORMAL, SpiceLevel.MILD, false, Mood.QUICK, Mood.SPECIAL),
            menu("연어 포케", "🥑", "신선한 연어와 채소로 활력 충전", Category.ASIAN, PriceLevel.PREMIUM, SpiceLevel.NONE, false, Mood.LIGHT, Mood.SPECIAL)
        ));
    }
    private Menu menu(String name, String emoji, String description, Category category, PriceLevel price,
                      SpiceLevel spice, boolean groupFriendly, Mood... moods) {
        return new Menu(name, emoji, description, category, price, spice, Set.of(moods), groupFriendly);
    }
}
