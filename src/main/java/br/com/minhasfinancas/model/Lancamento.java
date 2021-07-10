package br.com.minhasfinancas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import br.com.minhasfinancas.model.enums.StatusLancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "lancamento")
@Builder
@Data
public class Lancamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idLancamento;
	
	@Column(name = "mes_lancamento")
	private Integer mesLancamento;
	
	@Column(name = "ano_lancamento")
	private Integer anoLancamento;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@Column(name = "valor_lancamento")
	private BigDecimal valorLancamento;
	
	@Column(name = "data_cadastro")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate dataCadastro;
	
	@Column(name = "descricao")
	private String descricao;
	
	@Column(name = "tipo_lancamento")
	@Enumerated(value = EnumType.STRING)
	private TipoLancamento tipoLancamento;
	
	@Column(name = "status_lancamento")
	@Enumerated(value = EnumType.STRING)
	private StatusLancamento statusLancamento;
}
