package br.com.acompanheme.gestao.repository;

import br.com.acompanheme.gestao.model.domain.Paciente;
import br.com.acompanheme.gestao.model.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    Optional<Usuario> findByEmail(String email);
}
