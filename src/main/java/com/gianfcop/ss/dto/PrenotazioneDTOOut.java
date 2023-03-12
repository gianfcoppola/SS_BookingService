package com.gianfcop.ss.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrenotazioneDTOOut {

    private String id;
    private String giorno;
    private String oraInizio;
    private String oraFine;
    private String nomeStruttura;
    private int numeroPostiDisponibili;
    private int enableDelete;
}
