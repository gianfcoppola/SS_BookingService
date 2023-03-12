package com.gianfcop.ss.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Abbonamenti")
public class Abbonamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	private String timestampPrenotazione;
	private String idUtente;
	private int idStruttura;
	private String dataInizioAbbonamento;
	private String dataFineAbbonamento;
	private int prezzoTotale;
    

}
