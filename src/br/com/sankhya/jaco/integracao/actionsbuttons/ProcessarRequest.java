package br.com.sankhya.jaco.integracao.actionsbuttons;


import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.integracao.helper.ImportacaoHelper;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ProcessarRequest implements AcaoRotinaJava {

	@Override
	public void doAction(ContextoAcao contexto) throws Exception {
		
		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		
		for (Registro linha : contexto.getLinhas()) {
			
			PersistentLocalEntity persistentLocalEntity = dwfFacade.findEntityByPrimaryKey("AD_TGJIMP", linha.getCampo("SEQUENCIA"));
			EntityVO vo = persistentLocalEntity.getValueObject();
			DynamicVO dynamicVO = (DynamicVO) vo;
			
			ImportacaoHelper helper = new ImportacaoHelper();
			helper.importaDados(dynamicVO);
			
			persistentLocalEntity.setValueObject(vo);
				  
		}
		
		contexto.setMensagemRetorno("Executado com Sucesso");
	}
		
}
		
