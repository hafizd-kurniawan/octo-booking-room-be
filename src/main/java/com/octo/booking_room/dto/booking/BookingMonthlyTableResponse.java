package com.octo.booking_room.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingMonthlyTableResponse {

  private List<MonthlyBookingSummary> months;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MonthlyBookingSummary {
    private Integer year;
    private Integer month;
    private String label;

    @JsonProperty("total_bookings")
    private Integer totalBookings;

    @JsonProperty("by_room")
    private List<RoomBookingCount> byRoom;

    @JsonProperty("by_room_type")
    private List<RoomTypeBookingCount> byRoomType;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoomBookingCount {
    @JsonProperty("room_id")
    private String roomId;

    @JsonProperty("room_name")
    private String roomName;

    private Integer count;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoomTypeBookingCount {
    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("type_name")
    private String typeName;

    private Integer count;
  }
}
