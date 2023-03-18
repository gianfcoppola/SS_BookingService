package com.gianfcop.ss.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gianfcop.ss.dto.StrutturaDTOOut;

@Service
public class StruttureService {

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getNomiStrutture(){
         String uri = "http://structures-service/strutture/nomi";

         @SuppressWarnings("unchecked")
         List<String> listaNomiStrutture = restTemplate.getForObject(uri, List.class);

         return listaNomiStrutture;

    }

    public String getNomeStruttura(int idStruttura){

        String uri = "http://structures-service/strutture/nome/" + idStruttura;
        return restTemplate.getForObject(uri, String.class);

    }

    public List<StrutturaDTOOut> getInfoStrutture(){

        String uri = "http://structures-service/strutture/info";

        @SuppressWarnings("unchecked")
         List<StrutturaDTOOut> listaInfoStrutture = restTemplate.getForObject(uri, List.class);

         return listaInfoStrutture;

    }
    
}
