package com.octo.booking_room.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebResponse<T> {
    private String status;
    private String message;
    private T data;

}
