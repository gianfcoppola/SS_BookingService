package com.gianfcop.ss.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbbonamentoDTOIn {
    
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
	private String dataInizioAbbonamento;
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
	private String dataFineAbbonamento;
	@Positive @Min(3) @Max(10)
	private int idStruttura;
}
