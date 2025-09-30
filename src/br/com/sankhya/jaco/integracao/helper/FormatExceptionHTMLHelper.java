package br.com.sankhya.jaco.integracao.helper;

import com.sankhya.util.StringUtils;

public class FormatExceptionHTMLHelper {
	private static final String DEFAULT_COLOR = "#000000"; // Preto

	// Construtor sem imagem
	public static String formataHtml(String titulo, String motivo) {
		return formataHtml(titulo, motivo, null, null, null, null);
	}

	// Construtor sem imagem
	public static String formataHtml(String titulo, String motivo, String solucao) {
		return formataHtml(titulo, motivo, solucao, null, null, null);
	}

	// Construtor com imagem opcional
	public static String formataHtml(String titulo, String motivo, String solucao, String imageUrl) {
		return formataHtml(titulo, motivo, solucao, imageUrl, null, null);
	}

	// Construtor com imagem e cores personalizadas
	public static String formataHtml(String titulo, String motivo, String solucao, String imageUrl, String tituloColor,
			String mensagemColor) {
		StringBuilder mensagem = new StringBuilder();

		if (StringUtils.isEmpty(tituloColor)) {
			tituloColor = DEFAULT_COLOR; // Usa cor padrão se não fornecida
		}

		if (StringUtils.isEmpty(mensagemColor)) {
			mensagemColor = DEFAULT_COLOR; // Usa cor padrão se não fornecida
		}

		mensagem.append("<p align=\"left\">");

		// Adiciona a imagem se o URL não estiver vazio
		if (StringUtils.isNotEmpty(imageUrl)) {
			mensagem.append("<img src=\"").append(imageUrl).append(
					"\" alt=\"Imagem\" style=\"display:block; max-width:100%; height:auto; margin-bottom:10px;\"/>");
		}

		mensagem.append("<b style=\"color:").append(tituloColor).append(";\">Atenção:</b> ").append(titulo);

		if (StringUtils.isNotEmpty(motivo)) {
			mensagem.append("<br/><b style=\"color:").append(mensagemColor).append(";\">Motivo:</b> ").append(motivo);
		}

		if (StringUtils.isNotEmpty(solucao)) {
			mensagem.append("<br/><b style=\"color:").append(mensagemColor).append(";\">Solução:</b> ").append(solucao);
		}

		mensagem.append(
				"<br/><p align=\"center\"><font size=\"10\" color=\"#008B45\"><b>Informações para o Implantador e/ou equipe Sankhya</b></font>");

		mensagem.append("<br/></p>");

		return mensagem.toString();
	}
}