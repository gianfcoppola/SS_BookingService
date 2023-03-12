package com.gianfcop.ss.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gianfcop.ss.model.Campo;

@Repository
public interface CampiRepository extends JpaRepository<Campo, Integer>{
    
}
