package br.com.sankhya.jaco.actions.reajustes.utils;

import com.sankhya.util.StringUtils;

public class ErrorUtil {
	
	
	
	 public static String formataHtml(String titulo, String motivo) {
		return formataHtml(titulo, motivo, null);
	}

	 public static String formataHtml(String titulo, String motivo, String solucao) {
		String mensagem = "<p align=\"left\"><b>Atenção:</b> " + titulo;

		if (StringUtils.isNotEmpty(motivo)) {
			mensagem += "<br/><b>Motivo:</b> " + motivo;
		}

		if (StringUtils.isNotEmpty(solucao)) {
			mensagem += "<br/><b>Solução:</b> " + solucao;
		}

		mensagem += "<br/></p>";

		return mensagem;
	}

}
