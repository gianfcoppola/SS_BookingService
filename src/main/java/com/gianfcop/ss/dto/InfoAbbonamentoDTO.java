package com.gianfcop.ss.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfoAbbonamentoDTO {
    
    private int idAbbonamento;
    private String struttura;
    private int numeroIngressiSettimana;
    private int prezzoMensile;
}
