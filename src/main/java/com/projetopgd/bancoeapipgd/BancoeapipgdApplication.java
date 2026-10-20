package com.projetopgd.bancoeapipgd;

import com.projetopgd.bancoeapipgd.services.imports.PlanoImportService;
import com.projetopgd.bancoeapipgd.services.imports.UsuarioImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@EnableScheduling
@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class BancoeapipgdApplication implements CommandLineRunner {

    private static final Path ARQUIVO_CONTROLE_SYNC =
            Path.of(System.getProperty("user.home"), "h2data", "pgd_db", "prod", "ultima-sync.txt");
    private final UsuarioImportService usuarioImportService;
    private final PlanoImportService planoImportService;
    private final Environment env;

    @Value("${sync.executar-na-inicializacao:true}")
    private boolean sincronizarNaInicializacao;

    public static void main(String[] args) {
        SpringApplication.run(BancoeapipgdApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (!env.acceptsProfiles(Profiles.of("prod"))) {
            log.info("Perfil não é prod. Banco populado via seed de testes. Carregamento da API não realizado");
            return;
        }

        if (sincronizarNaInicializacao) {
            sincronizarSeNecessario();
        } else {
            log.info("Sync de inicialização desabilitada. Aguardando o agendamento.");
        }


    }

    @Scheduled(cron = "0 0 6 * * *", zone = "America/Sao_Paulo")
    private void sincronizacaoAgendada() {

        log.info("Iniciando sincronização agendada das 06h00!");

        if (!env.acceptsProfiles(Profiles.of("prod"))) {
            log.warn("Perfil não é prod. Banco populado via seed de testes. Carregamento da API não realizado");
            return;
        }

        sincronizarSeNecessario();

    }

    private void sincronizarSeNecessario() {
        if (jaSincronizouHoje()) {
            log.info("Sincronização já feita hoje ({}). Pulando.", LocalDate.now());
            return;
        }

        usuarioImportService.sync();
        planoImportService.sync();
        registrarSincronizacao();     // ← só marca se chegou até aqui
    }


    private boolean jaSincronizouHoje() {
        try {
            if (!Files.exists(ARQUIVO_CONTROLE_SYNC)) return false;
            String conteudo = Files.readString(ARQUIVO_CONTROLE_SYNC).trim();
            return LocalDate.parse(conteudo).isEqual(LocalDate.now());
        } catch (Exception e) {
            log.warn("Não consegui ler o controle de sync ({}). Vou sincronizar.", e.getMessage());
            return false;             // na dúvida, sincroniza
        }
    }

    private void registrarSincronizacao() {
        try {
            Files.writeString(ARQUIVO_CONTROLE_SYNC, LocalDate.now().toString());
            log.info("Sync registrada em {}", ARQUIVO_CONTROLE_SYNC.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Não consegui gravar o controle de sync: {}", e.getMessage());
        }
    }
}
