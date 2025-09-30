package br.com.sankhya.jaco.integracao.actionsbuttons;


import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.integracao.helper.ParceiroHelper;
import br.com.sankhya.jape.EntityFacade;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;

public class CadastrarParceiro implements AcaoRotinaJava
{
    public void doAction(final ContextoAcao contexto) throws Exception {
        final EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        Registro[] linhas;
        for (int length = (linhas = contexto.getLinhas()).length, i = 0; i < length; ++i) {
            final Registro linha = linhas[i];
            final DynamicVO registroVO = (DynamicVO)dwfFacade.findEntityByPrimaryKeyAsVO("AD_CLIENTES", linha.getCampo("IDPESSOA"));
            try {
                final ParceiroHelper parceiro = new ParceiroHelper();
                parceiro.importarParceiro(registroVO);
            }
            catch (Exception e) {
                throw new Exception("");
            }
        }
        contexto.setMensagemRetorno("Executado com Sucesso");
    }
}
