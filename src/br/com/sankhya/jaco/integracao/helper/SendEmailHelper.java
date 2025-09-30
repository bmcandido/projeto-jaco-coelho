package br.com.sankhya.jaco.integracao.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class SendEmailHelper {

	private final EntityFacade dwfFacade;
	private final BigDecimal defaultSmtp;
	private final BigDecimal defaultMaxAttempts;
	private final BigDecimal defaultConnection;

	// Valores padrão como constantes
	private static final BigDecimal DEFAULT_SMTP_VALUE = new BigDecimal(0);
	private static final BigDecimal DEFAULT_MAX_ATTEMPTS_VALUE = new BigDecimal(3);
	private static final BigDecimal DEFAULT_CONNECTION_VALUE = new BigDecimal(0);

	/**
	 * Construtor com valores padrão
	 */
	public SendEmailHelper() {
		this(DEFAULT_SMTP_VALUE, DEFAULT_MAX_ATTEMPTS_VALUE, DEFAULT_CONNECTION_VALUE);
	}

	/**
	 * Construtor com parâmetros configuráveis
	 * 
	 * @param defaultSmtp        Código SMTP padrão
	 * @param defaultMaxAttempts Número máximo de tentativas padrão
	 * @param defaultConnection  Código de conexão padrão
	 */
	public SendEmailHelper(BigDecimal defaultSmtp, BigDecimal defaultMaxAttempts, BigDecimal defaultConnection) {
		this.dwfFacade = EntityFacadeFactory.getDWFFacade();
		this.defaultSmtp = defaultSmtp != null ? defaultSmtp : DEFAULT_SMTP_VALUE;
		this.defaultMaxAttempts = defaultMaxAttempts != null ? defaultMaxAttempts : DEFAULT_MAX_ATTEMPTS_VALUE;
		this.defaultConnection = defaultConnection != null ? defaultConnection : DEFAULT_CONNECTION_VALUE;
	}

	/**
	 * Gera um relatório dinamicamente com parâmetros variáveis
	 */
	public byte[] gerarRelatorio(BigDecimal codRelatorio, BigDecimal codUsuarioLogado,
			List<AgendamentoRelatorioHelper.ParametroRelatorio> parametros) throws MGEModelException {
		try {
			if (parametros == null) {
				parametros = new ArrayList<>();
			}
			return AgendamentoRelatorioHelper.getPrintableReport(codRelatorio, parametros, codUsuarioLogado, dwfFacade);
		} catch (Exception e) {
			throw new MGEModelException("Erro ao gerar relatório: " + e.getMessage(), e);
		}
	}

	/**
	 * Envia e-mail com opção de anexo
	 */
	public BigDecimal enviarEmail(String assunto, String email, String mensagem, byte[] anexo, String tipoAnexo,
			String nomeArquivo) throws MGEModelException {
		return enviarEmail(assunto, email, mensagem, anexo, tipoAnexo, nomeArquivo, null, null, null);
	}

	/**
	 * Envia e-mail com opção de anexo e parâmetros SMTP configuráveis
	 */
	public BigDecimal enviarEmail(String assunto, String email, String mensagem, byte[] anexo, String tipoAnexo,
			String nomeArquivo, BigDecimal smtp, BigDecimal maxAttempts, BigDecimal connection)
			throws MGEModelException {

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();

			// Usa os valores fornecidos ou os defaults da instância
			BigDecimal smtpToUse = smtp != null ? smtp : this.defaultSmtp;
			BigDecimal maxAttemptsToUse = maxAttempts != null ? maxAttempts : this.defaultMaxAttempts;
			BigDecimal connectionToUse = connection != null ? connection : this.defaultConnection;

			// Cria mensagem principal
			DynamicVO mensagemVO = criarMensagemEmail(assunto, email, mensagem, smtpToUse, maxAttemptsToUse,
					connectionToUse);
			BigDecimal codigoFila = mensagemVO.asBigDecimal("CODFILA");

			// Processa anexo se existir
			if (anexo != null && anexo.length > 0) {
				BigDecimal nuAnexo = processarAnexo(anexo, tipoAnexo, nomeArquivo);
				vincularAnexoAMensagem(codigoFila, nuAnexo);
			}

			return codigoFila;
		} catch (Exception e) {
			throw new MGEModelException("Erro ao enviar e-mail: " + e.getMessage(), e);
		} finally {
			JapeSession.close(hnd);
		}
	}

	/**
	 * Cria o registro da mensagem na tabela MSDFilaMensagem
	 */
	private DynamicVO criarMensagemEmail(String assunto, String email, String mensagem, BigDecimal smtp,
			BigDecimal maxAttempts, BigDecimal connection) throws Exception {

		EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
		DynamicVO dynamicVO = (DynamicVO) entityVO;

		dynamicVO.setProperty("ASSUNTO", assunto);
		dynamicVO.setProperty("DTENTRADA", TimeUtils.getNow());
		dynamicVO.setProperty("STATUS", "Pendente");
		dynamicVO.setProperty("EMAIL", email);
		dynamicVO.setProperty("TENTENVIO", BigDecimal.ONE);
		dynamicVO.setProperty("MENSAGEM", mensagem.toCharArray());
		dynamicVO.setProperty("TIPOENVIO", "E");
		dynamicVO.setProperty("MAXTENTENVIO", maxAttempts);
		dynamicVO.setProperty("CODSMTP", smtp);
		dynamicVO.setProperty("CODCON", connection);

		PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDFilaMensagem", entityVO);
		return (DynamicVO) createEntity.getValueObject();
	}

	/**
	 * Processa o anexo do e-mail
	 */
	private BigDecimal processarAnexo(byte[] anexo, String tipoAnexo, String nomeArquivo) throws Exception {

		EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoMensagem");
		DynamicVO dynamicVO = (DynamicVO) entityVO;

		dynamicVO.setProperty("NOMEARQUIVO", nomeArquivo);
		dynamicVO.setProperty("TIPO", tipoAnexo);
		dynamicVO.setProperty("ANEXO", anexo);

		PersistentLocalEntity createEntity = dwfFacade.createEntity("AnexoMensagem", entityVO);
		DynamicVO savedVO = (DynamicVO) createEntity.getValueObject();
		return savedVO.asBigDecimal("NUANEXO");
	}

	/**
	 * Vincula o anexo à mensagem
	 */
	private void vincularAnexoAMensagem(BigDecimal codigoFila, BigDecimal nuAnexo) throws Exception {

		EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoPorMensagem");
		DynamicVO dynamicVO = (DynamicVO) entityVO;

		dynamicVO.setProperty("CODFILA", codigoFila);
		dynamicVO.setProperty("NUANEXO", nuAnexo);

		dwfFacade.createEntity("AnexoPorMensagem", entityVO);
	}

	// ... [outros membros da classe permanecem iguais] ...

	/**
	 * Carrega um template HTML a partir do classpath (compatível com Java 8)
	 * 
	 * @param templatePath Caminho do template (ex: "templates/email.html")
	 * @return Conteúdo do template como String
	 * @throws IOException Se o template não for encontrado
	 */
	public String loadTemplate(String templatePath) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = getClass().getClassLoader().getResourceAsStream(templatePath);
			if (inputStream == null) {
				throw new IOException("Template não encontrado no caminho: " + templatePath);
			}

			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result.toString(StandardCharsets.UTF_8.name());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// Logar o erro se necessário
				}
			}
		}
	}

	/**
	 * Preenche um template com múltiplos valores
	 * 
	 * @param template     Template HTML como String
	 * @param placeholders Mapa de chave-valor para substituição
	 * @return Template preenchido
	 */
	public String fillTemplate(String template, Map<String, String> placeholders) {
		if (template == null || placeholders == null) {
			return template; // evita erro se vier nulo
		}

		String result = template;
		for (Map.Entry<String, String> entry : placeholders.entrySet()) {
			// se o valor do placeholder for nulo, substitui por vazio
			String value = entry.getValue() == null ? "" : entry.getValue();
			result = result.replace("${" + entry.getKey() + "}", value);
		}
		return result;
	}

	/**
	 * Preenche um template com um único valor
	 * 
	 * @param template    Template HTML como String
	 * @param placeholder Nome do placeholder (sem ${})
	 * @param value       Valor para substituição
	 * @return Template preenchido
	 */
	public String fillTemplate(String template, String placeholder, String value) {
		return template.replace("${" + placeholder + "}", value);
	}

}