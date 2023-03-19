package com.gianfcop.ss.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gianfcop.ss.dto.StrutturaDTOOut;

@Service
public class StruttureService {

    @Autowired
    private RestTemplate restTemplate;

    public String getNomeStruttura(int idStruttura, String accessToken){

        String uri = "http://structures-service/strutture/nome/" + idStruttura;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public List<StrutturaDTOOut> getInfoStrutture(String accessToken){

        String uri = "http://structures-service/strutture/info";
    
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        
        ResponseEntity<List<StrutturaDTOOut>> response = restTemplate.exchange(
            uri, HttpMethod.GET, entity, new ParameterizedTypeReference<List<StrutturaDTOOut>>() {});
        
        List<StrutturaDTOOut> listaInfoStrutture = response.getBody();

        return listaInfoStrutture;

    }
    
}
