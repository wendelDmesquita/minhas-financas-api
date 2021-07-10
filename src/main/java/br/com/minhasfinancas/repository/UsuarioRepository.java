package br.com.minhasfinancas.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.minhasfinancas.model.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	Boolean existsByEmailUsuario(String emailUsuario);
	Optional<Usuario> findByEmailUsuario(String emailUsuario);
}
