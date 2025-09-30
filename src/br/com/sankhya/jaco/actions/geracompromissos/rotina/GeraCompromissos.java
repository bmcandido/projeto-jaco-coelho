package br.com.sankhya.jaco.actions.geracompromissos.rotina;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.jaco.actions.geracompromissos.utils.JacoUtils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class GeraCompromissos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contextoAcao) throws Exception {

		// Seta os Parametros
		// Os parametros estão dentro da Aplicação na criação da procedure

		BigDecimal p_CodContrato = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("CODCONTRATO"));
		BigDecimal p_CodParc = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("CODPARC"));
		BigDecimal p_SeqSub = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("SEQSUB"));
		Timestamp p_Referencia = (Timestamp) contextoAcao.getParam("DTBASEREF");
		BigDecimal p_QtdeParcelas = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("QTDPARCELAS"));
		BigDecimal p_VlrBase = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("VLRBASE"));
		BigDecimal p_VlrParcela = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("VLRPARCELAS"));
		BigDecimal p_IdProcesso = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("IDPROCESSO"));
		String p_GeraFatAuto = contextoAcao.getParam("FATAUTO").toString();
		BigDecimal p_codEventoVinculado = BigDecimalUtil.getBigDecimal(contextoAcao.getParam("CODEVENTO"));

		int qtdeParcelas = 0;

		StringBuffer stringBuffer;

		// JapeWrapper vo = JapeFactory.dao(DynamicEntityNames.FINANCEIRA);
		

		if (p_QtdeParcelas != null && p_QtdeParcelas.intValue() > 0) {

			stringBuffer = new StringBuffer();

			QueryExecutor queryExecutor = contextoAcao.getQuery();

			QueryExecutor queryProcessos = contextoAcao.getQuery();

			QueryExecutor queryConfigSubContratos = contextoAcao.getQuery();

			queryExecutor.setParam("IDPROCESSO", p_IdProcesso);
			queryExecutor.nativeSelect(" SELECT COUNT(1)\n   FROM AD_PROCESSOS D\n  WHERE D.IDPROCESSO = {IDPROCESSO}");
			if (!queryExecutor.next()) {
				stringBuffer.append("Operação não permitida!\r\n");
				stringBuffer.append("<b>Motivo:</b> Processo não Localizado!\r\n");
				stringBuffer.append("<b>Solução:</b> Refazer a operação\r\n");
				throw new Exception(stringBuffer.toString());

			} else {

				queryProcessos.setParam("IDPROCESSO", p_IdProcesso);
				queryProcessos.setParam("CODCONTRATO", p_CodContrato);

				queryProcessos.nativeSelect(
						"SELECT DISTINCT \n       D.NPC                            AS V_NPC\n     , D.PASTACPPRO                     AS V_PASTACPPRO\n     , D.NOMENATUREZA                   AS V_NOMENATUREZA\n     , D.PROTOCOLOATUAL                 AS V_PROTOCOLOATUAL\n     , D.NOMEJURIDICOSEGURADORA         AS V_NOMEJURIDICOSEGURADORA\n , D.NOMEASSUNTO AS V_NOMEASSUNTO\n     , D.VALORRISCOCALCULOHONORARIOS    AS V_VALORRISCOCALCULOHONORARIOS\n     , D.SINISTROJUDICIAL               AS V_SINISTROJUDICIAL\n     , D.DATAHORACADASTRO               AS V_DATAHORACADASTRO\n     , D.NOMESEGMENTOSEGURADORA         AS V_NOMESEGMENTOSEGURADORA\n     , (SELECT NVL(MAX(S. CODUF), 0)\n          FROM TSIUFS S\n         WHERE S.DESCRICAO = UPPER(VARCHAR_KEY(D.NOMEESTADO))) AS V_CODUF \n     , D.CPFSEGUNDOTITULARCC            AS V_CPFSEGUNDOTITULARCC\n     , D.NOMECENTROCUSTO                AS V_NOMECENTROCUSTO\n     , D.RAMO                           AS V_RAMO\n     , D.PRODUTO                        AS V_PRODUTO\n     , D.NOMESUBGRUPOSEGURADORA         AS V_NOMESUBGRUPOSEGURADORA\n     , D.APOLICE                        AS V_APOLICE\n     , CASE WHEN D.NOMEADVERSOPRINCIPAL IS NOT NULL AND D.NOMECLIENTEPRINCIPAL IS NOT NULL \n            THEN D.NOMEADVERSOPRINCIPAL || ' X ' || D.NOMECLIENTEPRINCIPAL\n        END                             AS V_PARTES\n     , (SELECT MAX(DD.CODPARC)\n          FROM AD_JURCONTRATOS DD\n         WHERE DD.CODCONTRATO = {CODCONTRATO}) AS V_CODPARC \n  FROM AD_PROCESSOS D\n WHERE D.IDPROCESSO = {IDPROCESSO}");
				if (queryProcessos.next()) {

					EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
					Integer desdob = 0;

					queryConfigSubContratos.setParam("CODCONTRATO", p_CodContrato);
					queryConfigSubContratos.setParam("SEQSUB", p_SeqSub);
					queryConfigSubContratos.setParam("ASSUNTO", queryProcessos.getString("V_NOMEASSUNTO"));
					queryConfigSubContratos.setParam("CODEVENTO", p_codEventoVinculado);
					queryConfigSubContratos.setParam("ORGAOPRINCIPAL", queryProcessos.getString("V_NOMENATUREZA"));
					

					queryConfigSubContratos.nativeSelect("SELECT count(1) \r\n"
							+ "  FROM AD_JURSUBTIPOSCONTRATOS DD\r\n" + " WHERE DD.CODCONTRATO = {CODCONTRATO}  \r\n"
							+ "   AND DD.CODSUB = {SEQSUB} \r\n" 
							+ "   AND  DD.CODEVENTO = {CODEVENTO} \r\n"
							+ "   AND NVL(DD.ASSUNTO, 'TO') =\r\n"
							+ "       NVL(FC_RETASSUNTONEW_PS({ASSUNTO}, DD.ASSUNTO), 'TO')\r\n"
							+ "   AND NVL(UPPER(VARCHAR_KEY(NVL(DD.ORGAO, ' '))), 'Todos') = \r\n"
							+ "       NVL(UPPER(FC_RETORGAOPRINCIPAL_PS(DD.CODSUB, DD.ORGAO, {ORGAOPRINCIPAL})),'Todos')\r\n");

					if (queryConfigSubContratos.next() && queryConfigSubContratos.getBigDecimal(1).intValue() == 0) {
						stringBuffer.append("Operação não permitida!<br><br>");
						stringBuffer.append("<b>Motivo:</b> Não existe configuração para o Sub - Tipo : " + p_SeqSub
								+ " Contrato : " + p_CodContrato + " Evento : " + p_codEventoVinculado + "<br><br>");
						stringBuffer
								.append("<b>Solução:</b> Verificar as configurações do Contrato e refazer!<br><br>");
						stringBuffer.append("\r\n");
						throw new Exception(stringBuffer.toString());

					} else {

						do {

							EntityVO agendaJuridicaEntityVO = dwfFacade
									.getDefaultValueObjectInstance("AD_JURCOMPROMISSOS");

							DynamicVO agendaJuridicaVO = (DynamicVO) agendaJuridicaEntityVO;

							agendaJuridicaVO.setProperty("IDPROCESSO", p_IdProcesso);
							agendaJuridicaVO.setProperty("NPC", queryProcessos.getString("V_NPC"));
							agendaJuridicaVO.setProperty("SEQSUB", p_SeqSub);
							agendaJuridicaVO.setProperty("CODEVENTO", p_codEventoVinculado);
							agendaJuridicaVO.setProperty("CODCONTRATO", p_CodContrato);
							agendaJuridicaVO.setProperty("CPPRO", queryProcessos.getString("V_PASTACPPRO"));
							agendaJuridicaVO.setProperty("IDSEVEN", p_IdProcesso);
							agendaJuridicaVO.setProperty("TITULOPARTES", queryProcessos.getString("V_PARTES"));
							agendaJuridicaVO.setProperty("ORRGAOPRINCIPAL", queryProcessos.getString("V_NOMENATUREZA"));
							agendaJuridicaVO.setProperty("ASSUNTO", queryProcessos.getString("V_NOMEASSUNTO"));

							agendaJuridicaVO.setProperty("NUMEROPROCESSO",
									queryProcessos.getString("V_PROTOCOLOATUAL"));
							agendaJuridicaVO.setProperty("PASTA", queryProcessos.getString("V_PASTACPPRO"));
							agendaJuridicaVO.setProperty("CALLCENTER", (Object) null);
							agendaJuridicaVO.setProperty("JURIDICODASEGURADORA",
									queryProcessos.getString("V_NOMEJURIDICOSEGURADORA"));
							agendaJuridicaVO.setProperty("VLRRISCO",
									queryProcessos.getBigDecimal("V_VALORRISCOCALCULOHONORARIOS"));
							agendaJuridicaVO.setProperty("DTCADASTRO",
									queryProcessos.getTimestamp("V_DATAHORACADASTRO"));
							agendaJuridicaVO.setProperty("SEGMENTO",
									queryProcessos.getString("V_NOMESEGMENTOSEGURADORA"));
							agendaJuridicaVO.setProperty("CODUF", queryProcessos.getBigDecimal("V_CODUF"));
							agendaJuridicaVO.setProperty("VLRCOBRANCA", p_VlrParcela);
							agendaJuridicaVO.setProperty("PARCELA", BigDecimal.valueOf(1L));
							agendaJuridicaVO.setProperty("DTAUDIENCIA", TimeUtils.getNow());
							agendaJuridicaVO.setProperty("NOMEDOPREPOSTO", (Object) null);
							agendaJuridicaVO.setProperty("CPFCGCAUTOR",
									queryProcessos.getString("V_CPFSEGUNDOTITULARCC"));
							agendaJuridicaVO.setProperty("CENTROCUSTOCLI",
									queryProcessos.getString("V_NOMECENTROCUSTO"));
							agendaJuridicaVO.setProperty("RAMO", queryProcessos.getString("V_RAMO"));
							agendaJuridicaVO.setProperty("PRODUTO", queryProcessos.getString("V_PRODUTO"));
							agendaJuridicaVO.setProperty("SUBGRUPODASEGURADORA",
									queryProcessos.getString("V_NOMESUBGRUPOSEGURADORA"));
							agendaJuridicaVO.setProperty("CODPARC", queryProcessos.getBigDecimal("V_CODPARC"));
							agendaJuridicaVO.setProperty("APOLICE", queryProcessos.getString("V_APOLICE"));
							agendaJuridicaVO.setProperty("GERFIN", (Object) null);
							agendaJuridicaVO.setProperty("CODPARCFAT", queryProcessos.getBigDecimal("V_CODPARC"));
							agendaJuridicaVO.setProperty("SELECIONADO", "N");
							agendaJuridicaVO.setProperty("CODUSU", AuthenticationInfo.getCurrent().getUserID());
							agendaJuridicaVO.setProperty("CODEMP", BigDecimal.ONE);
							agendaJuridicaVO.setProperty("BASECALCULO", p_VlrBase);
							agendaJuridicaVO.setProperty("MANUAL", "S");
							agendaJuridicaVO.setProperty("DTTRANSITOJULGADO", (Object) null);
							agendaJuridicaVO.setProperty("TIPOPREPOSTO", (Object) null);
							agendaJuridicaVO.setProperty("TIPFAT", BigDecimal.ONE.toString());
							agendaJuridicaVO.setProperty("STATUSCOMPROMISSO", BigDecimal.ONE.toString());
							agendaJuridicaVO.setProperty("DTPREVVENC",
									JacoUtils.dataAddMonth(p_Referencia, qtdeParcelas));
							desdob = qtdeParcelas + 1;
							agendaJuridicaVO.setProperty("DESDOBRAMENTO", desdob.toString());
							agendaJuridicaVO.setProperty("INTERNO",
									"br.com.sankhya.jaco.actions.geracompromisso.GeraCompromissos - Inserção");
							agendaJuridicaVO.setProperty("CODUSUINC", AuthenticationInfo.getCurrent().getUserID());
							agendaJuridicaVO.setProperty("DHINC", TimeUtils.getNow());
							agendaJuridicaVO.setProperty("DHALTER", TimeUtils.getNow());
							agendaJuridicaVO.setProperty("GERAFATAUTO", p_GeraFatAuto);
							agendaJuridicaVO.setProperty("CODPARCFAT", p_CodParc);

							// Persiste

							PersistentLocalEntity createEntity = dwfFacade.createEntity("AD_JURCOMPROMISSOS",
									agendaJuridicaEntityVO);

							// Salva

							DynamicVO save = (DynamicVO) createEntity.getValueObject();
							++qtdeParcelas;
						} while (qtdeParcelas < p_QtdeParcelas.intValue());
					}

				}

			}

			contextoAcao.setMensagemRetorno(qtdeParcelas + " parcelas geradas com sucesso");
		} else {
			stringBuffer = new StringBuffer();
			stringBuffer.append("Operação não concluida!\r\n");
			stringBuffer.append("<b>Motivo:</b> Parcelas não podem ser menor igual a zero!\r\n");
			stringBuffer.append("<b>Solução:</b> Cancelar o faturamento e repetir a operação!\r\n");
			throw new Exception(stringBuffer.toString());
		}

	}
}
