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

    @GetMapping("/info")
    public String returnInfoAbbonamenti(Model model, @AuthenticationPrincipal Jwt jwt){
        

        model.addAttribute("infoAbbonamenti", abbonamentiService.getInfoAbbonamenti());
        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute("idUtente", idUtente);
        model.addAttribute("nomeUtente", nomeUtente);
        

        return "info_abbonamenti";

    }

    
    @GetMapping("/new")
    public String returnCreaAbbonamento(Model model, @AuthenticationPrincipal Jwt jwt){

        String idUtente = jwt.getClaims().get("sub").toString();
        System.out.println(idUtente);
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute("idUtente", idUtente);
        model.addAttribute("nomeUtente", nomeUtente);
        
        // create Abbonamento object to hold prenotazione form data
		AbbonamentoDTOIn abbonamentoDTOIn = new AbbonamentoDTOIn();

        String dataDiOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString();
        String dataDiDomani = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
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
        
        model.addAttribute("idUtente", idUtente);
        model.addAttribute("nomeUtente", nomeUtente);
        
        abbonamentoDTOIn.setDataInizioAbbonamento(LocalDate.now().toString());
		abbonamentiService.insertAbbonamento(abbonamentoDTOIn, idUtente, jwt.getTokenValue());
        log.info("abbonamento inserito");
        model.addAttribute("abbonamentoInserito", "1");
        
        //redirectAttributes.addFlashAttribute("abbonamentoRegistrato", "1");
        //return "redirect:/abbonamenti/" + String.valueOf(abbonamentoDTOIn.getIdUtente());
        //return "redirect:/abbonamenti/new";
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
        model.addAttribute("idUtente", idUtente);
        model.addAttribute("nomeUtente", nomeUtente);
        model.addAttribute("abbonamentiUtente", abbonamentiService.getAbbonamentiByIdUtente(idUser));
        return "abbonamenti_utente";
    }



    /* 
    
    

    @PostMapping(value = "/insert")
    public ResponseEntity<String> creaAbbonamento(@RequestBody AbbonamentoDTOIn abbonamentoDTOIn){
        Abbonamento abbonamento = abbonamentiService.insertAbbonamento(abbonamentoDTOIn);

        return new ResponseEntity<String>("Abbonamento " + abbonamento.getId() + " creato con successo", HttpStatus.CREATED);
    }
    
    @GetMapping(value = "/all")
    public ResponseEntity<List<Abbonamento>> recuperaInfoAbbonamenti(){
        List<Abbonamento> abbonamenti = abbonamentiService.getAllAbbonamenti();

        return new ResponseEntity<List<Abbonamento>>(abbonamenti, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Abbonamento> recuperaInfoAbbonamentoById(@PathVariable("id") int idAbbonamento){

        Abbonamento abbonamento = abbonamentiService.getAbbonamentoById(idAbbonamento);

        return new ResponseEntity<Abbonamento>(abbonamento, HttpStatus.OK);
    }

    @GetMapping(value = "/utenti/{id}")
    public ResponseEntity<List<Abbonamento>> recuperoInfoAbbonamentiUtente(@PathVariable("id") int idUtente){
        List<Abbonamento> abbonamenti = abbonamentiService.getAbbonamentiByIdUtente(idUtente);
        return new ResponseEntity<List<Abbonamento>>(abbonamenti, HttpStatus.OK);
    
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> cancellaAbbonamento(@PathVariable("id") int idAbbonamento){
        abbonamentiService.deleteAbbonamento(idAbbonamento);

        return new ResponseEntity<String>("Abbonamento " + idAbbonamento + " cancellato con successo", HttpStatus.OK);
    }
*/
    
}
