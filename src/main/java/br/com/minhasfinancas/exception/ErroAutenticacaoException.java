package br.com.minhasfinancas.exception;

public class ErroAutenticacaoException extends RuntimeException{

	private static final long serialVersionUID = 69785;
	
	public ErroAutenticacaoException(String mensagem){
		super(mensagem);
	}

}
