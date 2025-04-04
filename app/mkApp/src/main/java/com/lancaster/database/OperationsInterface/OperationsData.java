package com.lancaster.database.OperationsInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Events;
import com.lancaster.database.Films;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OperationsData implements OperationsInterface {


    public Bookings.GroupBookings getGroupBooking(int groupBookingId, Connection connection) throws SQLException {
        String query = "SELECT booking_id people, date, room, event FROM group_bookings WHERE booking_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupBookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Bookings.GroupBookings(
                            resultSet.getInt("booking_id"),
                            resultSet.getInt("people"),
                            resultSet.getDate("date"),
                            resultSet.getString("room"),
                            resultSet.getString("event")
                    );
                }
            }
        }

        return null;
    }


    public List<Bookings.GroupBookings> getGroupBookingsByDate(String eventDate, Connection connection) throws SQLException {
        String query = "SELECT booking_id, people, date, room, event FROM group_bookings WHERE date = ?";
        List<Bookings.GroupBookings> groupBookingsList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(eventDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bookings.GroupBookings groupBooking = new Bookings.GroupBookings(
                            resultSet.getInt("booking_id"),
                            resultSet.getInt("people"),
                            resultSet.getDate("date"),
                            resultSet.getString("room"),
                            resultSet.getString("event")
                    );
                    groupBookingsList.add(groupBooking);
                }
            }
        }

        return groupBookingsList;
    }

    public Films.FilmInformation getFilmShow(int showId, Connection connection) throws SQLException {
        String query = "SELECT showId, filmId, filmTitle, showDate, duration, ticketPrice FROM film_bookings WHERE filmId = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, showId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Films.FilmInformation(
                            resultSet.getInt("showId"),
                            resultSet.getInt("filmId"),
                            resultSet.getString("filmTitle"),
                            resultSet.getDate("showDate"),
                            resultSet.getInt("duration"),
                            resultSet.getDate("endDate"),
                            resultSet.getInt("ticketPrice")
                    );
                }
            }
        }

        return null;
    }

    public List<Films.FilmInformation> getFilmShowsByDate(String showDate, Connection connection) throws SQLException {
        String query = "SELECT showId, filmId, filmTitle, showDate, duration, ticketPrice FROM film_bookings WHERE showDate = ?";
        List<Films.FilmInformation> filmShows = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, showDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Films.FilmInformation filmInformation = new Films.FilmInformation(
                            resultSet.getInt("showId"),
                            resultSet.getInt("filmId"),
                            resultSet.getString("filmTitle"),
                            resultSet.getDate("showDate"),
                            resultSet.getInt("duration"),
                            resultSet.getDate("endDate"),
                            resultSet.getInt("ticketPrice")
                    );
                    filmShows.add(filmInformation);
                }
            }
        }

        return filmShows;
    }


    public Bookings.PriorityBookings getPrioritySeat(int prioritySeatId, Connection connection) throws SQLException {
        Bookings.PriorityBookings priorityBooking = null;
        String query = "SELECT * FROM priority_bookings WHERE priorityID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, prioritySeatId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    priorityBooking = new Bookings.PriorityBookings(
                            resultSet.getInt("priorityID"),
                            resultSet.getString("room"),
                            resultSet.getInt("row"),
                            resultSet.getInt("seat"),
                            resultSet.getInt("eventID"),
                            resultSet.getDate("date"),
                            resultSet.getInt("friendID")
                    );
                }
            }
        }

        return priorityBooking;
    }


    public List<Bookings.PriorityBookings> getPrioritySeats(String startDate, String endDate, Connection connection) throws SQLException {
        List<Bookings.PriorityBookings> priorityBookingsList = new ArrayList<>();
        String query = "SELECT * FROM priority_bookings WHERE date BETWEEN ? AND ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Bookings.PriorityBookings priorityBooking = new Bookings.PriorityBookings(
                            resultSet.getInt("priorityID"),
                            resultSet.getString("room"),
                            resultSet.getInt("row"),
                            resultSet.getInt("seat"),
                            resultSet.getInt("eventID"),
                            resultSet.getDate("date"),
                            resultSet.getInt("friendID")
                    );
                    priorityBookingsList.add(priorityBooking);
                }
            }
        }

        return priorityBookingsList;
    }

    public Events getMarketingEvent(int marketingEventId, Connection connection) {
        Events event = null;
        String query = "SELECT * FROM events WHERE eventId = ? AND type = 'marketing_events'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, marketingEventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    event = new Events(
                            resultSet.getInt("eventId"),
                            resultSet.getString("type"),
                            resultSet.getDate("date"),
                            resultSet.getInt("room"),
                            resultSet.getInt("duration")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return event;
    }

    public List<Events> getMarketingEventsByDate(String eventDate, Connection connection) throws SQLException {
        List<Events> eventsList = new ArrayList<>();
        String query = "SELECT * FROM events WHERE date = ? AND type = 'marketing_events'";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, eventDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Events event = new Events(
                            resultSet.getInt("eventId"),
                            resultSet.getString("type"),
                            resultSet.getDate("date"),
                            resultSet.getInt("room"),
                            resultSet.getInt("duration")
                    );
                    eventsList.add(event);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventsList;
    }

    public List<Events> getRoomSchedule(String roomId, String date, Connection connection) throws SQLException {
        List<Events> eventsList = new ArrayList<>();
        String query = "SELECT * FROM events WHERE room = ? AND date = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, roomId);
            statement.setString(2, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Events event = new Events(
                            resultSet.getInt("eventId"),
                            resultSet.getString("type"),
                            resultSet.getDate("date"),
                            resultSet.getInt("room"),
                            resultSet.getInt("duration")
                    );
                    eventsList.add(event);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventsList;
    }
}
