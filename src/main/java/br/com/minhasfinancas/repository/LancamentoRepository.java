package br.com.minhasfinancas.repository;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.minhasfinancas.model.Lancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;

@Repository
@Transactional
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	@Query(value = "select sum(l.valorLancamento) from Lancamento l join l.usuario u where u.idUsuario = :idUsuario "
			+ "and l.tipoLancamento = :tipoLancamento group by u")
	BigDecimal buscarSaldoPorLancamentoEUsuario(@Param(value = "idUsuario") Long idUsuario, @Param("tipoLancamento") TipoLancamento tipoLancamento);
}
