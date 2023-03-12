package com.gianfcop.ss.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Struttura {

    @Transient
    public static final String SEQUENCE_NAME = "strutture_sequence";

    @Id
    private int id;
    private int tipoStruttura;
    private String descrizione;
    private int prezzoOrario;
    private String oraInizioDisponibilita;
    private String oraFineDisponibilita;
    
}

