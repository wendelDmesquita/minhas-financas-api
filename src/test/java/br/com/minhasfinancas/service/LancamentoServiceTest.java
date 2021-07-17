package br.com.minhasfinancas.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Lancamento;
import br.com.minhasfinancas.model.enums.StatusLancamento;
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
}
