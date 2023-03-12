package com.gianfcop.ss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gianfcop.ss.model.Prenotazione;

@Repository
public interface PrenotazioniRepository extends JpaRepository<Prenotazione, Integer>{

    List<Prenotazione> findByIdUtente(String idUtente);
    List<Prenotazione> findByIdStrutturaAndGiorno(int idStruttura, String giorno);
    
}
