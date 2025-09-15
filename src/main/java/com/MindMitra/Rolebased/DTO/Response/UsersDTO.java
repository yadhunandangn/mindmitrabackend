package com.MindMitra.Rolebased.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class UsersDTO {
    private Long id;
    private String username;
    private String email;
    private Long totalAppointments;
}