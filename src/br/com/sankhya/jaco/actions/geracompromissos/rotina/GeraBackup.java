package br.com.sankhya.jaco.actions.geracompromissos.rotina;

import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.jaco.actions.geracompromissos.utils.JacoUtils;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.TimeUtils;
import java.math.BigDecimal;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.sql.Timestamp;
import com.sankhya.util.BigDecimalUtil;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

public class GeraBackup implements AcaoRotinaJava {
	public void doAction(final ContextoAcao contextoAcao) throws Exception {
		final BigDecimal p_CodContrato = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("CODCONTRATO"));
		final BigDecimal p_CodParc = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("CODPARC"));
		final BigDecimal p_SeqSub = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("SEQSUB"));
		final Timestamp p_Referencia = (Timestamp) contextoAcao.getParam("DTBASEREF");
		final BigDecimal p_QtdeParcelas = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("QTDPARCELAS"));
		final BigDecimal p_VlrBase = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("VLRBASE"));
		final BigDecimal p_VlrParcela = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("VLRPARCELAS"));
		final BigDecimal p_IdProcesso = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("IDPROCESSO"));
		final String p_GeraFatAuto = contextoAcao.getParam("FATAUTO").toString();
		int qtdeParcelas = 0;
		if (p_QtdeParcelas == null || p_QtdeParcelas.intValue() <= 0) {
			final StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("Opera\u00e7\u00e3o n\u00e3o concluida!\r\n");
			stringBuffer.append("<b>Motivo:</b> Parcelas n\u00e3o podem ser menor igual a zero!\r\n");
			stringBuffer.append("<b>Solu\u00e7\u00e3o:</b> Cancelar o faturamento e repetir a opera\u00e7\u00e3o!\r\n");
			throw new Exception(stringBuffer.toString());
		}
		final StringBuffer stringBuffer = new StringBuffer();
		final QueryExecutor queryExecutor = contextoAcao.getQuery();
		final QueryExecutor queryProcessos = contextoAcao.getQuery();
		final QueryExecutor queryJurSubTiposContrato = contextoAcao.getQuery();
		queryJurSubTiposContrato.setParam("CODCONTRATO", (Object) p_CodContrato);
		queryJurSubTiposContrato.setParam("SEQSUB", (Object) p_SeqSub);
		queryJurSubTiposContrato.nativeSelect(
				" SELECT COUNT(1)\n   FROM AD_JURSUBTIPOSCONTRATOS DD\n  WHERE DD.CODCONTRATO = {CODCONTRATO}\n    AND DD.CODSUB = {SEQSUB}");
		if (queryJurSubTiposContrato.next() && queryJurSubTiposContrato.getBigDecimal(1).intValue() == 0) {
			stringBuffer.append("Opera\u00e7\u00e3o n\u00e3o permitida!<br><br>");
			stringBuffer.append("<b>Motivo:</b> N\u00e3o existe configura\u00e7\u00e3o para o Sub - Tipo : " + p_SeqSub
					+ " Contrato : " + p_CodContrato + "<br><br>");
			stringBuffer.append(
					"<b>Solu\u00e7\u00e3o:</b> Verificar as configura\u00e7\u00f5es do Contrato e refazer!<br><br>");
			stringBuffer.append("\r\n");
			throw new Exception(stringBuffer.toString());
		}
		queryExecutor.setParam("IDPROCESSO", (Object) p_IdProcesso);
		queryExecutor.nativeSelect(" SELECT COUNT(1)\n   FROM AD_PROCESSOS D\n  WHERE D.IDPROCESSO = {IDPROCESSO}");
		if (!queryExecutor.next()) {
			stringBuffer.append("Opera\u00e7\u00e3o n\u00e3o permitida!\r\n");
			stringBuffer.append("<b>Motivo:</b> Processo n\u00e3o Localizado!\r\n");
			stringBuffer.append("<b>Solu\u00e7\u00e3o:</b> Refazer a opera\u00e7\u00e3o\r\n");
			throw new Exception(stringBuffer.toString());
		}
		queryProcessos.setParam("IDPROCESSO", (Object) p_IdProcesso);
		queryProcessos.setParam("CODCONTRATO", (Object) p_CodContrato);
		queryProcessos.nativeSelect(
				"SELECT DISTINCT \n       D.NPC                            AS V_NPC\n     , D.PASTACPPRO                     AS V_PASTACPPRO\n     , D.NOMENATUREZA                   AS V_NOMENATUREZA\n     , D.PROTOCOLOATUAL                 AS V_PROTOCOLOATUAL\n     , D.NOMEJURIDICOSEGURADORA         AS V_NOMEJURIDICOSEGURADORA\n     , D.VALORRISCOCALCULOHONORARIOS    AS V_VALORRISCOCALCULOHONORARIOS\n     , D.SINISTROJUDICIAL               AS V_SINISTROJUDICIAL\n     , D.DATAHORACADASTRO               AS V_DATAHORACADASTRO\n     , D.NOMESEGMENTOSEGURADORA         AS V_NOMESEGMENTOSEGURADORA\n     , (SELECT NVL(MAX(S. CODUF), 0)\n          FROM TSIUFS S\n         WHERE S.DESCRICAO = UPPER(VARCHAR_KEY(D.NOMEESTADO))) AS V_CODUF\n     , D.CPFSEGUNDOTITULARCC            AS V_CPFSEGUNDOTITULARCC\n     , D.NOMECENTROCUSTO                AS V_NOMECENTROCUSTO\n     , D.RAMO                           AS V_RAMO\n     , D.PRODUTO                        AS V_PRODUTO\n     , D.NOMESUBGRUPOSEGURADORA         AS V_NOMESUBGRUPOSEGURADORA\n     , D.APOLICE                        AS V_APOLICE\n     , CASE WHEN D.NOMEADVERSOPRINCIPAL IS NOT NULL AND D.NOMECLIENTEPRINCIPAL IS NOT NULL \n            THEN D.NOMEADVERSOPRINCIPAL || ' X ' || D.NOMECLIENTEPRINCIPAL\n        END                             AS V_PARTES\n     , (SELECT MAX(DD.CODPARC)\n          FROM AD_JURCONTRATOS DD\n         WHERE DD.CODCONTRATO = {CODCONTRATO}) AS V_CODPARC \n  FROM AD_PROCESSOS D\n WHERE D.IDPROCESSO = {IDPROCESSO}");
		if (queryProcessos.next()) {
			final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			Integer desdob = 0;
			do {
				final EntityVO agendaJuridicaEntityVO = dwfFacade.getDefaultValueObjectInstance("AD_JURCOMPROMISSOS");
				final DynamicVO agendaJuridicaVO = (DynamicVO) agendaJuridicaEntityVO;
				agendaJuridicaVO.setProperty("IDPROCESSO", (Object) p_IdProcesso);
				agendaJuridicaVO.setProperty("NPC", (Object) queryProcessos.getString("V_NPC"));
				agendaJuridicaVO.setProperty("SEQSUB", (Object) p_SeqSub);
				agendaJuridicaVO.setProperty("CODCONTRATO", (Object) p_CodContrato);
				agendaJuridicaVO.setProperty("CPPRO", (Object) queryProcessos.getString("V_PASTACPPRO"));
				agendaJuridicaVO.setProperty("IDSEVEN", (Object) p_IdProcesso);
				agendaJuridicaVO.setProperty("TITULOPARTES", (Object) queryProcessos.getString("V_PARTES"));
				agendaJuridicaVO.setProperty("ORRGAOPRINCIPAL", (Object) queryProcessos.getString("V_NOMENATUREZA"));
				agendaJuridicaVO.setProperty("NUMEROPROCESSO", (Object) queryProcessos.getString("V_PROTOCOLOATUAL"));
				agendaJuridicaVO.setProperty("PASTA", (Object) queryProcessos.getString("V_PASTACPPRO"));
				agendaJuridicaVO.setProperty("CALLCENTER", (Object) null);
				agendaJuridicaVO.setProperty("JURIDICODASEGURADORA",
						(Object) queryProcessos.getString("V_NOMEJURIDICOSEGURADORA"));
				agendaJuridicaVO.setProperty("VLRRISCO",
						(Object) queryProcessos.getBigDecimal("V_VALORRISCOCALCULOHONORARIOS"));
				agendaJuridicaVO.setProperty("DTCADASTRO", (Object) queryProcessos.getTimestamp("V_DATAHORACADASTRO"));
				agendaJuridicaVO.setProperty("SEGMENTO", (Object) queryProcessos.getString("V_NOMESEGMENTOSEGURADORA"));
				agendaJuridicaVO.setProperty("CODUF", (Object) queryProcessos.getBigDecimal("V_CODUF"));
				agendaJuridicaVO.setProperty("VLRCOBRANCA", (Object) p_VlrParcela);
				agendaJuridicaVO.setProperty("PARCELA", (Object) BigDecimal.valueOf(1L));
				agendaJuridicaVO.setProperty("DTAUDIENCIA", (Object) TimeUtils.getNow());
				agendaJuridicaVO.setProperty("NOMEDOPREPOSTO", (Object) null);
				agendaJuridicaVO.setProperty("CPFCGCAUTOR", (Object) queryProcessos.getString("V_CPFSEGUNDOTITULARCC"));
				agendaJuridicaVO.setProperty("CENTROCUSTOCLI", (Object) queryProcessos.getString("V_NOMECENTROCUSTO"));
				agendaJuridicaVO.setProperty("RAMO", (Object) queryProcessos.getString("V_RAMO"));
				agendaJuridicaVO.setProperty("PRODUTO", (Object) queryProcessos.getString("V_PRODUTO"));
				agendaJuridicaVO.setProperty("SUBGRUPODASEGURADORA",
						(Object) queryProcessos.getString("V_NOMESUBGRUPOSEGURADORA"));
				agendaJuridicaVO.setProperty("CODPARC", (Object) queryProcessos.getBigDecimal("V_CODPARC"));
				agendaJuridicaVO.setProperty("APOLICE", (Object) queryProcessos.getString("V_APOLICE"));
				agendaJuridicaVO.setProperty("GERFIN", (Object) null);
				agendaJuridicaVO.setProperty("CODPARCFAT", (Object) queryProcessos.getBigDecimal("V_CODPARC"));
				agendaJuridicaVO.setProperty("SELECIONADO", (Object) "N");
				agendaJuridicaVO.setProperty("CODUSU", (Object) AuthenticationInfo.getCurrent().getUserID());
				agendaJuridicaVO.setProperty("CODEMP", (Object) BigDecimal.ONE);
				agendaJuridicaVO.setProperty("BASECALCULO", (Object) p_VlrBase);
				agendaJuridicaVO.setProperty("MANUAL", (Object) "S");
				agendaJuridicaVO.setProperty("DTTRANSITOJULGADO", (Object) null);
				agendaJuridicaVO.setProperty("TIPOPREPOSTO", (Object) null);
				agendaJuridicaVO.setProperty("TIPFAT", (Object) BigDecimal.ONE.toString());
				agendaJuridicaVO.setProperty("STATUSCOMPROMISSO", (Object) BigDecimal.ONE.toString());
				agendaJuridicaVO.setProperty("DTPREVVENC", (Object) JacoUtils.dataAddMonth(p_Referencia, qtdeParcelas));
				desdob = qtdeParcelas + 1;
				agendaJuridicaVO.setProperty("DESDOBRAMENTO", (Object) desdob.toString());
				agendaJuridicaVO.setProperty("INTERNO",
						(Object) "br.com.farbz.jacocoelho.actions.jurcompromissos.GerarCompromissos");
				agendaJuridicaVO.setProperty("CODUSUINC", (Object) AuthenticationInfo.getCurrent().getUserID());
				agendaJuridicaVO.setProperty("DHINC", (Object) TimeUtils.getNow());
				agendaJuridicaVO.setProperty("DHALTER", (Object) TimeUtils.getNow());
				agendaJuridicaVO.setProperty("GERAFATAUTO", (Object) p_GeraFatAuto);
				agendaJuridicaVO.setProperty("CODPARCFAT", (Object) p_CodParc);
				final PersistentLocalEntity createEntity = dwfFacade.createEntity("AD_JURCOMPROMISSOS",
						agendaJuridicaEntityVO);
				final DynamicVO save = (DynamicVO) createEntity.getValueObject();
			} while (++qtdeParcelas < p_QtdeParcelas.intValue());
		}
		contextoAcao.setMensagemRetorno(String.valueOf(qtdeParcelas) + " parcelas geradas com sucesso");
	}
}
