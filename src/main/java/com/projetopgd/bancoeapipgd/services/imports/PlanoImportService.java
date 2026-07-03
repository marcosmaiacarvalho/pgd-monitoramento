package com.projetopgd.bancoeapipgd.services.imports;

import com.projetopgd.bancoeapipgd.dtos.imports.PlanoImportDTO;
import com.projetopgd.bancoeapipgd.entities.Plano;
import com.projetopgd.bancoeapipgd.repositories.PlanoRepository;
import com.projetopgd.bancoeapipgd.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlanoImportService {

    private final PlanoRepository planoRepository;
    private final RestTemplate restTemplate;

    @Value("${URL_PLANOS_ARQUIVADOS}")
    private String urlPlanosArquivados;

    @Value("${URL_PLANOS_CORRENTES}")
    private String urlPlanosCorrentes;

    public void sync() {

        log.info("Iniciando sincronização de PLANOS do JSON.");
        List<Plano> todosOsPlanos = new ArrayList<>();


        ResponseEntity<List<PlanoImportDTO>> respostaCorrentes = restTemplate.exchange(
                urlPlanosCorrentes,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PlanoImportDTO>>() {
                }
        );

        if (respostaCorrentes.hasBody()) {
            todosOsPlanos.addAll(respostaCorrentes.getBody().stream()
                    .filter(p -> p.getInicioVigencia() != null && !p.getInicioVigencia().isBefore(DataUtils.getDataLimiteDeInicio()))
                    .map(Plano::new).toList());
            log.info("Sincronização inicial concluída. {} planos correntes encontrados.", respostaCorrentes.getBody().size());
        } else {
            log.warn("O JSON dos planos correntes não retornou um corpo (body) na requisição (request).");
        }


        ResponseEntity<List<PlanoImportDTO>> respostaArquivados = restTemplate.exchange(
                urlPlanosArquivados,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PlanoImportDTO>>() {
                }
        );

        if (respostaArquivados.hasBody()) {
            todosOsPlanos.addAll(respostaArquivados.getBody().stream()
                    .filter(p -> p.getInicioVigencia() != null && !p.getInicioVigencia().isBefore(DataUtils.getDataLimiteDeInicio()))
                    .map(Plano::new).toList());
            log.info("Sincronização inicial concluída. {} planos arquivados encontrados.", respostaArquivados.getBody().size());
        } else {
            log.warn("O JSON dos planos arquivados não retornou um corpo (body) na requisição (request).");
        }

        if (!todosOsPlanos.isEmpty()) {
            planoRepository.saveAll(todosOsPlanos);
            log.info("{} planos (correntes e arquivados) salvos no banco de dados.", todosOsPlanos.size());

        } else {
            log.warn("Nenhum plano encontrado, nada foi salvo em banco.");

        }


    }


}
