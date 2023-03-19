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
import com.gianfcop.ss.dto.AbbonamentoDTOIn;
import com.gianfcop.ss.dto.AbbonamentoDTOOut;
import com.gianfcop.ss.model.Abbonamento;
import com.gianfcop.ss.repository.AbbonamentiRepository;
import com.gianfcop.ss.service.AbbonamentiService;
import com.gianfcop.ss.service.StruttureService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@AutoConfigureMockMvc
public class AbbonamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StruttureService struttureService;

    @MockBean
    private AbbonamentiRepository abbonamentiRepository;

    @MockBean
    private AbbonamentiService abbonamentiService;

    @Test
    public void returnInfoAbbonamenti_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/abbonamenti/info")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("info_abbonamenti"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("infoAbbonamenti"))
                .andReturn();
    }

    @Test
    public void returnInfoAbbonamenti_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
            .get("/abbonamenti/info");

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void returnCreaAbbonamento_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/abbonamenti/new")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("crea_abbonamento"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("oggi"))
                .andExpect(model().attribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                .andExpect(model().attributeExists("domani"))
                .andExpect(model().attribute("domani", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(model().attributeExists("infoAbbonamenti"))
                .andExpect(model().attributeExists("abbonamentoDTOIn"))
                .andReturn();
    }

    @Test
    public void returnCreaAbbonamento_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
            .get("/abbonamenti/new");

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }


    @Test
    public void listaMieiAbbonamenti_200() throws Exception {

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get("/abbonamenti/c828a488-ee0b-4194-89b8-8a490ca1eaf5")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("abbonamenti_utente"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("abbonamentiUtente"))
                .andReturn();
    }

    @Test
    public void listaMieiAbbonamenti_401() throws Exception {

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
        .get("/abbonamenti/c828a488-ee0b-4194-89b8-8a490ca1eaf5");

        mockMvc.perform(mockRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
    

    
    @Test
    public void testInsertAbbonamento_200() throws Exception {
        
        // create a mock JWT
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        // create a mock AbbonamentoDTOIn
        AbbonamentoDTOIn abbonamentoDTOIn = new AbbonamentoDTOIn();
        abbonamentoDTOIn.setDataFineAbbonamento("31-05-2023");
        abbonamentoDTOIn.setDataInizioAbbonamento("01-04-2023");
        abbonamentoDTOIn.setIdStruttura(1);

        Abbonamento abbonamento = new Abbonamento();
        Mockito.when(abbonamentiService.insertAbbonamento(eq(abbonamentoDTOIn), anyString(), anyString())).thenReturn(abbonamento);
        List<AbbonamentoDTOOut> abbonamentiUtente = new ArrayList<>();
        Mockito.when(abbonamentiService.getAbbonamentiByIdUtente(anyString(), anyString())).thenReturn(abbonamentiUtente);

        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/abbonamenti/new")
                .param("dataInizioAbbonamento", "2023-03-18")
                .param("dataFineAbbonamento", "2023-06-30")
                .param("idStruttura", "3")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        // perform the POST request
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("abbonamenti_utente"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("abbonamentoInserito"))
                .andExpect(model().attribute("abbonamentoInserito", "1"))
                .andExpect(model().attributeExists("abbonamentiUtente"))
                .andExpect(model().attribute("abbonamentiUtente", abbonamentiUtente));
    }

    @Test
    public void testInsertAbbonamento_400() throws Exception {
        
        // create a mock JWT
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaims()).thenReturn(Map.of("sub", "1234", "name", "Mario Rossi"));

        // create a mock AbbonamentoDTOIn
        AbbonamentoDTOIn abbonamentoDTOIn = new AbbonamentoDTOIn();
        abbonamentoDTOIn.setDataFineAbbonamento("31-05-2023");
        abbonamentoDTOIn.setDataInizioAbbonamento("01-04-2023");
        abbonamentoDTOIn.setIdStruttura(1);

         
        Abbonamento abbonamento = new Abbonamento();
        Mockito.when(abbonamentiService.insertAbbonamento(eq(abbonamentoDTOIn), anyString(), anyString())).thenReturn(abbonamento);
        List<AbbonamentoDTOOut> abbonamentiUtente = new ArrayList<>();
        Mockito.when(abbonamentiService.getAbbonamentiByIdUtente(anyString(), anyString())).thenReturn(abbonamentiUtente);

        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/abbonamenti/new")
                .param("dataInizioAbbonamento", "01-04-2023")
                .param("dataFineAbbonamento", "31-05-2023")
                .param("idStruttura", "3")
                .with(authentication(new JwtAuthenticationToken(jwt, Collections.emptyList())))
                .with(csrf());

        // perform the POST request
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("crea_abbonamento"))
                .andExpect(model().attributeExists("idUtente"))
                .andExpect(model().attribute("idUtente", "1234"))
                .andExpect(model().attributeExists("badRequest"))
                .andExpect(model().attribute("badRequest", "1"))
                .andExpect(model().attributeExists("oggi"))
                .andExpect(model().attribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                .andExpect(model().attributeExists("domani"))
                .andExpect(model().attribute("domani", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(model().attributeExists("abbonamentoDTOIn"))
                .andExpect(model().attributeExists("infoAbbonamenti"))
                .andExpect(model().attributeDoesNotExist("abbonamentoInserito"))
                .andExpect(model().attributeDoesNotExist("abbonamentiUtente"));
    }

    @Test
    public void testInsertAbbonamento_401() throws Exception {
        
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/abbonamenti/new")
                .param("dataInizioAbbonamento", "2023-03-18")
                .param("dataFineAbbonamento", "2023-06-30")
                .param("idStruttura", "3");

        // perform the POST request
        mockMvc.perform(mockRequest)
                .andExpect(status().isForbidden());
    }
    

}
