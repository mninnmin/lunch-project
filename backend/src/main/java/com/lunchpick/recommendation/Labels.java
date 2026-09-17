package com.lunchpick.recommendation;

import com.lunchpick.menu.*;

final class Labels {
    private Labels() {}
    static String category(Menu menu) { return switch (menu.getCategory()) { case KOREAN -> "한식"; case JAPANESE -> "일식"; case WESTERN -> "양식"; case CHINESE -> "중식"; case ASIAN -> "아시안"; }; }
    static String price(Menu menu) { return switch (menu.getPriceLevel()) { case VALUE -> "가성비"; case NORMAL -> "적당한 가격"; case PREMIUM -> "플렉스"; }; }
    static String spice(Menu menu) { return switch (menu.getSpiceLevel()) { case NONE -> "안 매운맛"; case MILD -> "살짝 매콤"; case HOT -> "화끈하게"; }; }
    static String mood(Mood mood) { return switch (mood) { case QUICK -> "빠르게"; case LIGHT -> "가볍게"; case STRESS -> "스트레스 해소"; case WARM -> "따뜻하게"; case COMFORT -> "익숙하게"; case SPECIAL -> "특별하게"; }; }
}
