package com.gianfcop.ss.mapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.gianfcop.ss.dto.PrenotazioneDTOIn;
import com.gianfcop.ss.dto.PrenotazioneDTOOut;
import com.gianfcop.ss.model.Prenotazione;

public class PrenotazioneMapper {


    public static Prenotazione toPrenotazione(PrenotazioneDTOIn prenotazioneDTOIn, String idUtente){
        String data = prenotazioneDTOIn.getGiorno();
        int giorno, mese, anno;
        giorno = Integer.valueOf(data.substring(8));
        mese = Integer.valueOf(data.substring(5, 7));
        anno = Integer.valueOf(data.substring(0, 4));
        String dataPrenotazione = LocalDate.of(anno, mese, giorno).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();

       
        Prenotazione nuovaPrenotazione = Prenotazione.builder()
            .giorno(dataPrenotazione)
            .idUtente(idUtente)
            .oraInizio(prenotazioneDTOIn.getOraInizio())
            .oraFine(prenotazioneDTOIn.getOraFine())
            .idStruttura(prenotazioneDTOIn.getIdStruttura())
            .timestampPrenotazione(new Timestamp(System.currentTimeMillis()).toString())
            .build();
            
        return nuovaPrenotazione;
    }

    public static PrenotazioneDTOOut toPrenotazioneDTOOut(Prenotazione prenotazione, String nomeStruttura){

        int enableDelete;
        LocalDateTime now = LocalDateTime.now();
        int giorno, mese, anno, ora, minuto;
        giorno = Integer.valueOf(prenotazione.getGiorno().substring(0,2));
        mese = Integer.valueOf(prenotazione.getGiorno().substring(3,5));
        anno = Integer.valueOf(prenotazione.getGiorno().substring(6));
        ora = Integer.valueOf(prenotazione.getOraInizio().substring(0,2));
        minuto = Integer.valueOf(prenotazione.getOraInizio().substring(3));
        LocalDateTime dataOraPrenotazione = LocalDateTime.of(anno, mese, giorno, ora, minuto);
        
        if(dataOraPrenotazione.isBefore(now))
            enableDelete = 0;
        else
            enableDelete = 1;
        return PrenotazioneDTOOut.builder()
            .id(String.valueOf(prenotazione.getId()))
            .giorno(prenotazione.getGiorno())
            .oraInizio(prenotazione.getOraInizio())
            .oraFine(prenotazione.getOraFine())
            .nomeStruttura(nomeStruttura)
            .enableDelete(enableDelete)
            .build();
        
    }

    public static PrenotazioneDTOOut toPrenotazioneDTOOut2(Prenotazione prenotazione, String nomeStruttura, int numeroPostiDisponibili){

        return PrenotazioneDTOOut.builder()
            .id(String.valueOf(prenotazione.getId()))
            .giorno(prenotazione.getGiorno())
            .oraInizio(prenotazione.getOraInizio())
            .oraFine(prenotazione.getOraFine())
            .nomeStruttura(nomeStruttura)
            .numeroPostiDisponibili(numeroPostiDisponibili)
            .build();
        
    }

    public static List<PrenotazioneDTOOut> toPrenotazioneDTOOutList(List<Prenotazione> prenotazioni, String nomeStruttura){
        List<PrenotazioneDTOOut> prenotazioneDTOOuts = new ArrayList<>();
        for(Prenotazione p: prenotazioni)
            prenotazioneDTOOuts.add(toPrenotazioneDTOOut(p, nomeStruttura));
        return prenotazioneDTOOuts;
    }

    public static List<PrenotazioneDTOOut> toPrenotazioneDTOOutList2(List<Prenotazione> prenotazioni, String nomeStruttura, int numeroPostiDisponibili){
        List<PrenotazioneDTOOut> prenotazioneDTOOuts = new ArrayList<>();
        
        for(Prenotazione p: prenotazioni)
            prenotazioneDTOOuts.add(toPrenotazioneDTOOut2(p, nomeStruttura, numeroPostiDisponibili));
        return prenotazioneDTOOuts;
    }
    
}
