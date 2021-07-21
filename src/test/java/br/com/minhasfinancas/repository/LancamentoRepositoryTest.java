package br.com.minhasfinancas.repository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.minhasfinancas.model.Lancamento;
import br.com.minhasfinancas.model.enums.StatusLancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository lancamentoRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		
		lancamento = lancamentoRepository.save(lancamento);
		
		assertThat(lancamento.getIdLancamento()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getIdLancamento());

		lancamentoRepository.delete(lancamento);
		
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getIdLancamento());
		assertThat(lancamentoInexistente).isNull();
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento.setAnoLancamento(2019);
		lancamento.setMesLancamento(5);
		lancamento.setStatusLancamento(StatusLancamento.CANCELADO);
		lancamentoRepository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getIdLancamento());
		
		assertThat(lancamentoAtualizado.getAnoLancamento()).isEqualTo(2019);
		assertThat(lancamentoAtualizado.getMesLancamento()).isEqualTo(5);
		assertThat(lancamentoAtualizado.getStatusLancamento()).isEqualTo(StatusLancamento.CANCELADO);
	}
	
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		Optional<Lancamento> lancamentoFiltrado = lancamentoRepository.findById(lancamento.getIdLancamento());
		
		assertThat(lancamentoFiltrado.isPresent()).isTrue();
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.descricao("Lada Laika LS3 500hp")
				.anoLancamento(2021)
				.mesLancamento(7)
				.statusLancamento(StatusLancamento.PENDENTE)
				.tipoLancamento(TipoLancamento.RECEITA)
				.dataCadastro(LocalDate.now()).build();
			
	}
	
	public Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

}
