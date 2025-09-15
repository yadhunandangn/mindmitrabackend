package com.MindMitra.Rolebased.DTO.Request;

import lombok.Data;

@Data
public class ChangePasswordReq {
    private String oldPassword;
    private String newPassword;
}