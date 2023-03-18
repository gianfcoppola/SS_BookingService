package com.gianfcop.ss.mapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import com.gianfcop.ss.dto.AbbonamentoDTOIn;
import com.gianfcop.ss.dto.AbbonamentoDTOOut;
import com.gianfcop.ss.model.Abbonamento;

public class AbbonamentoMapper {

    private AbbonamentoMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Abbonamento toAbbonamento (AbbonamentoDTOIn abbonamentoDTOIn, String idUtente, int prezzoMensileAbbonamento){

        String dataFineAbbonamento = abbonamentoDTOIn.getDataFineAbbonamento();
        String dataInizioAbbonamento = abbonamentoDTOIn.getDataInizioAbbonamento();
        
        Period diff = Period.between(LocalDate.parse(dataInizioAbbonamento), LocalDate.parse(dataFineAbbonamento));
        int numeroMesiAbbonamento = diff.getMonths() + 1;
        int prezzoAbbonamento = prezzoMensileAbbonamento * numeroMesiAbbonamento;

        return Abbonamento.builder()
            .idUtente(idUtente)
            .idStruttura(abbonamentoDTOIn.getIdStruttura())
            .dataInizioAbbonamento(LocalDate.parse(abbonamentoDTOIn.getDataInizioAbbonamento()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            .dataFineAbbonamento(LocalDate.parse(abbonamentoDTOIn.getDataFineAbbonamento()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            .prezzoTotale(prezzoAbbonamento)
            .timestampPrenotazione(new Timestamp(System.currentTimeMillis()).toString())
            .build();
    }
    
    public static AbbonamentoDTOOut toAbbonamentoDTOOut(Abbonamento abbonamento, String struttura){

        return AbbonamentoDTOOut.builder()
            .dataFineAbbonamento(abbonamento.getDataFineAbbonamento())
            .dataInizioAbbonamento(abbonamento.getDataInizioAbbonamento())
            .idAbbonamento(abbonamento.getId())
            .numeroIngressiSettimana(3)
            .prezzoTotale(abbonamento.getPrezzoTotale())
            .struttura(struttura)
            .build();
    }
}
