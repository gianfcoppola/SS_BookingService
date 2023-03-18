package com.gianfcop.ss.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gianfcop.ss.dto.AbbonamentoDTOIn;
import com.gianfcop.ss.service.AbbonamentiService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/abbonamenti")
@Slf4j
@Validated
public class AbbonamentoController {

    private AbbonamentiService abbonamentiService;

    public AbbonamentoController(AbbonamentiService abbonamentiService) {
        this.abbonamentiService = abbonamentiService;
    }

    private static final String ID_UTENTE = "idUtente";
    private static final String NOME_UTENTE = "nomeUtente";

    @GetMapping("/info")
    public String returnInfoAbbonamenti(Model model, @AuthenticationPrincipal Jwt jwt){
        

        model.addAttribute("infoAbbonamenti", abbonamentiService.getInfoAbbonamenti());
        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        

        return "info_abbonamenti";

    }

    
    @GetMapping("/new")
    public String returnCreaAbbonamento(Model model, @AuthenticationPrincipal Jwt jwt){

        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        
        // create Abbonamento object to hold prenotazione form data
		AbbonamentoDTOIn abbonamentoDTOIn = new AbbonamentoDTOIn();

        String dataDiOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String dataDiDomani = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        model.addAttribute("oggi", dataDiOggi);
        model.addAttribute("domani", dataDiDomani);
        model.addAttribute("infoAbbonamenti", abbonamentiService.getCreazioneAbbonamentoInfo());
		model.addAttribute("abbonamentoDTOIn", abbonamentoDTOIn);
        return "crea_abbonamento";
    }

    
    @PostMapping("/new")
	public String insertAbbonamento(@ModelAttribute("abbonamentoDTOIn") @Valid AbbonamentoDTOIn abbonamentoDTOIn, Model model, @AuthenticationPrincipal Jwt jwt) {
        

        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        
        abbonamentoDTOIn.setDataInizioAbbonamento(LocalDate.now().toString());
		abbonamentiService.insertAbbonamento(abbonamentoDTOIn, idUtente, jwt.getTokenValue());
        log.info("abbonamento inserito");
        model.addAttribute("abbonamentoInserito", "1");
        model.addAttribute("abbonamentiUtente", abbonamentiService.getAbbonamentiByIdUtente(idUtente));
        return "abbonamenti_utente";
        
	}

    
    @GetMapping("/{idUtente}")
    public String listaMieiAbbonamenti(
            Model model, 
            @PathVariable("idUtente") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String idUser, 
            @AuthenticationPrincipal Jwt jwt){
        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        model.addAttribute("abbonamentiUtente", abbonamentiService.getAbbonamentiByIdUtente(idUser));
        return "abbonamenti_utente";
    }

    
}
