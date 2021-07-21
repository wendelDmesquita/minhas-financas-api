package br.com.minhasfinancas.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.minhasfinancas.dto.UsuarioDTO;
import br.com.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Usuario;
import br.com.minhasfinancas.service.LancamentoService;
import br.com.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UsuarioController.class)
public class UsuarioControllerTest {
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService usuarioService;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception{
		String emailUsuario = "north@exe.bol";
		String senhaUsuario = "gol bola";
		UsuarioDTO dto = UsuarioDTO.builder()
				.emailUsuario(emailUsuario)
				.senhaUsuario(senhaUsuario).build();
		
		Usuario usuario = Usuario.builder()
				.idUsuario(1L)
				.emailUsuario(emailUsuario)
				.senhaUsuario(senhaUsuario).build();
		
		when(usuarioService.autenticar(emailUsuario, senhaUsuario)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("idUsuario").value(usuario.getIdUsuario()))
			.andExpect(MockMvcResultMatchers.jsonPath("emailUsuario").value(usuario.getEmailUsuario()))
			.andExpect(MockMvcResultMatchers.jsonPath("nomeUsuario").value(usuario.getNomeUsuario()));
	}
	
	@Test
	public void deveRetornarBadRequestAoErrarAutenticacao() throws Exception{
		String emailUsuario = "north@exe.bol";
		String senhaUsuario = "gol bola";
		UsuarioDTO dto = UsuarioDTO.builder()
				.emailUsuario(emailUsuario)
				.senhaUsuario(senhaUsuario).build();
		
		
		when(usuarioService.autenticar(emailUsuario, senhaUsuario)).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/autenticar"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveSalvarUmUsuario() throws Exception{
		String emailUsuario = "north@exe.bol";
		String senhaUsuario = "gol bola";
		UsuarioDTO dto = UsuarioDTO.builder()
				.emailUsuario(emailUsuario)
				.senhaUsuario(senhaUsuario).build();
		
		Usuario usuario = Usuario.builder()
				.idUsuario(1L)
				.emailUsuario(emailUsuario)
				.senhaUsuario(senhaUsuario).build();
		
		when(usuarioService.salvarUsuario(any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/salvarUsuario"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("idUsuario").value(usuario.getIdUsuario()))
			.andExpect(MockMvcResultMatchers.jsonPath("emailUsuario").value(usuario.getEmailUsuario()))
			.andExpect(MockMvcResultMatchers.jsonPath("nomeUsuario").value(usuario.getNomeUsuario()));
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarSalvarUsuarioInvalido() throws Exception{
		String emailUsuario = "north@exe.bol";
		String senhaUsuario = "gol bola";
		UsuarioDTO dto = UsuarioDTO.builder()
				.emailUsuario(emailUsuario)
				.senhaUsuario(senhaUsuario).build();
		
		
		when(usuarioService.salvarUsuario(any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post(API.concat("/salvarUsuario"))
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
}
