package com.gianfcop.ss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrenotazioneDTOOut {

    private String id;
    private String giorno;
    private String oraInizio;
    private String oraFine;
    private String nomeStruttura;
    private int numeroPostiDisponibili;
    private int enableDelete;
}
