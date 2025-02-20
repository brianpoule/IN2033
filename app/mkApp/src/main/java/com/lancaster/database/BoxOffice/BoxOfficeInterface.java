package com.lancaster.database.BoxOffice;

import com.lancaster.database.Bookings;
import com.lancaster.database.Films;
import com.lancaster.database.Promotions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface defining the communication between Box Office and Marketing
 * for Lancaster's Music Hall system
 */
public interface BoxOfficeInterface {
    /**
     * Retrieves upcoming events and their associated promotions
     * @return Map of events and their promotion details
     */
    Map<String, Promotions.PromotionDetails> getUpcomingEventPromotions();

    /**
     * Retrieves priority ticket reservations for 'Friends of Lancaster's'
     * @param membershipId the membership ID to check
     * @return List of available priority reservations
     */
    List<Bookings.PriorityBookings> getFriendsReservations(String membershipId);

    /**
     * Tracks promotional ticket sales
     * @param promotionCode the promotion code to track
     * @return Statistics about the promotion's performance
     */
    Promotions.PromotionStats getPromotionalSalesTracking(String promotionCode);

    // Group bookings and reservations
    /**
     * Gets information about held seats for large groups
     * @param minimumGroupSize minimum size to consider (typically 12)
     * @return List of current group holds
     */
    List<Bookings.GroupBookings> getGroupHolds(int minimumGroupSize);

    /**
     * Confirms reserved rows for group bookings
     * @param groupId the group booking ID
     * @return List of reserved row numbers
     */
    List<String> getReservedRows(String groupId);

    /**
     * Retrieves institutional booking details
     * @param institutionType type of institution (SCHOOL, COLLEGE, UNIVERSITY)
     * @return Map of bookings and their applied discounts
     */
    Map<String, Double> getInstitutionalBookings(String institutionType);

    // Film screening schedules
    /**
     * Gets all scheduled films and their showtimes
     * @param startDate beginning of date range
     * @param endDate end of date range
     * @return List of film screenings
     */
    List<Films.FilmScreening> getFilmSchedule(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves real-time sales and audience data for films
     * @param filmId the film ID to check
     * @return Current sales and audience engagement metrics
     */
    Films.FilmEngagement getFilmEngagement(String filmId);

    /**
     * Retrieves cost and revenue data for film screenings
     * @param filmId the film ID to analyze
     * @return Financial metrics for the film
     */
    Films.FilmFinancials getFilmFinancials(String filmId);
}
