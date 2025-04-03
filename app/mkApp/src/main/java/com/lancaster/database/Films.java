package com.lancaster.database;

import java.util.Date;

public abstract class Films {
    public static class FilmFinancials{

    }
    public static class FilmEngagement{

    }
    public static class FilmScreening{

    }
    public static class FilmInformation {
        private int showId;
        private int filmId;
        private String filmTitle;
        private Date showDate;
        private int duration;
        private int ticketPrice;

        public FilmInformation(int showId, int filmId, String filmTitle, Date showDate, int duration,int ticketPrice) {
            this.showId = showId;
            this.filmId = filmId;
            this.filmTitle = filmTitle;
            this.showDate = showDate;
            this.duration = duration;
            this.ticketPrice = ticketPrice;
        }

        @Override
        public String toString() {
            return "FilmInformation{" +
                    "showId=" + showId +
                    ", filmId=" + filmId +
                    ", filmTitle='" + filmTitle + '\'' +
                    ", showDate=" + showDate +
                    ", duration=" + duration +
                    ", ticketPrice=" + ticketPrice +
                    '}';
        }

        public int getShowId() {
            return showId;
        }

        public void setShowId(int showId) {
            this.showId = showId;
        }

        public int getFilmId() {
            return filmId;
        }

        public void setFilmId(int filmId) {
            this.filmId = filmId;
        }

        public String getFilmTitle() {
            return filmTitle;
        }

        public void setFilmTitle(String filmTitle) {
            this.filmTitle = filmTitle;
        }

        public Date getShowDate() {
            return showDate;
        }

        public void setShowDate(Date showDate) {
            this.showDate = showDate;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getTicketPrice() {
            return ticketPrice;
        }

        public void setTicketPrice(int ticketPrice) {
            this.ticketPrice = ticketPrice;
        }
    }
}
