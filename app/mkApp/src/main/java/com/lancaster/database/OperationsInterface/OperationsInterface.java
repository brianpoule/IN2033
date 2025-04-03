package com.lancaster.database.OperationsInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Events;
import com.lancaster.database.Films;

import java.util.List;

/**
 * Interface defining operations team access to marketing data at Lancaster's Music Hall.
 * Provides read-only access to group bookings, film shows, priority seating, and marketing events.
 */
public interface OperationsInterface {

    /**
     * Retrieves group booking information by ID
     * @param groupBookingId Unique identifier for the group booking
     * @return GroupBooking object containing booking details
     */
    Bookings.GroupBookings getGroupBooking(int groupBookingId);

    /**
     * Retrieves all group bookings for a specific date
     * @param eventDate Date in format "YYYYMMDD"
     * @return List of GroupBooking objects
     */
    List<Bookings.GroupBookings> getGroupBookingsByDate(String eventDate);

    /**
     * Retrieves film show information by ID
     * @param filmShowId Unique identifier for the film show
     * @return FilmShow object containing show details
     */
    Films.FilmInformation getFilmShow(int filmShowId);

    /**
     * Retrieves all film shows scheduled for a specific date
     * @param showDate Date in format "YYYYMMDD"
     * @return List of FilmShow objects
     */
    List<Films.FilmInformation> getFilmShowsByDate(String showDate);

    /**
     * Retrieves priority seat reservation by ID
     * @param prioritySeatId Unique identifier for the priority seat reservation
     * @return PrioritySeat object containing reservation details
     */
    Bookings.PriorityBookings getPrioritySeat(int prioritySeatId);

    /**
     * Retrieves all priority seat reservations for Friends of Lancaster's within a date range
     * @param startDate Start date in format "YYYYMMDD"
     * @param endDate End date in format "YYYYMMDD"
     * @return List of PrioritySeat objects
     */
    List<Bookings.PriorityBookings> getPrioritySeats(String startDate, String endDate);

    /**
     * Retrieves marketing event information by ID
     * @param marketingEventId Unique identifier for the marketing event
     * @return MarketingEvent object containing event details
     */
    Events getMarketingEvent(int marketingEventId);

    /**
     * Retrieves all marketing events scheduled for a specific date
     * @param eventDate Date in format "YYYYMMDD"
     * @return List of MarketingEvent objects
     */
    List<Events> getMarketingEventsByDate(String eventDate);

    /**
     * Retrieves all events (including film shows and marketing events) scheduled for a specific room
     * @param roomId Room identifier (e.g., "C101")
     * @param date Date in format "YYYYMMDD"
     * @return List of Event objects
     */
    List<Events> getRoomSchedule(String roomId, String date);
}
