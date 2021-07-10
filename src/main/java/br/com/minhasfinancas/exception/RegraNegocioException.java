package br.com.minhasfinancas.exception;

public class RegraNegocioException extends RuntimeException{
	
	private static final long serialVersionUID = 69;

	public RegraNegocioException(String mensagem) {
		super(mensagem);
	}
}
