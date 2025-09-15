package com.MindMitra.Rolebased.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingReq {
    private Long userID;
    private Long docID;
    private String userName;
    private String doctorName;
    private String issue;
    private String desc;
    private LocalDate date;      // Only date
    private LocalTime time;
    private String status;// Only time
}
