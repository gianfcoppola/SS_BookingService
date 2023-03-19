package com.gianfcop.ss.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.gianfcop.ss.dto.PrenotazioneCercaDTOIn;
import com.gianfcop.ss.dto.PrenotazioneDTOIn;
import com.gianfcop.ss.service.PrenotazioniService;
import com.gianfcop.ss.service.StruttureService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@Validated
public class PrenotazioneController {
    
    @Autowired
    private PrenotazioniService prenotazioniService;

    @Autowired
    private StruttureService struttureService;

    private static final String ID_UTENTE = "idUtente";
    private static final String NOME_UTENTE = "nomeUtente";
    private static final String PRENOTAZIONI_UTENTE = "prenotazioni";
    private static final String PRENOTAZIONI_UTENTE_PAGE = "prenotazioni-utente";


    @GetMapping("/prenotazioni/index")
    public String home(){
        return "index";
    } 

    @GetMapping("/prenotazioni/new")
    public String returnCreaPrenotazione(Model model, @AuthenticationPrincipal Jwt jwt){

        
        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        
		PrenotazioneDTOIn prenotazioneDTOIn = new PrenotazioneDTOIn();

        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        model.addAttribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		model.addAttribute("prenotazioneDTOIn", prenotazioneDTOIn);
        model.addAttribute("infoStrutture", struttureService.getInfoStrutture(jwt.getTokenValue()));
        return "crea_prenotazione";
    }

    @PostMapping("/prenotazioni/new")
	public String savePrenotazione(@ModelAttribute("prenotazioneDTOIn") @Valid PrenotazioneDTOIn prenotazioneDTOIn, Model model, @AuthenticationPrincipal Jwt jwt) {

        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);

        if(prenotazioniService.insertPrenotazione(prenotazioneDTOIn, idUtente, jwt.getTokenValue())){
            log.info("prenotazione inserita");
            
            model.addAttribute("prenotazioneInserita", "1");
            model.addAttribute(PRENOTAZIONI_UTENTE, prenotazioniService.getPrenotazioneByIdUtente(idUtente, jwt.getTokenValue()));
            return PRENOTAZIONI_UTENTE_PAGE;
        }
        else{
            String data = prenotazioneDTOIn.getGiorno();
            int idStruttura = prenotazioneDTOIn.getIdStruttura();
            int giorno;
            int mese;
            int anno;
            giorno = Integer.valueOf(data.substring(8));
            mese = Integer.valueOf(data.substring(5, 7));
            anno = Integer.valueOf(data.substring(0, 4));
            String dataPrenotazione = LocalDate.of(anno, mese, giorno).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            model.addAttribute("prenotazioneNonInserita", "1");
            model.addAttribute("nomeStruttura", struttureService.getNomeStruttura(idStruttura, jwt.getTokenValue()));
            model.addAttribute("dataPrenotazioneRichiesta", dataPrenotazione);
            model.addAttribute("prenotazioniDisponibili", prenotazioniService.getPrenotazioneLibere(idStruttura, dataPrenotazione, jwt.getTokenValue()));
            return "prenotazioni-disponibili";
        }
            
        
	}

    @GetMapping("/prenotazioni/{idUtente}")
    public String listaMiePrenotazioni(
            Model model, 
            @PathVariable("idUtente") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") String idUser, 
            @AuthenticationPrincipal Jwt jwt){

        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        model.addAttribute(PRENOTAZIONI_UTENTE, prenotazioniService.getPrenotazioneByIdUtente(idUser, jwt.getTokenValue()));
        return PRENOTAZIONI_UTENTE_PAGE;
    }
  


    @GetMapping("/prenotazioni/disponibili/cerca") 
    public String cercaPrenotazioniDisponibili(Model model, @AuthenticationPrincipal Jwt jwt){

        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        model.addAttribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        PrenotazioneCercaDTOIn prenotazioneCercaDTOIn = new PrenotazioneCercaDTOIn();
        model.addAttribute("prenotazioneCercaDTOIn", prenotazioneCercaDTOIn);
        model.addAttribute("infoStrutture", struttureService.getInfoStrutture(jwt.getTokenValue()));
        return "prenotazioni-disponibili-cerca";
    }

    @PostMapping("/prenotazioni/disponibili/cerca")
	public String cercaPrenotazione(@ModelAttribute("prenotazioneCercaDTOIn") @Valid PrenotazioneCercaDTOIn prenotazioneCercaDTOIn, Model model, @AuthenticationPrincipal Jwt jwt) {
        
        String data = prenotazioneCercaDTOIn.getData();
        int giorno;
        int mese;
        int anno;
        giorno = Integer.valueOf(data.substring(8));
        mese = Integer.valueOf(data.substring(5, 7));
        anno = Integer.valueOf(data.substring(0, 4));
        data = LocalDate.of(anno, mese, giorno).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);
        model.addAttribute("nomeStruttura", struttureService.getNomeStruttura(prenotazioneCercaDTOIn.getIdStruttura(), jwt.getTokenValue()));
        model.addAttribute("dataPrenotazioneRichiesta", data);
        model.addAttribute("prenotazioniDisponibili", prenotazioniService.getPrenotazioneLibere(prenotazioneCercaDTOIn.getIdStruttura(), data, jwt.getTokenValue()));
        return "prenotazioni-disponibili";
	}
    
    @GetMapping("/prenotazioni/cancella/{id}")
    public String cancellaPrenotazione(@PathVariable("id") @Positive int idPrenotazione, Model model, @AuthenticationPrincipal Jwt jwt){
        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
		
        model.addAttribute(ID_UTENTE, idUtente);
        model.addAttribute(NOME_UTENTE, nomeUtente);

        prenotazioniService.deletePrenotazione(idPrenotazione);
        model.addAttribute(PRENOTAZIONI_UTENTE, prenotazioniService.getPrenotazioneByIdUtente(idUtente, jwt.getTokenValue()));
        model.addAttribute("prenotazioneAnnullata", "1");
        return PRENOTAZIONI_UTENTE_PAGE;
    }
    



}
