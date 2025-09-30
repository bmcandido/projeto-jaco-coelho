package br.com.sankhya.jaco.integracao.event;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jaco.integracao.helper.ImportacaoHelper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;

public class ImportacaoEvent implements EventoProgramavelJava {

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		throw new Exception(
				"<b>Operação Não Permitida</b>\n\n<b>Motivo: </b>Não é permitido remover registros, pois são informações referentes a Integração de Software.\n\n");
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

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

		DynamicVO registroVO = (DynamicVO) event.getVo();

		registroVO.setProperty("DHINC", TimeUtils.getNow());
		registroVO.setProperty("CODUSUINC", AuthenticationInfo.getCurrent().getUserID());

		ImportacaoHelper helper = new ImportacaoHelper();
		helper.importaDados(registroVO);

	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {

	}

}
