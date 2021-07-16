package br.com.minhasfinancas.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {
	private Long idLancamento;
	private String descricao;
	private Integer mesLancamento;
	private Integer anoLancamento;
	private BigDecimal valorLancamento;
	private Long usuario;
	private String tipoLancamento;
	private String statusLancamento;
	
}
