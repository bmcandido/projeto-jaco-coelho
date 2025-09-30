package br.com.sankhya.jaco.integracao.event;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jaco.integracao.helper.ParceiroHelper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class CDParceiroEvent implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		throw new Exception(
				"<b>Operação Não Permitida</b>\n\n<b>Motivo: </b>Não é permitido remover registros, pois são informações referentes a Composisso de Dados decorrrido de Integração entre Software.\n\n");
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {

	}

	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {

	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {

	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {

	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {

		ParceiroHelper parceiro = new ParceiroHelper();
		parceiro.importarParceiro((DynamicVO) event.getVo());

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {

		ParceiroHelper parceiro = new ParceiroHelper();
		parceiro.importarParceiro((DynamicVO) event.getVo());

	}

}
