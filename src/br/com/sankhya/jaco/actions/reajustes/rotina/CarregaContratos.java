package br.com.sankhya.jaco.actions.reajustes.rotina;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.actions.reajustes.utils.ErrorUtil;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.util.DateTimeUtil;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class CarregaContratos implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {

		JdbcWrapper jdbc = null;
		SimpleDateFormat mesAno = new SimpleDateFormat("dd/MM/yyyy");
		BigDecimal codReajuste = null;
		// SimpleDateFormat dtFormatSkw = new SimpleDateFormat("dd/MM/yyyy");
		// BigDecimal codUsuLog = AuthenticationInfo.getCurrent().getUserID();
		// String dtNow = dtFormatSkw.format(TimeUtils.getNow());

		try {

			EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			jdbc = dwfEntityFacade.getJdbcWrapper();

			if (contexto.getLinhas().length == 0) {

				throw new Exception("Selecione um registro!");

			}

			/*************************************
			 * Faz o Loop nos Registros Selecionados
			 ***************************************/

			for (Registro registro : contexto.getLinhas()) {

				PersistentLocalEntity persistenceReajuste = dwfEntityFacade.findEntityByPrimaryKey("AD_JURCONREAJUSTE",
						registro.getCampo("CODREAJUSTE"));
				DynamicVO reajusteVO = (DynamicVO) persistenceReajuste.getValueObject();

				final String mesAnoReajuste = mesAno.format(reajusteVO.asTimestamp("DTREFREAJUSTE")).toString()
						.substring(3, 10);

				/***************************************
				 * Valida o Status Cancelado
				 ***************************************/

				if ("CN".equals(reajusteVO.asString("STATUS"))) {

					throw new Exception(ErrorUtil.formataHtml("Operacao não permitida!",
							"Reajuste encontra-se Cancelado!", "Selecionar um reajuste Valido!"));
				}

				if ("RE".equals(reajusteVO.asString("STATUS"))) {

					throw new Exception(ErrorUtil.formataHtml("Operacao não permitida!",
							"Contrato(s) já se encontram reajustados!", "Selecionar um reajuste valido!"));
				}

				QueryExecutor queryGetContratosReajustaveis = contexto.getQuery();

				/***************************************
				 * Query para buscar os contratos reajustaveis
				 ***************************************/

				try {

					queryGetContratosReajustaveis.nativeSelect("SELECT CON.CODCONTRATO      AS CODCONTRATO,\r\n"
							+ "       CON.CODMOEDA         AS CODMOEDA,\r\n"
							+ "       CON.DTULTIMOREAJUSTE AS DTULTIMOREAJUSTE,\r\n"
							+ "       CON.DTPROXREAJUSTE   AS DTPROXREAJUSTE,\r\n"
							+ "       CON.CODPARC          AS CODPARC\r\n"
							+ "  FROM AD_JURCONTRATOS CON\r\n"
							+ " WHERE TO_CHAR(TRUNC(CON.DTPROXREAJUSTE, 'MM'), 'MM/YYYY') <=\r\n"
							+ "       TO_CHAR('03/2024')\r\n"
							+ "   and CON.ATIVO = 'S'\r\n"
							+ "   AND EXISTS (SELECT 1\r\n"
							+ "          FROM AD_JURSUBTIPOSCONTRATOS SUB\r\n"
							+ "         INNER JOIN AD_JURSUBTIPOS TIP\r\n"
							+ "            ON TIP.SEQUENCIA = SUB.CODSUB\r\n"
							+ "         WHERE SUB.CODCONTRATO = CON.CODCONTRATO\r\n"
							+ "           AND NVL(TIP.ACEITAREAJUSTE, 'N') = 'S')\r\n"
							);

				} catch (Exception e) {
					throw new Exception("Mes do Reajuste é : " + mesAnoReajuste);
				}

				if (queryGetContratosReajustaveis.next()) {

					while (queryGetContratosReajustaveis.next()) {

						codReajuste = reajusteVO.asBigDecimal("CODREAJUSTE");
						BigDecimal codContrato = queryGetContratosReajustaveis.getBigDecimal("CODCONTRATO");
						BigDecimal codParc = queryGetContratosReajustaveis.getBigDecimal("CODPARC");
						BigDecimal codMoeda = queryGetContratosReajustaveis.getBigDecimal("CODMOEDA");

						EntityFacade dwfFacadeJur = EntityFacadeFactory.getDWFFacade();

						EntityVO agendaJuridicaEntityVO = dwfFacadeJur
								.getDefaultValueObjectInstance("AD_JURCONREAJUSTEITENS");

						DynamicVO reajusteItensVO = (DynamicVO) agendaJuridicaEntityVO;

						reajusteItensVO.setProperty("CODREAJUSTE", codReajuste);
						reajusteItensVO.setProperty("CODCONTRATO", codContrato);
						reajusteItensVO.setProperty("CODPARC", codParc);
						reajusteItensVO.setProperty("CODMOEDA", codMoeda);

						// Persiste os dados

						PersistentLocalEntity createEntity = dwfFacadeJur.createEntity("AD_JURCONREAJUSTEITENS",
								agendaJuridicaEntityVO);

						createEntity.getValueObject();

						// Inserindo os Itens atraves da Persistencia de Dados

						Collection<DynamicVO> itensReajuste = new ArrayList<DynamicVO>();

						FinderWrapper finderSubtipos = new FinderWrapper("AD_JURCONREAJUSTEITENS",
								"this.CODREAJUSTE = ?", "this.CODCONTRATO = ?",
								new Object[] { codReajuste, codContrato });

						Collection<DynamicVO> subtiposRetornados = dwfEntityFacade
								.findByDynamicFinderAsVO(finderSubtipos);

						itensReajuste.addAll(subtiposRetornados);

					}

				} else {

					throw new Exception(ErrorUtil.formataHtml("Operacao não permitida!",
							"Não foram encontrados registros na referencia informada : " + mesAnoReajuste,
							"Verificar os dados e refazer a operação!"));
				}

				contexto.setMensagemRetorno("Registros inseridos com sucesso!");

			}

		} finally {
			JdbcWrapper.closeSession(jdbc);
		}

	}

}
