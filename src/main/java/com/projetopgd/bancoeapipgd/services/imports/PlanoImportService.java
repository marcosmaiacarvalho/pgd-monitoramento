package com.projetopgd.bancoeapipgd.services.imports;

import com.projetopgd.bancoeapipgd.dtos.imports.PlanoImportDTO;
import com.projetopgd.bancoeapipgd.dtos.imports.RespostaApiAnttProDTO;
import com.projetopgd.bancoeapipgd.dtos.imports.UsuarioImportDTO;
import com.projetopgd.bancoeapipgd.entities.Plano;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import com.projetopgd.bancoeapipgd.repositories.PlanoRepository;
import com.projetopgd.bancoeapipgd.repositories.UsuarioRepository;
import com.projetopgd.bancoeapipgd.utils.DataUtils;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlanoImportService {

    private static final int LIMITE_PAGINAS_REAL = 500;

    private static final HttpHeaders HEADER = criarHeaderRequisicao();
    private final PlanoRepository planoRepository;
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

        log.info("Iniciando sincronização de PLANOS do JSON.");

        LocalDateTime momentoDaSincronizacao = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Set<Long> idsSalvosEmBanco = usuarioRepository.buscarTodosOsIdsEmBanco();
        boolean finalizouComSucesso = false;

        int offset = 0;
        int pagina = 1;
        int qtdPlanosSalvos = 0;
        while (true) {

            if (pagina > LIMITE_PAGINAS_REAL) {
                log.error("Limite de páginas TOTAL ({} paginas) atingido. VERIFICAR!", LIMITE_PAGINAS_REAL);
                break;
            }

            ResponseEntity<RespostaApiAnttProDTO<PlanoImportDTO>> resposta = gerarRequisicaoPaginada(offset);

            //1 = hash válido, != de 1, pode ser hash inválido (usuário não encontrado) ou
            //não tem permissão para os parâmetros informados (buscar todos, etc.)
            if (!resposta.hasBody() || resposta.getBody().getStatus() != 1) {
                log.error("Body OU status retornado pela API (não código HTTP) inválido.");
                break;
            }

            List<PlanoImportDTO> itens = resposta.getBody().getItens();


            if (!itens.isEmpty()) {
                log.info("Os array de itens não está vazio. {} planos  encontrados.", itens.size());
                log.info("Página atual: {}", pagina);

                List<PlanoImportDTO> planosFiltradosDto = itens.stream()
                        .filter(p -> p.getInicioVigencia() != null && !p.getInicioVigencia().isBefore(DataUtils.getDataLimiteDeInicio()))
                        .toList();

                Map<Long, Usuario> novosUsuarios = new HashMap<>();
                for (PlanoImportDTO p : itens) {
                    UsuarioImportDTO usuario = p.getUsuario();
                    if (!idsSalvosEmBanco.contains(usuario.getId())) {
                        novosUsuarios.putIfAbsent(usuario.getId(), new Usuario(usuario));
                        log.info("Usuário {} encontrado a partir do plano {}.", usuario.getNome(), p.getId());
                    }
                }

                if (!novosUsuarios.isEmpty()) {
                    usuarioRepository.saveAll(novosUsuarios.values());
                    idsSalvosEmBanco.addAll(novosUsuarios.keySet());
                    log.info("{} usuários salvos a partir dos planos.", novosUsuarios.size());
                }

                planoRepository.saveAll(planosFiltradosDto.stream().map(dto -> new Plano(dto, momentoDaSincronizacao)).toList());
                log.info("{} planos (da página) salvos com sucesso.", planosFiltradosDto.size());

                offset += itens.size();
                qtdPlanosSalvos += planosFiltradosDto.size();
                log.info("Total de planos salvos até o momento: {}", qtdPlanosSalvos);

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
                finalizouComSucesso = true;
                break;
            }
        }

        log.info("FINALIZADO! {} planos salvos no banco de dados.", qtdPlanosSalvos);


        if (finalizouComSucesso) {
            int qtdDePlanosExcluidos = planoRepository.deletarPlanosExcluidos(momentoDaSincronizacao);
            log.info("{} plano excluídos pois foram excluídos no ANTT Pró.", qtdDePlanosExcluidos);
        }


    }

    private ResponseEntity<RespostaApiAnttProDTO<PlanoImportDTO>> gerarRequisicaoPaginada(int offset) {

        HttpEntity<MultiValueMap<String, String>> requisicao = new HttpEntity<>(generateBody(offset), HEADER);

        return restTemplate.exchange(
                urlApiAnttPro,
                HttpMethod.POST,
                requisicao,
                new ParameterizedTypeReference<RespostaApiAnttProDTO<PlanoImportDTO>>() {
                }
        );
    }

    private MultiValueMap<String, String> generateBody(int offset) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.set("action", "report_planos"); //pode ser escondida em variável de ambiente tb.
        form.set("hash", auth);
        form.set("offset", String.valueOf(offset));
        form.set("all_data", "true");
        form.set("data_inicio_query", "2024-04-01");

        return form;

    }


}
