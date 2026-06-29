package edu.ban7.bsd_eval.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
