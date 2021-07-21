package br.com.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Lancamento;
import br.com.minhasfinancas.model.Usuario;
import br.com.minhasfinancas.model.enums.StatusLancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;
import br.com.minhasfinancas.repository.LancamentoRepository;
import br.com.minhasfinancas.repository.LancamentoRepositoryTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoService lancamentoService;
	
	@MockBean
	LancamentoRepository lancamentoRepository;
	
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(lancamentoService).validarLancamento(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setIdLancamento(1L);
		lancamentoSalvo.setStatusLancamento(StatusLancamento.PENDENTE);
		when(lancamentoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		Lancamento lancamento = lancamentoService.salvarLancamento(lancamentoASalvar);
		assertThat(lancamento.getIdLancamento()).isEqualTo(lancamentoSalvo.getIdLancamento());
		assertThat(lancamento.getStatusLancamento()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow(RegraNegocioException.class).when(lancamentoService).validarLancamento(lancamentoASalvar);
		
		catchThrowableOfType(() -> lancamentoService.salvarLancamento(lancamentoASalvar), RegraNegocioException.class);
		verify(lancamentoRepository, never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setIdLancamento(1L);
		lancamentoSalvo.setStatusLancamento(StatusLancamento.PENDENTE);
		
		doNothing().when(lancamentoService).validarLancamento(lancamentoSalvo);
		
		when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		lancamentoService.atualizarLancamento(lancamentoSalvo);
		
		verify(lancamentoRepository, times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarLancamentoQeAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(() -> lancamentoService.atualizarLancamento(lancamentoASalvar), NullPointerException.class);
		verify(lancamentoRepository, never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setIdLancamento(1L);
		
		lancamentoService.deletarLancamento(lancamento);
		
		verify(lancamentoRepository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueNaoFoiSalvo() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		catchThrowableOfType(() -> lancamentoService.deletarLancamento(lancamento), NullPointerException.class);
		
		verify(lancamentoRepository, never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setIdLancamento(1L);
		
		List<Lancamento> lancamentos = Arrays.asList(lancamento);
				
		when(lancamentoRepository.findAll(any(Example.class))).thenReturn(lancamentos);
		
		List<Lancamento> resultado = lancamentoService.buscarLancamento(lancamento);
		
		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarStatusDeUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setIdLancamento(1L);
		lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		doNothing().when(lancamentoService).validarLancamento(lancamento);
		doReturn(lancamento).when(lancamentoService).atualizarLancamento(lancamento);
		
		lancamentoService.atualizarStatus(lancamento, novoStatus);
		
		assertThat(lancamento.getStatusLancamento()).isEqualTo(novoStatus);
		verify(lancamentoService).atualizarLancamento(lancamento);
	}
	
	@Test
	public void deveObterLancamentoPorId() {
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setIdLancamento(id);
		
		when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = lancamentoService.buscarPorId(id);
		
		assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoLancamentoNaoExiste() {
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setIdLancamento(id);
		
		when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = lancamentoService.buscarPorId(id);
		
		assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveRetornarErroAoValidarLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida!");
		
		lancamento.setDescricao("Gol quadrado");
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido!");
		
		lancamento.setMesLancamento(7);
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido!");
		
		lancamento.setAnoLancamento(2021);
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário!");
		
		lancamento.setUsuario(new Usuario());
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário!");
		
		lancamento.getUsuario().setIdUsuario(1L);
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido!");
		
		lancamento.setValorLancamento(BigDecimal.ZERO);
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido!");
		
		lancamento.setValorLancamento(BigDecimal.valueOf(2500));
		
		erro = catchThrowable(() -> lancamentoService.validarLancamento(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo válido!");
		
		lancamento.setTipoLancamento(TipoLancamento.RECEITA);
	}
	
	
}
