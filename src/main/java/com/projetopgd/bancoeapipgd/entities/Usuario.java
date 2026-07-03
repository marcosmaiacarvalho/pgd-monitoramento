package com.projetopgd.bancoeapipgd.entities;


import com.projetopgd.bancoeapipgd.dtos.imports.UsuarioImportDTO;
import com.projetopgd.bancoeapipgd.enums.TipoPerfil;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String matricula;

    @Column(unique = true)
    private String email;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoPerfil tipoPerfil;

    private String unidadeAtual;

    public Usuario(UsuarioImportDTO dto) {
        id = dto.getId();
        matricula = dto.getMatricula();
        email = dto.getEmail();
        nome = dto.getNome();
        tipoPerfil = dto.getTipoPerfil();
        unidadeAtual = dto.getUnidadeAtual();
    }

    public Usuario(Long id) {
        this.id = id;
    }

}
