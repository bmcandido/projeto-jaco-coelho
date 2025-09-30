package br.com.sankhya.jaco.actions.events;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EventsProcess implements EventoProgramavelJava {

	// private static final Logger LOGGER =
	// Logger.getLogger(EventsProcess.class.getName());
	
	HelperLog helperLog = new HelperLog();
	private void insereAgendaJuridica(PersistenceEvent event, String tipo) {
		SessionHandle hnd = null;

		try {

			boolean debug = true;

			helperLog.debug(debug,
					"Trata Exito no momento da execução do objeto insereAgendaJuridica do tipo : " + tipo);

			hnd = JapeSession.open();
			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO entityVO = event.getVo();
			DynamicVO processoVO = (DynamicVO) entityVO;

			ArrayList<String> lista = new ArrayList<String>();
			lista.add(StringUtils.replaceAccentuatedChars(StringUtils.getNullAsEmpty("Encerrado")).toUpperCase());
			// lista.add(StringUtils.replaceAccentuatedChars(StringUtils.getNullAsEmpty("AEncerrar")).toUpperCase());
			String status = StringUtils
					.replaceAccentuatedChars(StringUtils.getNullAsEmpty(processoVO.asString("STATUS"))).toUpperCase();

			boolean notGerExito = processoVO.asBoolean("NAOCOBRAREXITO");

			final JdbcWrapper jdbcWrapper = dwfFacade.getJdbcWrapper();

			final NativeSql nativeSqlCount = new NativeSql(jdbcWrapper);

			helperLog.info(debug, "Entrando na query");

			BigDecimal existsCompromisso = BigDecimal.ZERO;
			try {

				nativeSqlCount.appendSql("SELECT COUNT(1) CONTADOR \r\n"
						+ "                  FROM AD_PROCESSOS PRO\r\n"
						+ "             INNER JOIN AD_JURCONTRATOS CON \r\n"
						+ "                    ON CON.IDSEVEN = PRO.IDCLIENTE\r\n"
						+ "             WHERE EXISTS (SELECT 1\r\n"
						+ "                          FROM AD_JURSUBTIPOSCONTRATOS SUB\r\n"
						+ "                     WHERE SUB.CODCONTRATO = CON.CODCONTRATO \r\n"
						+ "                           AND SUB.CODSUB = 999\r\n"
						+ "                       AND NVL(SUB.EXITO, 'N') = 'S')\r\n"
						+ "              AND (NVL(CON.COBRAEXITOENCERRAMENTO, 'N') = 'N' OR\r\n"
						+ "                   NVL(PRO.NAOCOBRAREXITO, 'N') = 'S')  \r\n"
						+ "               AND NOT EXISTS (SELECT 1\r\n"
						+ "                      FROM AD_JURCOMPROMISSOS COM\r\n"
						+ "                     INNER JOIN AD_JURSUBTIPOSCONTRATOS SUB\r\n"
						+ "                        ON SUB.CODSUB = COM.SEQSUB\r\n"
						+ "												AND SUB.CODCONTRATO = COM.CODCONTRATO\r\n"
						+ "                     WHERE COM.CODCONTRATO = CON.CODCONTRATO\r\n"
						+ "                       AND COM.IDPROCESSO = PRO.IDPROCESSO\r\n"
						+ "                       AND NVL(SUB.EXITO, 'N') = 'S') \r\n"
						+ "               AND PRO.IDPROCESSO = :IDPROCESSO");

				nativeSqlCount.setNamedParameter("IDPROCESSO", processoVO.asBigDecimalOrZero("IDPROCESSO"));

				final ResultSet result = nativeSqlCount.executeQuery();

				if (result.next()) {
					existsCompromisso = result.getBigDecimal("CONTADOR");

				}

			} finally {

				JdbcWrapper.closeSession(jdbcWrapper);

			}

			helperLog.info(debug,
					"Passou pela consulta, parametros :\nNro Processo Jurídico "
							+ processoVO.asBigDecimalOrZero("IDPROCESSO") + "\nStatus : " + status + "\nContrato : "
							+ processoVO.asBigDecimalOrZero("CODCONTRATO") + "\nCompromisso Encessamento : "
							+ processoVO.asBigDecimalOrZero("IDCOMPROMISSOENCERRAMENTO") + "\nGera Exito : "
							+ notGerExito + "\nExiste Compromisso :" + existsCompromisso);

			if (lista.contains(status.trim())
					&& processoVO.asBigDecimalOrZero("CODCONTRATO").compareTo(BigDecimal.ZERO) != 0
					&& processoVO.asBigDecimalOrZero("IDCOMPROMISSOENCERRAMENTO").compareTo(BigDecimal.ZERO) == 0
					&& existsCompromisso.compareTo(BigDecimal.ZERO) > 0 && notGerExito == false) {

				helperLog.info(debug, "Entrou na condição para gerar o compromisso");

				EntityVO agendaJuridicaEntityVO = dwfFacade.getDefaultValueObjectInstance("AD_JURCOMPROMISSOS");

				DynamicVO agendaJuridicaVO = (DynamicVO) agendaJuridicaEntityVO;

				agendaJuridicaVO.setProperty("IDPROCESSO", processoVO.asBigDecimal("IDPROCESSO"));
				agendaJuridicaVO.setProperty("NPC", processoVO.asString("NPC"));
				agendaJuridicaVO.setProperty("SEQSUB", BigDecimal.valueOf(999L));
				agendaJuridicaVO.setProperty("CODCONTRATO", processoVO.asBigDecimal("CODCONTRATO"));
				agendaJuridicaVO.setProperty("CPPRO", processoVO.asString("PASTACPPRO"));
				agendaJuridicaVO.setProperty("IDSEVEN", BigDecimal.ZERO);
				agendaJuridicaVO.setProperty("TITULOPARTES", processoVO.asString("NOMEADVERSOPRINCIPAL") + " X "
						+ processoVO.asString("NOMECLIENTEPRINCIPAL"));
				agendaJuridicaVO.setProperty("ORRGAOPRINCIPAL", processoVO.asString("NOMENATUREZA"));
				agendaJuridicaVO.setProperty("NUMEROPROCESSO", processoVO.asString("PROTOCOLOATUAL"));
				agendaJuridicaVO.setProperty("PASTA", processoVO.asString("PASTACPPRO"));
				agendaJuridicaVO.setProperty("CALLCENTER", (Object) null);
				agendaJuridicaVO.setProperty("JURIDICODASEGURADORA", processoVO.asString("NOMEJURIDICOSEGURADORA"));
				agendaJuridicaVO.setProperty("VLRRISCO", processoVO.asBigDecimal("VALORRISCOCALCULOHONORARIOS"));
				agendaJuridicaVO.setProperty("DTCADASTRO", processoVO.asTimestamp("DATAHORACADASTRO"));
				agendaJuridicaVO.setProperty("SEGMENTO", processoVO.asString("NOMESEGMENTOSEGURADORA"));
				agendaJuridicaVO.setProperty("CODUF", processoVO.asBigDecimal("CODUF"));
				agendaJuridicaVO.setProperty("VLRCOBRANCA", BigDecimal.ZERO);
				agendaJuridicaVO.setProperty("PARCELA", BigDecimal.valueOf(99L));
				agendaJuridicaVO.setProperty("DTAUDIENCIA", TimeUtils.getNow());
				agendaJuridicaVO.setProperty("NOMEDOPREPOSTO", (Object) null);
				agendaJuridicaVO.setProperty("CPFCGCAUTOR", processoVO.asString("CPFSEGUNDOTITULARCC"));
				agendaJuridicaVO.setProperty("CENTROCUSTOCLI", processoVO.asString("NOMECENTROCUSTO"));
				agendaJuridicaVO.setProperty("RAMO", processoVO.asString("RAMO"));
				agendaJuridicaVO.setProperty("PRODUTO", processoVO.asString("PRODUTO"));
				agendaJuridicaVO.setProperty("SUBGRUPODASEGURADORA", processoVO.asString("NOMESUBGRUPOSEGURADORA"));
				agendaJuridicaVO.setProperty("CODPARC", processoVO.asBigDecimal("CODPARC"));
				agendaJuridicaVO.setProperty("APOLICE", processoVO.asString("APOLICE"));
				agendaJuridicaVO.setProperty("GERFIN", "S");
				agendaJuridicaVO.setProperty("CODPARCFAT", processoVO.asBigDecimal("CODPARC"));
				agendaJuridicaVO.setProperty("SELECIONADO", "N");
				agendaJuridicaVO.setProperty("CODUSU", AuthenticationInfo.getCurrent().getUserID());
				agendaJuridicaVO.setProperty("CODEMP", BigDecimal.ONE);
				agendaJuridicaVO.setProperty("BASECALCULO", processoVO.asBigDecimal("VALORRISCOCALCULOHONORARIOS"));
				agendaJuridicaVO.setProperty("MANUAL", "S");
				agendaJuridicaVO.setProperty("DTTRANSITOJULGADO", (Object) null);
				agendaJuridicaVO.setProperty("TIPOPREPOSTO", (Object) null);
				agendaJuridicaVO.setProperty("TIPFAT", BigDecimal.ONE.toString());
				agendaJuridicaVO.setProperty("DATAHORACONCLUSAO", TimeUtils.getNow());
				agendaJuridicaVO.setProperty("FORO", processoVO.asString("NOMEJUIZADO"));
				agendaJuridicaVO.setProperty("COMARCA", processoVO.asString("NOMECIDADE"));
				agendaJuridicaVO.setProperty("NRGO", processoVO.asString("NUMJURISDICAO"));
				agendaJuridicaVO.setProperty("STATUSCOMPROMISSO", BigDecimal.ONE.toString());
				agendaJuridicaVO.setProperty("TIPOACAO", processoVO.asString("TIPOACAO"));
				agendaJuridicaVO.setProperty("NOMEESTADO", processoVO.asString("NOMEESTADO"));
				agendaJuridicaVO.setProperty("STATUSPROCESSO", processoVO.asString("STATUS"));
				agendaJuridicaVO.setProperty("OBJETO", processoVO.asString("OBJETO"));
				agendaJuridicaVO.setProperty("PASTAMIGRADA", processoVO.asString("PASTAMIGRADA"));

				agendaJuridicaVO.setProperty("INTERNO",
						"Objeto : br.com.sankhya.jaco.actions.events/EventsProcess Descrição : Integração Tipo : "
								+ tipo);
				agendaJuridicaVO.setProperty("DHINC", TimeUtils.getNow());
				agendaJuridicaVO.setProperty("DHALTER", TimeUtils.getNow());

				// Adicionado dia 28/08/2024 para integração dos Contratos

				agendaJuridicaVO.setProperty("IDCONTRATOSEVEN", processoVO.asBigDecimalOrZero("IDCONTRATO"));

				PersistentLocalEntity createEntity = dwfFacade.createEntity("AD_JURCOMPROMISSOS",
						agendaJuridicaEntityVO);
				DynamicVO save = (DynamicVO) createEntity.getValueObject();
				processoVO.setProperty("IDCOMPROMISSOENCERRAMENTO", save.asBigDecimal("SEQUENCIA"));

				helperLog.info(debug, "Gerou o compromisso de Nro. " + save.asBigDecimal("SEQUENCIA"));
			}
		} catch (Exception e) {
			helperLog.error(true,
					"Ocorreu um erro ao inserir o compromisso de Encerramento: " + e.getMessage(),e);
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());

		} finally {
			JapeSession.close(hnd);
		}

	}

	public void beforeInsert(PersistenceEvent event) throws Exception {

		helperLog.info(true, "Entrou no insert para o beforeInsert dentro da tabela");
		EntityVO entityVO = event.getVo();
		DynamicVO processoVO = (DynamicVO) entityVO;
		processoVO.setProperty("CODUSUINC", AuthenticationInfo.getCurrent().getUserID());
		processoVO.setProperty("DHINC", TimeUtils.getNow());
		processoVO.setProperty("CODUSUALTER", AuthenticationInfo.getCurrent().getUserID());
		processoVO.setProperty("DHALTER", TimeUtils.getNow());

		this.insereAgendaJuridica(event, "Inclusão");
	}

	public void beforeUpdate(PersistenceEvent event) throws Exception {

		helperLog.info(true, "Entrou no update para o beforeUpdate dentro da tabela");
		EntityVO entityVO = event.getVo();
		DynamicVO processoVO = (DynamicVO) entityVO;
		processoVO.setProperty("CODUSUALTER", AuthenticationInfo.getCurrent().getUserID());
		processoVO.setProperty("DHALTER", TimeUtils.getNow());
		this.insereAgendaJuridica(event, "Alteração");
	}

	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {
	}

	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
	}

	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
	}

	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {
	}

	public void beforeCommit(TransactionContext transactionContext) throws Exception {
	}

}
