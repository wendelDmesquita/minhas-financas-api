package br.com.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Usuario;
import br.com.minhasfinancas.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioService usuarioService;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@Test
	public void deveValidarEmail() {
		Mockito.when(usuarioRepository.existsByEmailUsuario(Mockito.anyString())).thenReturn(false);
		
		usuarioService.validarEmail("north@exe.bol");
	}
	
	@Test
	public void deveLancarErroAoValidarEmailJaCadastrado() {
		Mockito.when(usuarioRepository.existsByEmailUsuario(Mockito.anyString())).thenReturn(true);
		
		assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail("north@exe.bol"));
	}
	
	@Test
	public void deveAuteticarUmUsuarioComSucesso() {
		String email = "north@exe.bol";
		String senha = "gol quadrado";
		
		Usuario usuario = Usuario
				.builder()
				.idUsuario(1l)
				.emailUsuario(email)
				.senhaUsuario(senha)
				.build();
		Mockito.when(usuarioRepository.findByEmailUsuario(email)).thenReturn(Optional.of(usuario));
		
		Usuario result =  usuarioService.autenticar(email, senha);
		
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroAoNaoEncontrarUsuarioComEmailInformado() {
		Mockito.when(usuarioRepository.findByEmailUsuario(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("north@exe.bol", "gol quadrado"));
		
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Usuário não encontrado! Verifique o email!");
	}
	
	@Test
	public void deveLancarErroQuandoASenhaNaoForAMesma() {
		String senha = "gol bola";
		Usuario usuario = Usuario
							.builder()
							.idUsuario(1L)
							.emailUsuario("north@exe.bol")
							.senhaUsuario(senha)
							.build();
		Mockito.when(usuarioRepository.findByEmailUsuario(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("north@exe.bol", "gol quadrado"));
		
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Senha incorreta!!");
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario
				.builder()
				.idUsuario(1L)
				.nomeUsuario("north.exe")
				.emailUsuario("north@exe.bol")
				.senhaUsuario("gol quadrado")
				.build();
		Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());
		
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getIdUsuario()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNomeUsuario()).isEqualTo("north.exe");
		Assertions.assertThat(usuarioSalvo.getEmailUsuario()).isEqualTo("north@exe.bol");
		Assertions.assertThat(usuarioSalvo.getSenhaUsuario()).isEqualTo("gol quadrado");
		
	}
	
	@Test
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
		String email = "north@exe.bol";
		Usuario usuario = Usuario.builder().emailUsuario(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
		
		assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail(email));
		
		Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
	}
}
