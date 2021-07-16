package br.com.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Lancamento;
import br.com.minhasfinancas.model.enums.StatusLancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;
import br.com.minhasfinancas.repository.LancamentoRepository;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	public Lancamento salvarLancamento(Lancamento lancamento) {
		validarLancamento(lancamento);
		lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}
	
	public Optional<Lancamento> buscarPorId(Long idLancamento){
		return lancamentoRepository.findById(idLancamento);
	}

	public Lancamento atualizarLancamento(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getIdLancamento());
		validarLancamento(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	public void deletarLancamento(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getIdLancamento());
		lancamentoRepository.delete(lancamento);
	}

	public List<Lancamento> buscarLancamento(Lancamento lancamentoFiltro) {
		Example<Lancamento> example = Example.of(lancamentoFiltro,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		return lancamentoRepository.findAll(example);
	}

	public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
		validarLancamento(lancamento);
		lancamento.setStatusLancamento(statusLancamento);
		atualizarLancamento(lancamento);
	}
	
	
	public BigDecimal obterSaldoPorUsuario(Long idUsuario) {
		BigDecimal receitas = lancamentoRepository.buscarSaldoPorLancamentoEUsuario(idUsuario, TipoLancamento.RECEITA);
		BigDecimal despesas = lancamentoRepository.buscarSaldoPorLancamentoEUsuario(idUsuario, TipoLancamento.DESPESA);
		
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		
		return receitas.subtract(despesas);
	}

	public void validarLancamento(Lancamento lancamento) {
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma descrição válida!");
		}
		
		if(lancamento.getMesLancamento() == null || lancamento.getMesLancamento() < 1 || lancamento.getMesLancamento() > 12) {
			throw new RegraNegocioException("Informe um mês válido!");
		}
		
		if(lancamento.getAnoLancamento() == null || lancamento.getAnoLancamento().toString().length() != 4) {
			throw new RegraNegocioException("Informe um ano válido!");

		}
		
		if(lancamento.getUsuario() == null) {
			throw new RegraNegocioException("Informe um usuário!");
		}
		
		if(lancamento.getValorLancamento() == null || lancamento.getValorLancamento().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor válido!");
		}
		
		if(lancamento.getTipoLancamento() == null) {
			throw new RegraNegocioException("Informe um tipo válido!");
		}
	}

}
