package com.MindMitra.Rolebased.DTO.Response;


import com.MindMitra.Rolebased.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRes {
private String token;
private String username;
private Long userID;
private String role;
}
