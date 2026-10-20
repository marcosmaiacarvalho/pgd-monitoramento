package com.projetopgd.bancoeapipgd.repositories;

import com.projetopgd.bancoeapipgd.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByMatricula(String matricula);

    //Set garante unicidade, ok? Pra não usar DISTINCT
    @Query("SELECT u.id FROM Usuario u")
    Set<Long> buscarTodosOsIdsEmBanco();
}
