package com.gianfcop.ss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gianfcop.ss.model.Abbonamento;

@Repository
public interface AbbonamentiRepository extends JpaRepository<Abbonamento, Integer>{

    List<Abbonamento> findByIdUtente(String idUtente);
    
}
