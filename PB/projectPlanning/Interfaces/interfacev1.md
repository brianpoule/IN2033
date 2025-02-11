```
title: Sharing interfaces
author: Filippo Vicini
date of publication: 11/02/2024
version number: v1.0
```

## What is an interface
An Interface in Java programming language is defined as an abstract type used to specify the behavior of a class.
In our case we need interfaces to understand what data to access from other teams.
"You need to share the data not the database"

## What we need to provide
We need to provide means by which other teams can access our data without accessing our database. We can control what other teams can
do with our data.

By week 4 we are expected to provide:
- Specification documents
    - what we need from operations
    - what we need from box office
- Java interfaces
    - How we are going to provide access to requested data



## Marketing Team Data Requirements from Operations
### Introduction
The marketing team needs access different data from the operations team to optimize film scheduling, group bookings, and venue promotions. The following specification outlines the required data and its expected format.

### Venue calendar data
- **Why?**: To find empty data slots to display films and other events
- **Required Data**:
    - `event_id`
    - `event_name`
    - `venue_id`
    - `venue_name`
    - `date`
    - `start_time`
    - `end_time`
    - `status`

### Seating data
- **Why?**:  To allocate seats for group bookings and manage accessibility needs.
- **Required Data**:
    - `venue_id`
    - `seat_id`
    - `row_number`
    - `seat_number`
    - `status`
    - `restricted_view`
    - `date`
    - `start_time`
    - `end_time`



## Specification for box office
The marketing team requires access to various data points from the box office team to manage group bookings, analyze sales performance, and optimize promotions. The following specification outlines the necessary data and its expected format.

### Bookings data
- **Why?**: To plan sales for group bookings
- **Required Data**
    - `booking_id`
    - `customer_id`
    - `event_id`
    - `price`
    - `quantity`
    - `booking_date`
    - `discount_type`

### Customer data
- **Why?**: Personalized communication for "Friends"
- **Required data**
    - `customer_id`
    - `customer_name`
    - `email`
    - `phone_number`
    - `friend_member`
