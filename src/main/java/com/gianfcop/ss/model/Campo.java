package com.gianfcop.ss.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "campi")
public class Campo {

    @Id
    private int id;

    private String sport;
    private int prezzoOrario;
    private String oraInizioDisponibilita;
    private String oraFineDisponibilita;


    
}
