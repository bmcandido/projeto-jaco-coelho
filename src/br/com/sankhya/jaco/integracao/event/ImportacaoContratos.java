package br.com.sankhya.jaco.integracao.event;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jaco.integracao.dao.Contratos;
import br.com.sankhya.jaco.integracao.helper.ContratosHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ImportacaoContratos implements EventoProgramavelJava {

	// Colocar na tabela AD_CONTRATOS
	
	HelperLog helperLog = new HelperLog();

	@Override
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

		boolean debug = true;

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();

			helperLog.debug(debug, "Entrando no After Insert");

			DynamicVO vo = (DynamicVO) persistenceEvent.getVo();

			Contratos contratos = new Contratos();

			contratos.setIdContrato(vo.asBigDecimal("IDCONTRATO"));
			contratos.setNumero(vo.asString("NUMERO"));
			contratos.setIdCliente(vo.asString("IDCLIENTE"));

			helperLog.info(debug, "ID Contrato : " + vo.asBigDecimal("IDCONTRATO") + "\nNumero Contrato : "
					+ vo.asString("NUMERO") + "\nId Cliente :" + vo.asString("IDCLIENTE"));

			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

			Contratos contratosNew = ContratosHelper.importarContrato(debug, dwfFacade, contratos,
					vo.asBigDecimal("SEQUENCIA"));

			vo.setProperty("CODPARC", contratosNew.getCodParceiroSnk());
			vo.setProperty("CODCONTRATO", contratosNew.getCodContratoSnk());

			FluidUpdateVO update = JapeFactory.dao("AD_CONTRATOS").prepareToUpdate(vo);
			update.set("CODPARC", contratosNew.getCodParceiroSnk()).set("CODCONTRATO", contratosNew.getCodContratoSnk())
					.update();

		} catch (Exception e) {
			helperLog.logErrorAndReturnMessage(true, "Erro ao gerar Contrato", e);
		} finally {
			JapeSession.close(hnd);
		}

	}

	@Override
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

	}

	@Override
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

	}

	@Override
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

	}

	@Override
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

	}

	@Override
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

	}

	@Override
	public void beforeCommit(TransactionContext transactionContext) throws Exception {

	}
}
