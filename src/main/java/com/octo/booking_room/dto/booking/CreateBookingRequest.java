package com.octo.booking_room.dto.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingRequest {

  @NotBlank(message = "room_id is required")
  @JsonProperty("room_id")
  private String roomId;

  @NotNull(message = "date is required")
  private LocalDate date;

  @NotEmpty(message = "slots must not be empty")
  @Valid
  private List<SlotRequest> slots;

  /**
   * Optional. Admin-only: specify the target customer's email.
   * If provided by a non-admin, the service layer will throw an AccessDeniedException.
   * If omitted, the booking is created for the currently authenticated customer.
   */
  @Email(message = "customer_email must be a valid email")
  @JsonProperty("customer_email")
  private String customerEmail;

  @Data
  public static class SlotRequest {
    @NotNull(message = "start_hour is required")
    @JsonProperty("start_hour")
    private LocalDateTime startHour;

    @NotNull(message = "end_hour is required")
    @JsonProperty("end_hour")
    private LocalDateTime endHour;
  }
}