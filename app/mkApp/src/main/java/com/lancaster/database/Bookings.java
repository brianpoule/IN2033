package com.lancaster.database;

import java.util.Date;

public abstract class Bookings {
    public static class PriorityBookings {
        private int priorityID;
        private String room;
        private int row;
        private int seat;
        private int eventID;
        private Date date;
        private int friendID;

        // Parameterized constructor
        public PriorityBookings(int priorityID, String room, int row, int seat, int eventID, Date date, int friendID) {
            this.priorityID = priorityID;
            this.room = room;
            this.row = row;
            this.seat = seat;
            this.eventID = eventID;
            this.date = date;
            this.friendID = friendID;
        }

        // Getters
        public int getPriorityID() {
            return priorityID;
        }

        public String getRoom() {
            return room;
        }

        public int getRow() {
            return row;
        }

        public int getSeat() {
            return seat;
        }

        public int getEventID() {
            return eventID;
        }

        public Date getDate() {
            return date;
        }

        public int getFriendID() {
            return friendID;
        }
    }
    public static class GroupBookings{
        private int bookingID;
        private int people;
        private Date date;
        private String room;
        private String event;


        public GroupBookings(int bookingID, int people,Date date, String room,  String event ) {
            this.bookingID = bookingID;
            this.people = people;
            this.room = room;
            this.event = event;
            this.date = date;
        }

        public int getBookingID() {
            return bookingID;
        }

        public int getPeople() {
            return people;
        }

        public String getRoom() {
            return room;
        }

        public String getEvent() {
            return event;
        }

        public Date getDate() {
            return date;
        }
    }
}
