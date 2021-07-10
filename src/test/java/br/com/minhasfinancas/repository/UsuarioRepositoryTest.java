package br.com.minhasfinancas.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasfinancas.model.Usuario;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	public static Usuario criarUsuario() {
		return Usuario.builder()
				.nomeUsuario("northEXE")
				.emailUsuario("north@exe.bol")
				.senhaUsuario("golo")
				.build();
	}
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		Usuario usuario = criarUsuario()
;		
		entityManager.persist(usuario);
		
		Boolean result = usuarioRepository.existsByEmailUsuario("north@exe.bol");
		
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverEmail() {
		
		Boolean result = usuarioRepository.existsByEmailUsuario("north@exe.bol");
		
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePeristirUmUsuarioNaBaseDeDados() {
		Usuario usuario = criarUsuario();
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		Assertions.assertThat(usuarioSalvo.getIdUsuario()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		Optional<Usuario> result = usuarioRepository.findByEmailUsuario("north@exe.bol");
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarEmailQueNaoExisteNaBase() {
		Optional<Usuario> result = usuarioRepository.findByEmailUsuario("north@exe.bol");
		
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
}
