package br.com.sankhya.jaco.integracao.actionsbuttons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.sankhya.util.BigDecimalUtil;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.integracao.dao.Contratos;
import br.com.sankhya.jaco.integracao.helper.ContratosHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ReprocessarContrato implements AcaoRotinaJava {
	
	HelperLog helperLog = new HelperLog();
	@Override
	public void doAction(ContextoAcao contexto) throws Exception {

		boolean debug = true;

		helperLog.debug(debug, "Reprocessando Contrato");

		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

		for (Registro linha : contexto.getLinhas()) {

			try {

				Contratos contratos = new Contratos();
				boolean isAtivo = true;

				contratos.setAtivo(true);
				contratos.setIdContrato((BigDecimal) linha.getCampo("IDCONTRATO"));
				contratos.setAtivo(isAtivo);
				contratos.setNumero((String) linha.getCampo("NUMERO"));
				contratos.setAlias((String) linha.getCampo("ALIAS"));

				Object campo1 = linha.getCampo("DATAINICIO");
				Timestamp dataInicio = (Timestamp) campo1;

				contratos.setDataInicio(dataInicio.toString());

				Object campo2 = linha.getCampo("DATAFIM");
				Timestamp dataFim = (Timestamp) campo2;
				if (dataFim != null) {
					contratos.setDataFim(dataFim.toString());
				}

				contratos.setIdCliente((String) linha.getCampo("IDCLIENTE"));
				contratos.setNomeCliente((String) linha.getCampo("NOMECLIENTE"));
				// contratos.setTipoContrato(dynamicVO.asString("TIPOCONTRATO"));
				BigDecimal sequencia = (BigDecimal) linha.getCampo("SEQUENCIA");

				Contratos contratoNew = ContratosHelper.importarContrato(debug, dwfFacade, contratos, sequencia);

				if (contratoNew.getCodContratoSnk() != null
						&& contratoNew.getCodContratoSnk().compareTo(BigDecimalUtil.ZERO_VALUE) > 0) {

					SessionHandle hnd = null;
					try {

						hnd = JapeSession.open();

						JapeFactory.dao("AD_CONTRATOS").prepareToUpdateByPK(linha.getCampo("IDCONTRATO"))
								.set("CODCONTRATO", contratoNew.getCodContratoSnk()).update();

					} catch (Exception e) {
						helperLog.logErrorAndReturnMessage(true, "Erro atualizar numero de contrato!", e);

						MGEModelException.throwMe(e);
					} finally {
						JapeSession.close(hnd);
					}

					contexto.setMensagemRetorno("Executado com Sucesso");
				} else {
					contexto.setMensagemRetorno("Ocorreu um erro ao executar o processo!");
				}
			} catch (Exception e) {
				helperLog.error(debug, "Erro ao reprocessar o contrato", e, true);
			}

		}
	}
}
