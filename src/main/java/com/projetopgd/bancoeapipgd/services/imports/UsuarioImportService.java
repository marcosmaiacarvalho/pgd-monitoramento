package com.projetopgd.bancoeapipgd.services.imports;

import com.projetopgd.bancoeapipgd.dtos.imports.UsuarioImportDTO;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import com.projetopgd.bancoeapipgd.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsuarioImportService {

    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;

    @Value("${URL_USUARIOS}")
    private String urlUsuarios;

    public void sync() {

        log.info("Iniciando sincronização de USUÁRIOS do JSON.");

        ResponseEntity<List<UsuarioImportDTO>> resposta = restTemplate.exchange(
                urlUsuarios,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UsuarioImportDTO>>() {
                }
        );

        if (resposta.hasBody()) {
            List<Usuario> usuarios = resposta.getBody().stream().map(Usuario::new).toList();
            usuarioRepository.saveAll(usuarios);
            log.info("Sincronização concluída. {} usuários salvos/atualizados.", resposta.getBody().size());
        } else {
            log.warn("O JSON não retornou um corpo (body) na requisição (request).");
        }

    }


}
