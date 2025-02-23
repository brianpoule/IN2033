import java.time.LocalDate;
import java.util.List;

public interface VenueCalendarService {
    /**
     * Retrieves all events happening within a given date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of events happening within the specified date range.
     */
    List<Event> getEventsInRange(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves events happening at a specific venue.
     *
     * @param venueId   The venue ID.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of events for the venue within the date range.
     */
    List<Event> getEventsByVenue(int venueId, LocalDate startDate, LocalDate endDate);
}
