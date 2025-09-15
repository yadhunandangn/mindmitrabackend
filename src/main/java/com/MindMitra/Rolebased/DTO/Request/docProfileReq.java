package com.MindMitra.Rolebased.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class docProfileReq {
    private String doctorName;
    private String specialization;
    private String email;
    private String password;
    private String aboutDoc;
}
