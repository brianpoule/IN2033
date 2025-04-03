package com.lancaster.database.BoxOfficeInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Films;
import com.lancaster.database.Promotions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class BoxOfficeData implements BoxOfficeInterface {

    @Override
    public Map<String, Promotions.PromotionDetails> getUpcomingEventPromotions() {
        return Map.of();
    }

    @Override
    public List<Bookings.PriorityBookings> getFriendsReservations(String membershipId) {
        return List.of();
    }

    @Override
    public Promotions.PromotionStats getPromotionalSalesTracking(String promotionCode) {
        return null;
    }

    @Override
    public List<Bookings.GroupBookings> getGroupHolds(int minimumGroupSize) {
        return List.of();
    }

    @Override
    public List<String> getReservedRows(String groupId) {
        return List.of();
    }

    @Override
    public Map<String, Double> getInstitutionalBookings(String institutionType) {
        return Map.of();
    }

    @Override
    public List<Films.FilmScreening> getFilmSchedule(LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public Films.FilmEngagement getFilmEngagement(String filmId) {
        return null;
    }

    @Override
    public Films.FilmFinancials getFilmFinancials(String filmId) {
        return null;
    }
}
