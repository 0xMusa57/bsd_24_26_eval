package edu.ban7.bsd_eval.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassementDto {
    private String email;
    private int score; // score = total des points (chaque écart = -1 point)
}
