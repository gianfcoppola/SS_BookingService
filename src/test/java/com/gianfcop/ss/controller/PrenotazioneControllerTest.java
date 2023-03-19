package com.gianfcop.ss.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.gianfcop.ss.dto.PrenotazioneCercaDTOIn;
import com.gianfcop.ss.dto.PrenotazioneDTOIn;
import com.gianfcop.ss.dto.PrenotazioneDTOOut;
import com.gianfcop.ss.dto.StrutturaDTOOut;
import com.gianfcop.ss.repository.PrenotazioniRepository;
import com.gianfcop.ss.service.PrenotazioniService;
import com.gianfcop.ss.service.StruttureService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
public class PrenotazioneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StruttureService struttureService;

    @MockBean
    private PrenotazioniRepository prenotazioniRepository;

    @MockBean
    private PrenotazioniService prenotazioniService;

    @Test
    public void returnCreaPrenotazione_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        List<StrutturaDTOOut> strutturaDTOOuts = Arrays.asList(
                new StrutturaDTOOut(1, "Campo da calcetto"),
                new StrutturaDTOOut(2, "Campo da tennis"),
                new StrutturaDTOOut(3, "Palestra"),
                new StrutturaDTOOut(4, "Piscina"));
        Mockito.when(struttureService.getInfoStrutture(jwt.getTokenValue())).thenReturn(strutturaDTOOuts);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/prenotazioni/new")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("crea_prenotazione"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("oggi"))
                .andExpect(model().attribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(model().attributeExists("infoStrutture"))
                .andExpect(model().attribute("infoStrutture", strutturaDTOOuts))
                .andReturn();
    }

    @Test
    public void returnCreaPrenotazione_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/prenotazioni/new");

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    @Test
    public void savePrenotazioneSuccess() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        PrenotazioneDTOIn prenotazioneDTOIn = PrenotazioneDTOIn.builder()
                .giorno("2023-03-18")
                .oraInizio("18:00")
                .oraFine("19:30")
                .idStruttura(1)
                .build();
        Mockito.when(prenotazioniService.insertPrenotazione(prenotazioneDTOIn, "1234", jwt.getTokenValue())).thenReturn(true);
        Mockito.when(prenotazioniService.checkPossibileEffetuarePrenotazione(any())).thenReturn(true);
        
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/new")
                .param("giorno", "2023-03-18")
                .param("oraInizio", "18:00")
                .param("oraFine", "19:30")
                .param("idStruttura", "1")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-utente"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("prenotazioneInserita"))
                .andExpect(model().attribute("prenotazioneInserita", "1"))
                .andExpect(model().attributeExists("prenotazioni"))
                .andReturn();
    }

    @Test
    public void savePrenotazioneFail() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        PrenotazioneDTOIn prenotazioneDTOIn = PrenotazioneDTOIn.builder()
                .giorno("2023-03-18")
                .oraInizio("18:00")
                .oraFine("19:30")
                .idStruttura(1)
                .build();
        Mockito.when(prenotazioniService.insertPrenotazione(prenotazioneDTOIn, "1234", jwt.getTokenValue())).thenReturn(false);
        
       
        Mockito.when(struttureService.getNomeStruttura(1, jwt.getTokenValue())).thenReturn("Campo da calcetto");
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/new")
                .param("giorno", "2023-03-18")
                .param("oraInizio", "18:00")
                .param("oraFine", "19:30")
                .param("idStruttura", "1")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-disponibili"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeDoesNotExist("prenotazioneInserita"))
                .andExpect(model().attributeExists("prenotazioneNonInserita"))
                .andExpect(model().attribute("prenotazioneNonInserita", "1"))
                .andExpect(model().attributeExists("nomeStruttura"))
                .andExpect(model().attribute("nomeStruttura", "Campo da calcetto"))
                .andExpect(model().attributeExists("dataPrenotazioneRichiesta"))
                .andExpect(model().attribute("dataPrenotazioneRichiesta", "18-03-2023"))
                .andExpect(model().attributeExists("prenotazioniDisponibili"))
                .andReturn();
    }


    @Test
    public void savePrenotazione_400() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        PrenotazioneDTOIn prenotazioneDTOIn = PrenotazioneDTOIn.builder()
                .giorno("18-03-2023")
                .oraInizio("18:00")
                .oraFine("19:30")
                .idStruttura(1)
                .build();
        Mockito.when(prenotazioniService.insertPrenotazione(prenotazioneDTOIn, "1234", jwt.getTokenValue())).thenReturn(false);
        
       
        Mockito.when(struttureService.getNomeStruttura(1, jwt.getTokenValue())).thenReturn("Campo da calcetto");
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/new")
                .param("giorno", "18-03-2023")
                .param("oraInizio", "18:00")
                .param("oraFine", "19:30")
                .param("idStruttura", "1")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("crea_prenotazione"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("badRequest"))
                .andExpect(model().attribute("badRequest", "1"))
                .andExpect(model().attributeExists("prenotazioneDTOIn"))
                .andExpect(model().attributeExists("infoStrutture"))
                .andExpect(model().attributeDoesNotExist("prenotazioneInserita"))
                .andExpect(model().attributeDoesNotExist("prenotazioneNonInserita"))
                .andExpect(model().attributeDoesNotExist("nomeStruttura"))
                .andExpect(model().attributeDoesNotExist("dataPrenotazioneRichiesta"))
                .andExpect(model().attributeDoesNotExist("prenotazioniDisponibili"))
                .andReturn();
    }


    @Test
    public void savePrenotazione_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/new")
                .param("giorno", "2023-03-18")
                .param("oraInizio", "18:00")
                .param("oraFine", "19:30")
                .param("idStruttura", "1");

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void returnListaMiePrenotazioni_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        String idUtente = "c828a488-ee0b-4194-89b8-8a490ca1eaf5";
        String nomeUtente = "Mario Rossi";
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", idUtente));

        PrenotazioniService prenotazioniService = Mockito.mock(PrenotazioniService.class);
        List<PrenotazioneDTOOut> prenotazioneDTOOuts = Arrays.asList(
                new PrenotazioneDTOOut("1",
                        LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), "20:00",
                        "Campo da calcetto", nomeUtente, 0, 0),
                new PrenotazioneDTOOut("1",
                        LocalDate.now().minusDays(4).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), "20:00",
                        "Campo da tennis", nomeUtente, 0, 0),
                new PrenotazioneDTOOut("1",
                        LocalDate.now().minusDays(11).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), "20:00",
                        "Campo da calcetto", nomeUtente, 0, 0));
        Mockito.when(prenotazioniService.getPrenotazioneByIdUtente(idUtente, jwt.getTokenValue())).thenReturn(prenotazioneDTOOuts);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/prenotazioni/" + idUtente)
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-utente"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", idUtente))
                .andExpect(model().attributeExists("prenotazioni"))
                .andReturn();
    }

    @Test
    public void returnListaMiePrenotazioni_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/prenotazioni/" + UUID.randomUUID());

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void returnCercaPrenotazioniDisponibili_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        List<StrutturaDTOOut> strutturaDTOOuts = Arrays.asList(
                new StrutturaDTOOut(1, "Campo da calcetto"),
                new StrutturaDTOOut(2, "Campo da tennis"),
                new StrutturaDTOOut(3, "Palestra"),
                new StrutturaDTOOut(4, "Piscina"));
        Mockito.when(struttureService.getInfoStrutture(jwt.getTokenValue())).thenReturn(strutturaDTOOuts);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/prenotazioni/disponibili/cerca")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-disponibili-cerca"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("oggi"))
                .andExpect(model().attribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(model().attributeExists("infoStrutture"))
                .andExpect(model().attribute("infoStrutture", strutturaDTOOuts))
                .andReturn();
    }

    @Test
    public void returnCercaPrenotazioniDisponibili_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
            .get("/prenotazioni/disponibili/cerca");

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void returnCancellaPrenotazione_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        List<StrutturaDTOOut> strutturaDTOOuts = Arrays.asList(
                new StrutturaDTOOut(1, "Campo da calcetto"),
                new StrutturaDTOOut(2, "Campo da tennis"),
                new StrutturaDTOOut(3, "Palestra"),
                new StrutturaDTOOut(4, "Piscina"));
        Mockito.when(struttureService.getInfoStrutture(jwt.getTokenValue())).thenReturn(strutturaDTOOuts);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/prenotazioni/cancella/1")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-utente"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("prenotazioni"))
                .andExpect(model().attribute("prenotazioneAnnullata", "1"))
                .andReturn();
    }


    @Test
    public void returnCancellaPrenotazione_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
        .get("/prenotazioni/cancella/1");

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    @Test
    public void cercaPrenotazione_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        PrenotazioneCercaDTOIn prenotazioneCercaDTOIn = PrenotazioneCercaDTOIn.builder()
                .data("2023-03-18")
                .idStruttura(1)
                .build();
        
        Mockito.when(struttureService.getNomeStruttura(1, jwt.getTokenValue())).thenReturn("Campo da calcetto");
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/disponibili/cerca")
                .param("data", prenotazioneCercaDTOIn.getData())
                .param("idStruttura", String.valueOf(prenotazioneCercaDTOIn.getIdStruttura()))
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-disponibili"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("dataPrenotazioneRichiesta"))
                .andExpect(model().attribute("dataPrenotazioneRichiesta", "18-03-2023"))
                .andExpect(model().attributeExists("nomeStruttura"))
                .andExpect(model().attribute("nomeStruttura", "Campo da calcetto"))
                .andExpect(model().attributeExists("prenotazioniDisponibili"))
                .andReturn();
        
    }

    @Test
    public void cercaPrenotazione_400() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        PrenotazioneCercaDTOIn prenotazioneCercaDTOIn = PrenotazioneCercaDTOIn.builder()
                .data("18-03-2023")
                .build();
        
        Mockito.when(struttureService.getNomeStruttura(1, jwt.getTokenValue())).thenReturn("Campo da calcetto");
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/disponibili/cerca")
                .param("data", prenotazioneCercaDTOIn.getData())
                .param("idStruttura", String.valueOf(prenotazioneCercaDTOIn.getIdStruttura()))
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("prenotazioni-disponibili-cerca"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("oggi"))
                .andExpect(model().attribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(model().attributeExists("prenotazioneCercaDTOIn"))
                .andExpect(model().attributeExists("infoStrutture"))
                .andExpect(model().attributeExists("badRequest"))
                .andExpect(model().attribute("badRequest", "1"))
                .andExpect(model().attributeDoesNotExist("dataPrenotazioneRichiesta"))
                .andExpect(model().attributeDoesNotExist("nomeStruttura"))
                .andExpect(model().attributeDoesNotExist("prenotazioniDisponibili"))
                .andReturn();
        
    }

    @Test
    public void cercaPrenotazione_401() throws Exception {

        PrenotazioneCercaDTOIn prenotazioneCercaDTOIn = PrenotazioneCercaDTOIn.builder()
                .data("2023-03-18")
                .idStruttura(1)
                .build();
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/prenotazioni/disponibili/cerca")
                .param("data", prenotazioneCercaDTOIn.getData())
                .param("idStruttura", String.valueOf(prenotazioneCercaDTOIn.getIdStruttura()));

        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden())
                .andReturn();
        
    }

}
