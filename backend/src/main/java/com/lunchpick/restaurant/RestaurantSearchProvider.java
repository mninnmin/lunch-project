package com.lunchpick.restaurant;

import java.util.List;

/** Kakao 또는 Naver Local/Map API 어댑터가 구현할 계약입니다. */
public interface RestaurantSearchProvider {
    List<RestaurantResult> search(String menuName, Location origin);
    record Location(double latitude, double longitude) {}
    record RestaurantResult(String externalId, String name, String address, double distanceMeters,
                            Integer walkingMinutes, String routeUrl, String menuName, Integer priceWon) {}
}
