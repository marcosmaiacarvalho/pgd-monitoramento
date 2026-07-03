package com.projetopgd.bancoeapipgd;

import com.projetopgd.bancoeapipgd.services.imports.PlanoImportService;
import com.projetopgd.bancoeapipgd.services.imports.UsuarioImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@RequiredArgsConstructor
@SpringBootApplication
public class BancoeapipgdApplication implements CommandLineRunner {

    private final UsuarioImportService usuarioImportService;
    private final PlanoImportService planoImportService;
    private final Environment env;


    public static void main(String[] args) {
        SpringApplication.run(BancoeapipgdApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Caso não seja o perfil de prod, o banco in memory é populado via seed do data-test.sql.
        if (!env.acceptsProfiles(Profiles.of("prod"))) {
            System.out.println("Busca via URL ignorada pois o perfil não é o de prod. Banco populado via seed de testes.");
            return;
        }

        usuarioImportService.sync();
        planoImportService.sync();
    }

}
