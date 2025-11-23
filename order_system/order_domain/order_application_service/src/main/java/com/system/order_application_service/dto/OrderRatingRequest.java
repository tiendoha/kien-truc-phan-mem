package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for rating an order")
public class OrderRatingRequest {
    @Schema(description = "Rating score from 1 to 5", required = true, example = "5", minimum = "1", maximum = "5")
    private int rating;

    @Schema(description = "Optional comment about the order experience", example = "Great food and fast delivery!")
    private String comment;

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}