package com.gianfcop.ss.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PrenotazioneCercaDTOIn {
    
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String data;
    @Positive @Max(10)
    private int idStruttura;
}
