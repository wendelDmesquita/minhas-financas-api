package br.com.minhasfinancas.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Usuario;
import br.com.minhasfinancas.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	public Usuario autenticar(String emailUsuario, String senhaUsuario) {
		Optional<Usuario> usuario = usuarioRepository.findByEmailUsuario(emailUsuario);
		if(!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuário não encontrado! Verifique o email!");
		}
		
		if(!usuario.get().getSenhaUsuario().equals(senhaUsuario)) {
			throw new ErroAutenticacaoException("Senha incorreta!!");
		}
		
		return usuario.get();
	}
	
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmailUsuario());
		return usuarioRepository.save(usuario);
	}
	
	public void validarEmail(String emailUsuario) {
		Boolean existe = usuarioRepository.existsByEmailUsuario(emailUsuario);
		
		if(existe) {
			throw new RegraNegocioException("Este email já está cadastrado!");
		}
	}
}
