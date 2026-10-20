package com.projetopgd.bancoeapipgd.services.imports;

import com.projetopgd.bancoeapipgd.dtos.imports.RespostaApiAnttProDTO;
import com.projetopgd.bancoeapipgd.dtos.imports.UsuarioImportDTO;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import com.projetopgd.bancoeapipgd.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsuarioImportService {

    private static final HttpHeaders HEADER = criarHeaderRequisicao();
    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;
    @Value("${URL_API_ANTT_PRO}")
    private String urlApiAnttPro;
    @Value("${AUTH}")
    private String auth;

    private static HttpHeaders criarHeaderRequisicao() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return HttpHeaders.readOnlyHttpHeaders(headers); //imutável

    }

    @Transactional
    public void sync() {

        log.info("Iniciando sincronização de USUÁRIOS do JSON.");
        List<Usuario> todosOsUsuarios = new ArrayList<>();

        int offset = 0;
        int pagina = 1;
        while (true) {
            //1 = hash válido, != de 1, pode ser hash inválido (usuário não encontrado) ou
            //não tem permissão para os parâmetros informados (buscar todos, etc.)
            ResponseEntity<RespostaApiAnttProDTO<UsuarioImportDTO>> resposta = gerarRequisicaoPaginada(offset);

            if (resposta.hasBody() && resposta.getBody().getStatus() == 1) {
                log.info("Body e status retornado pela API (não código HTTP) válido.");

                List<UsuarioImportDTO> itens = resposta.getBody().getItens();

                if (!itens.isEmpty()) {
                    log.info("Os array de itens não está vazio. {} usuários  encontrados.", itens.size());
                    log.info("Página atual: {}", pagina);

                    todosOsUsuarios.addAll(itens.stream().map(Usuario::new).toList());
                    offset += itens.size();

                    log.info("Total de usuários encontrados até o momento: {}", offset);

                    pagina++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("InterruptedException lançada, saindo do loop.");
                        break;
                    }

                } else {
                    log.warn("O array de itens está vazio.");
                    break;
                }

            } else {
                log.warn("Body OU status retornado pela API (não código HTTP) inválido.");
            }
        }


        if (!todosOsUsuarios.isEmpty()) {
            usuarioRepository.saveAll(todosOsUsuarios);
            log.info("{} usuários salvos no banco de dados.", todosOsUsuarios.size());
        } else {
            log.warn("Nenhum usuário encontrado, nada foi salvo em banco.");
        }

    }

    private ResponseEntity<RespostaApiAnttProDTO<UsuarioImportDTO>> gerarRequisicaoPaginada(int offset) {

        HttpEntity<MultiValueMap<String, String>> requisicao = new HttpEntity<>(generateBody(offset), HEADER);

        return restTemplate.exchange(
                urlApiAnttPro,
                HttpMethod.POST,
                requisicao,
                new ParameterizedTypeReference<RespostaApiAnttProDTO<UsuarioImportDTO>>() {
                }
        );
    }

    private MultiValueMap<String, String> generateBody(int offset) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.set("action", "config_users"); //pode ser escondida em variável de ambiente tb.
        form.set("hash", auth);
        form.set("offset", String.valueOf(offset));
        form.set("disabled", "show");

        return form;

    }


}
