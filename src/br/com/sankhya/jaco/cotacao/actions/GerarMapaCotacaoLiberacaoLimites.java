package br.com.sankhya.jaco.cotacao.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.helpers.RelatoriosHelper;
import br.com.sankhya.jaco.integracao.helper.FormatExceptionHTMLHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerarMapaCotacaoLiberacaoLimites implements AcaoRotinaJava {

    HelperLog helperLog = new HelperLog();
    boolean showLog = true;

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {


        EntityFacade dwfEntityFacade;
        JdbcWrapper jdbc = null;

        helperLog.debug(showLog, "Executando objeto doAction() em GerarMapaCotacaoLiberacaoLimites");


        helperLog.error(showLog, "Nenhuma linha selecionada");
        if (contexto.getLinhas().length == 0) {
            throw new Exception(FormatExceptionHTMLHelper.formataHtml("Operação não permitida!",
                    "Selecione um registro antes!", "Verificar e refazer a operação!"));
        }
try {

    dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
    jdbc = dwfEntityFacade.getJdbcWrapper();
    jdbc.openSession();



    for (Registro registro : contexto.getLinhas()) {



        RelatoriosHelper relatoriosHelper = new RelatoriosHelper();


        BigDecimal numCotacao = (BigDecimal) registro.getCampo("NUCHAVE");
        String tabela = (String) registro.getCampo("TABELA");

        if (!"TGFCOT".equals(tabela)) {

            throw new Exception(FormatExceptionHTMLHelper.formataHtml("Operação não permitida!",
                    "Não é uma cotação", "Verifique a operação pois a impressão não é do tipo cotação!"));

        }

        List<AgendamentoRelatorioHelper.ParametroRelatorio> parametros = new
                ArrayList<>();


        parametros.add(new
                AgendamentoRelatorioHelper.ParametroRelatorio("PK_NUMCOTACAO",
                BigDecimal.class.getName(), numCotacao)

        );

        parametros.add(new AgendamentoRelatorioHelper.ParametroRelatorio("desconsiderar_nao_resp",
                String.class.getName(), "S"));



//        Map<String, Object> parametros = new HashMap<>();
//        parametros.put("PK_NUMCOTACAO", numCotacao);
//        parametros.put("desconsiderar_nao_resp", "S");


        String linkRelatorio = "";
        linkRelatorio = relatoriosHelper.linkRelatorio("impressaoRelatorios" + numCotacao,
                BigDecimal.valueOf(203),
                contexto.getUsuarioLogado(), parametros, dwfEntityFacade,helperLog,showLog);



       //relatoriosHelper.imprimir(parametros,BigDecimal.valueOf(203), jdbc,dwfEntityFacade);



//        contexto.setMensagemRetorno("Impressão realizada com sucesso!");

        contexto.setMensagemRetorno(linkRelatorio);
    }


} catch (Exception e) {
    throw new RuntimeException(e);
}finally {
    JdbcWrapper.closeSession(jdbc);
}







    }
}
