package br.com.minhasfinancas.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.minhasfinancas.model.Lancamento;

@Repository
@Transactional
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
