package com.gianfcop.ss.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gianfcop.ss.dto.AbbonamentoDTOIn;
import com.gianfcop.ss.dto.PrenotazioneCercaDTOIn;
import com.gianfcop.ss.dto.PrenotazioneDTOIn;
import com.gianfcop.ss.service.AbbonamentiService;
import com.gianfcop.ss.service.StruttureService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomGlobalExceptionHandler {

    @Autowired
    private StruttureService struttureService;

    @Autowired
    private AbbonamentiService abbonamentiService;

    @Autowired
    private HttpServletResponse response;

    private static final String BAD_REQUEST = "badRequest";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String INDEX_PAGE = "index";

    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException ex, HttpServletRequest request,
            Model model, @AuthenticationPrincipal Jwt jwt) throws ServletException {

        log.error(ex.getMessage());

        String idUtente = jwt.getClaims().get("sub").toString();
        String nomeUtente = jwt.getClaimAsString("name");
        model.addAttribute("nomeUtente", nomeUtente);
        model.addAttribute("idUtente", idUtente);

        String returnPage = INDEX_PAGE;
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/prenotazioni/new")) {

            model.addAttribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            PrenotazioneDTOIn prenotazioneDTOIn = new PrenotazioneDTOIn();
            model.addAttribute("prenotazioneDTOIn", prenotazioneDTOIn);
            model.addAttribute("infoStrutture", struttureService.getInfoStrutture(jwt.getTokenValue()));
            model.addAttribute(BAD_REQUEST, "1");

            returnPage = "crea_prenotazione";
        } else if (requestURI.equals("/prenotazioni/disponibili/cerca")) {
            model.addAttribute("oggi", LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            PrenotazioneCercaDTOIn prenotazioneCercaDTOIn = new PrenotazioneCercaDTOIn();
            model.addAttribute("prenotazioneCercaDTOIn", prenotazioneCercaDTOIn);
            model.addAttribute("infoStrutture", struttureService.getInfoStrutture(jwt.getTokenValue()));
            model.addAttribute(BAD_REQUEST, "1");

            returnPage = "prenotazioni-disponibili-cerca";
        } else if (requestURI.equals("/abbonamenti/new")) {
            AbbonamentoDTOIn abbonamentoDTOIn = new AbbonamentoDTOIn();
            String dataDiOggi = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String dataDiDomani = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            model.addAttribute("oggi", dataDiOggi);
            model.addAttribute("domani", dataDiDomani);
            model.addAttribute("infoAbbonamenti", abbonamentiService.getCreazioneAbbonamentoInfo());
            model.addAttribute("abbonamentoDTOIn", abbonamentoDTOIn);

            model.addAttribute(BAD_REQUEST, "1");
            returnPage = "crea_abbonamento";
        } else if (requestURI
                .matches("/abbonamenti/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}") ||
                requestURI.matches(
                        "/prenotazioni/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")) {

            logoutProcedure(request);
        }

        return returnPage;

    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(BindException ex, HttpServletRequest request) throws ServletException {

        logoutProcedure(request);

        return INDEX_PAGE;

    }

    private void logoutProcedure(HttpServletRequest request) throws ServletException {

        request.logout(); // Invalida l'autenticazione corrente
        SecurityContextHolder.getContext().setAuthentication(null); // Elimina l'autenticazione corrente
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalida la sessione
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0); // Elimina i cookie
                cookie.setValue(null);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

    }


}
