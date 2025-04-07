package com.lancaster.database.BoxOfficeInterface;

import com.lancaster.database.Bookings;
import com.lancaster.database.Films;
import com.lancaster.database.Promotions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoxOfficeData implements BoxOfficeInterface {


    private Connection connection;

    @Override
    public Map<String, Promotions.PromotionDetails> getUpcomingEventPromotions() {
        return Map.of();
    }

    @Override
    public List<Bookings.PriorityBookings> getFriendsReservations(String membershipId) {
        List<Bookings.PriorityBookings> bookings = new ArrayList<>();
        String query = "SELECT * FROM priority_bookings WHERE friendID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bookings.PriorityBookings booking = new Bookings.PriorityBookings(
                        rs.getInt("priorityID"),
                        rs.getString("room"),
                        rs.getInt("row"),
                        rs.getInt("seat"),
                        rs.getInt("eventID"),
                        rs.getDate("date"),
                        rs.getInt("friendID")
                );
                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    @Override
    public Promotions.PromotionStats getPromotionalSalesTracking(String promotionCode) {
        return null;
    }

    @Override
    public List<Bookings.GroupBookings> getGroupHolds(int minimumGroupSize) {
        List<Bookings.GroupBookings> groupHolds = new ArrayList<>();
        String query = "SELECT * FROM group_bookings WHERE people >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, minimumGroupSize);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bookings.GroupBookings group = new Bookings.GroupBookings(
                        rs.getInt("booking_id"),
                        rs.getInt("people"),
                        rs.getDate("date"),
                        rs.getString("room"),
                        rs.getString("event")
                );
                groupHolds.add(group);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupHolds;
    }


    public List<String> getReservedRows(String groupId, Connection connection) throws SQLException {
        List<String> reservedRows = new ArrayList<>();
        String query = "SELECT reservedRows FROM group_bookings WHERE booking_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservedRows.add(resultSet.getString("reservedRows"));
                }
            }
        }

        return reservedRows;
    }
    @Override
    public Map<String, Double> getInstitutionalBookings(String institutionType) {
        return Map.of();
    }


    public List<Films.FilmInformation> getFilmSchedule(LocalDateTime startDate, LocalDateTime endDate, Connection connection) throws SQLException {
        List<Films.FilmInformation> filmInformations = new ArrayList<>();
        String query = "SELECT * FROM films WHERE showDate BETWEEN ? AND ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);

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
                    filmInformations.add(filmInformation);
                }
            }
        }

        return filmInformations;
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