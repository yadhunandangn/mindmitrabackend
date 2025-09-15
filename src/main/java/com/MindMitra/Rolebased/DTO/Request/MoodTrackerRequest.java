package com.MindMitra.Rolebased.DTO.Request;


import lombok.Data;

@Data
public class MoodTrackerRequest {
    private String mood;
    private String note;
}