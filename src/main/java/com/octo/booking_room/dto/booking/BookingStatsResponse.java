package com.octo.booking_room.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.util.List;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatsResponse {
  @JsonProperty("total_bookings")
  private Integer totalBookings;
 
  @JsonProperty("by_room_type")
  private List<RoomTypeStat> byRoomType;

  @JsonProperty("by_room")
  private List<RoomStat> byRoom;
 
  @JsonProperty("by_month")
  private List<MonthStat> byMonth;
 
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoomTypeStat {
    @JsonProperty("type_id")
    private String typeId;
 
    @JsonProperty("type_name")
    private String typeName;
 
    private Integer count;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoomStat {
    @JsonProperty("room_id")
    private String roomId;

    @JsonProperty("room_name")
    private String roomName;

    private Integer floor;

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("type_name")
    private String typeName;

    private Integer count;
  }
 
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MonthStat {
    private Integer year;
    private Integer month;
    private String label;
    private Integer count;
  }
}
