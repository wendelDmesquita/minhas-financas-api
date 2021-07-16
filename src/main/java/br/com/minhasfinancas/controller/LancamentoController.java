package br.com.minhasfinancas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.minhasfinancas.dto.AtualizarStatusDTO;
import br.com.minhasfinancas.dto.LancamentoDTO;
import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Lancamento;
import br.com.minhasfinancas.model.Usuario;
import br.com.minhasfinancas.model.enums.StatusLancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;
import br.com.minhasfinancas.service.LancamentoService;
import br.com.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping(path = "/api/lancamentos")
public class LancamentoController {

	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping(path = "/salvar")
	public ResponseEntity<Object> salvarLancamento(@RequestBody LancamentoDTO dto){
		try {
			Lancamento entidade = converter(dto);
			entidade = lancamentoService.salvarLancamento(entidade);
			return new ResponseEntity<Object>(entidade, HttpStatus.CREATED);
		} catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	
	@PutMapping(path = "/{idLancamento}/atualizar")
	public ResponseEntity<? extends Object> atualizarLancamento(@PathVariable Long idLancamento, @RequestBody LancamentoDTO dto){
		return lancamentoService.buscarPorId(idLancamento).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setIdLancamento(entity.getIdLancamento());
				lancamentoService.atualizarLancamento(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch(RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e);
			}
		}).orElseGet(() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));

	}
	
	@PutMapping(path = "/{idLancamento}/atualizar-status")
	public ResponseEntity<? extends Object> atualizarStatus(@PathVariable Long idLancamento, @RequestBody AtualizarStatusDTO dto){
		return lancamentoService.buscarPorId(idLancamento).map(entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatusLancamento());
			if(statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível realizar a requisição. Envie um status válido!");
			}
			try {
				entity.setStatusLancamento(statusSelecionado);
				lancamentoService.atualizarLancamento(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e){
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados!",HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping(path = "/{idLancamento}/deletar")
	public ResponseEntity<Object> deletar(@PathVariable Long idLancamento){
		return lancamentoService.buscarPorId(idLancamento).map(entity -> {
			lancamentoService.deletarLancamento(entity);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity<Object>("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping
	public ResponseEntity<Object> buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mesLancamento", required = false) Integer mesLancamento,
			@RequestParam(value = "anoLancamento", required = false) Integer anoLancamento,
			@RequestParam Long idUsuario){
			
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMesLancamento(mesLancamento);
		lancamentoFiltro.setAnoLancamento(anoLancamento);
		
		
		Optional<Usuario> usuario = usuarioService.buscarPorId(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("não foi possível realizar a consulta. Usuário inválido");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = lancamentoService.buscarLancamento(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setIdLancamento(dto.getIdLancamento());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAnoLancamento(dto.getAnoLancamento());
		lancamento.setMesLancamento(dto.getMesLancamento());
		lancamento.setValorLancamento(dto.getValorLancamento());
		
		Usuario usuario = usuarioService
				.buscarPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado!"));
		
		lancamento.setUsuario(usuario);
		
		if(dto.getTipoLancamento() != null) {
			lancamento.setTipoLancamento(TipoLancamento.valueOf(dto.getTipoLancamento()));
		}
		
		if(dto.getStatusLancamento() != null) {
			lancamento.setStatusLancamento(StatusLancamento.valueOf(dto.getStatusLancamento()));
		}
		
		return lancamento;
	}
}
