package com.gianfcop.ss.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AbbonamentoDTOOut {

    private int idAbbonamento;
    private String struttura;
    private int numeroIngressiSettimana;
    private String dataInizioAbbonamento;
	private String dataFineAbbonamento;
	private int prezzoTotale;
    
}
