package com.lancaster.database.BoxOffice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class handling promotional data for Lancaster's Music Hall Box Office
 */
public class Promotions {

    /**
     * Inner class containing details about a specific promotion
     */
    public static class PromotionDetails {
        private String promotionCode;
        private String description;
        private double discountPercentage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private int maxRedemptions;
        private boolean isActive;
        private List<String> applicableEvents;

        // Constructor
        public PromotionDetails(String promotionCode, String description,
                                double discountPercentage, LocalDateTime startDate,
                                LocalDateTime endDate, int maxRedemptions,
                                List<String> applicableEvents) {
            this.promotionCode = promotionCode;
            this.description = description;
            this.discountPercentage = discountPercentage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.maxRedemptions = maxRedemptions;
            this.isActive = true;
            this.applicableEvents = applicableEvents;
        }

        // Getters and Setters
        public String getPromotionCode() { return promotionCode; }
        public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public double getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

        public int getMaxRedemptions() { return maxRedemptions; }
        public void setMaxRedemptions(int maxRedemptions) { this.maxRedemptions = maxRedemptions; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }

        public List<String> getApplicableEvents() { return applicableEvents; }
        public void setApplicableEvents(List<String> applicableEvents) { this.applicableEvents = applicableEvents; }
    }

    /**
     * Inner class containing statistics about promotion performance
     */
    public static class PromotionStats {
        private String promotionCode;
        private int timesRedeemed;
        private double totalDiscountGiven;
        private double averageTicketValue;
        private int numberOfTicketsSold;
        private LocalDateTime lastRedemptionDate;
        private double conversionRate;

        // Constructor
        public PromotionStats(String promotionCode) {
            this.promotionCode = promotionCode;
            this.timesRedeemed = 0;
            this.totalDiscountGiven = 0.0;
            this.averageTicketValue = 0.0;
            this.numberOfTicketsSold = 0;
            this.conversionRate = 0.0;
        }

        // Getters and Setters
        public String getPromotionCode() { return promotionCode; }
        public void setPromotionCode(String promotionCode) { this.promotionCode = promotionCode; }

        public int getTimesRedeemed() { return timesRedeemed; }
        public void setTimesRedeemed(int timesRedeemed) { this.timesRedeemed = timesRedeemed; }

        public double getTotalDiscountGiven() { return totalDiscountGiven; }
        public void setTotalDiscountGiven(double totalDiscountGiven) { this.totalDiscountGiven = totalDiscountGiven; }

        public double getAverageTicketValue() { return averageTicketValue; }
        public void setAverageTicketValue(double averageTicketValue) { this.averageTicketValue = averageTicketValue; }

        public int getNumberOfTicketsSold() { return numberOfTicketsSold; }
        public void setNumberOfTicketsSold(int numberOfTicketsSold) { this.numberOfTicketsSold = numberOfTicketsSold; }

        public LocalDateTime getLastRedemptionDate() { return lastRedemptionDate; }
        public void setLastRedemptionDate(LocalDateTime lastRedemptionDate) { this.lastRedemptionDate = lastRedemptionDate; }

        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }

        /**
         * Calculates the average discount per redemption
         * @return average discount amount
         */
        public double getAverageDiscountPerRedemption() {
            return timesRedeemed > 0 ? totalDiscountGiven / timesRedeemed : 0.0;
        }
    }
}
