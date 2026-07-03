package com.projetopgd.bancoeapipgd.services.responses;

import com.projetopgd.bancoeapipgd.dtos.responses.base.UsuarioDTO;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import com.projetopgd.bancoeapipgd.mappers.UsuarioMapper;
import com.projetopgd.bancoeapipgd.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorMatriculaParaTela(String matricula) {
        Usuario entidade = usuarioRepository.findByMatricula(matricula).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
        return usuarioMapper.entidadeParaDTO(entidade);
    }


    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorMatricula(String matricula) {
        Usuario entidade = usuarioRepository.findByMatricula(matricula).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
        return entidade;
    }
}
