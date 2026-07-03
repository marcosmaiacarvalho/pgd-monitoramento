package com.projetopgd.bancoeapipgd.repositories;

import com.projetopgd.bancoeapipgd.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByMatricula(String matricula);
    
}
