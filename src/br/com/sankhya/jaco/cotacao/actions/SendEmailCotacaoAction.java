package br.com.sankhya.jaco.cotacao.actions;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.sankhya.jaco.cotacao.dao.GeraEmailCotacaoDao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.integracao.helper.FormatExceptionHTMLHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.vo.DynamicVO;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

public class SendEmailCotacaoAction implements AcaoRotinaJava {

    boolean showDebug = true;

    HelperLog helperLog = new HelperLog();

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {

        helperLog.debug(showDebug, "Iniciando Rotina de Envio de Liberação e E-mail p/ Cotação!");
        try {


            if (contexto.getLinhas().length == 0) {
                throw new Exception(
                        FormatExceptionHTMLHelper
                                .formataHtml("Movimento Proibido", "Selecionar um registro antes"));
            }

            String codUsuLibString = (String) contexto.getParam("CODUSULIB");
            BigDecimal codUsuLib = BigDecimalUtil.valueOf(codUsuLibString);
            BigDecimal codEvento = new BigDecimal(1006);
            GeraEmailCotacaoDao geraEmailCotacao = new GeraEmailCotacaoDao();
            helperLog.info(showDebug, "Iniciando Validações");


            boolean liberacao = geraEmailCotacao
                    .consultaEventoDeLiberacaoVinculadoUsuario(codUsuLib, codEvento, helperLog, showDebug);


            if (!liberacao) {
                throw new Exception(
                        FormatExceptionHTMLHelper
                                .formataHtml("Movimento Proibido",
                                        "Usuário informado não possui permissão para liberar a cotação",
                                        "Verificar o usuário, ou pedir para o responsável vincular o evento \" 1006 - LIBERAÇÃO DE COTAÇÃO\" ao usuário específico!"));
            }


            helperLog.info(showDebug, "Achou evento e usuario para liberação!");


            for (Registro registro : contexto.getLinhas()) {


                String statusCotacao = (String) registro.getCampo("STATUS");
                BigDecimal numCotacao = (BigDecimal) registro.getCampo("NUMCOTACAO");
                BigDecimal codUsuLogado = contexto.getUsuarioLogado();
                BigDecimal codUsuResponsavel = (BigDecimal) registro.getCampo("CODUSURESP");


                if ("F".equals(statusCotacao)) {


                    throw new Exception(FormatExceptionHTMLHelper
                            .formataHtml("Movimento Proibido!", "Cotação já está fechada!",
                                    "Cotação precisa estar aberta para geração!"));


                }

                Collection<DynamicVO> verificaSeExisteLiberadores = geraEmailCotacao
                        .consultaEValidaSolicitacaoDeLiberacao(numCotacao, helperLog, showDebug);


                boolean existeAprovador = true;


                for (DynamicVO validaVO : verificaSeExisteLiberadores) {

                    if (validaVO.asTimestamp("DHAPROVACAO") == null && "DA".equals(validaVO.asString("TIPAPROVADOR"))) {

                        throw new Exception(FormatExceptionHTMLHelper
                                .formataHtml("Movimento Proibido!", "Já existe solicitação de liberação para esta cotação e não está aprovada pela Diretoria Solicitação ",
                                        "Favor Verificar!"));

                    } else if (validaVO.asTimestamp("DHAPROVACAO") != null && "DA".equals(validaVO.asString("TIPAPROVADOR"))) {
                        existeAprovador = false;
                    }

                    if (validaVO.asTimestamp("DHAPROVACAO") == null && "DI".equals(validaVO.asString("TIPAPROVADOR"))) {

                        throw new Exception(FormatExceptionHTMLHelper
                                .formataHtml("Movimento Proibido!", "Já existe solicitação de liberação para esta cotação e não está aprovada pela Diretoria Compras ",
                                        "Favor Verificar!"));

                    } else if (validaVO.asTimestamp("DHAPROVACAO") != null
                            && "DI".equals(validaVO.asString("TIPAPROVADOR"))) {

                        throw new Exception(FormatExceptionHTMLHelper
                                .formataHtml("Movimento Proibido!", "Já existe liberação p/ esta cotação",
                                        "Rotina não pode ser executada!"));

                    }
                }

                Collection<DynamicVO> usersLiberadores = geraEmailCotacao.responseUsersLiberadores();


                boolean userIsLiberador = false;

                for (DynamicVO item : usersLiberadores) {

                    final BigDecimal codUSu = item.asBigDecimal("CODUSU");

                    if (codUSu.equals(codUsuLib)) {
                        userIsLiberador = true;
                        break;
                    }
                }


                if (!userIsLiberador || !existeAprovador) {

                    //?Gera info Liberadores

                    helperLog.info(showDebug, "Inserindo informações na TSILIB p/ o liberador que é um gerente!");

                    Map<String, Object> paramsTsiLib = new HashMap<>();


                    paramsTsiLib.put("NUCHAVE", numCotacao);
                    paramsTsiLib.put("TABELA", "TGFCOT");
                    paramsTsiLib.put("EVENTO", codEvento);
                    paramsTsiLib.put("CODUSUSOLICIT", codUsuResponsavel);
                    paramsTsiLib.put("DHSOLICIT", TimeUtils.getNow());
                    paramsTsiLib.put("VLRLIMITE", BigDecimalUtil.valueOf(1));
                    paramsTsiLib.put("VLRATUAL", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("VLRLIBERADO", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("CODUSULIB", codUsuLib);
                    paramsTsiLib.put("PERCLIMITE", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("SEQUENCIA", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("REPROVADO", "N");
                    paramsTsiLib.put("SUPLEMENTO", "N");
                    paramsTsiLib.put("ANTECIPACAO", "N");
                    paramsTsiLib.put("TRANSF", "N");
                    paramsTsiLib.put("SEQCASCATA", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("NUCLL", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("OBSERVACAO", "Esta é uma liberação enviada pelo modulo de cotação!");

                    geraEmailCotacao.insercaoLiberacao(paramsTsiLib);
                    helperLog.info(showDebug, "Inseriu na TSILIB");

                    //? Insere dados da liberacao

                    helperLog.info(showDebug, "Inserindo informações na AD_TGFCOTAPROVADORES");

                    Map<String, Object> paramsLiberadores = new HashMap<>();

                    paramsLiberadores.put("NUMCOTACAO", numCotacao);
                    paramsLiberadores.put("CODUSULIB", codUsuLib);
                    paramsLiberadores.put("CODUSUSOL", codUsuResponsavel);
                    paramsLiberadores.put("DHSOLICITACAO", TimeUtils.getNow());
                    paramsLiberadores.put("TIPAPROVADOR", "DA");

                    geraEmailCotacao.insereLiberadores(paramsLiberadores);

                    helperLog.info(showDebug, "Inseriu na AD_TGFCOTAPROVADORES");

                    //? Envia e-mail para liberadores
                    helperLog.info(showDebug, "Buscando dados do Liberador");

                    final DynamicVO liberadorVO = geraEmailCotacao.responseUserLiberador(codUsuLib);

                    helperLog.info(showDebug, "Enviando E-mail sem Anexo");

                    final String email = liberadorVO.asString("EMAIL");

                    geraEmailCotacao.geraEmailCotacaoSemAnexo(numCotacao, email,
                            null,
                            helperLog, showDebug);


                } else {

                    //Gera info Alessandra ou Liberadores de Cotação

                    helperLog.info(showDebug, "Enviando E-mail mapa de Cotação p/ o usuário que já é Liberador na tabela AD_LIBERACOTACAO = S");


                    //?Gera info Liberadores

                    helperLog.info(showDebug, "Inserindo informações na TSILIB p/ o liberador que é um gerente!");

                    Map<String, Object> paramsTsiLib = new HashMap<>();


                    paramsTsiLib.put("NUCHAVE", numCotacao);
                    paramsTsiLib.put("TABELA", "TGFCOT");
                    paramsTsiLib.put("EVENTO", new BigDecimal(1007));
                    paramsTsiLib.put("CODUSUSOLICIT", codUsuResponsavel);
                    paramsTsiLib.put("DHSOLICIT", TimeUtils.getNow());
                    paramsTsiLib.put("VLRLIMITE", BigDecimalUtil.valueOf(1));
                    paramsTsiLib.put("VLRATUAL", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("VLRLIBERADO", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("CODUSULIB", codUsuLib);
                    paramsTsiLib.put("PERCLIMITE", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("SEQUENCIA", new BigDecimal(1));
                    paramsTsiLib.put("REPROVADO", "N");
                    paramsTsiLib.put("SUPLEMENTO", "N");
                    paramsTsiLib.put("ANTECIPACAO", "N");
                    paramsTsiLib.put("TRANSF", "N");
                    paramsTsiLib.put("SEQCASCATA", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("NUCLL", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("OBSERVACAO", "Esta é uma liberação enviada pelo modulo de cotação!");

                    geraEmailCotacao.insercaoLiberacao(paramsTsiLib);
                    helperLog.info(showDebug, "Inseriu na TSILIB");

                    //? Insere dados da liberacao

                    helperLog.info(showDebug, "Inserindo informações na AD_TGFCOTAPROVADORES");

                    Map<String, Object> paramsLiberadores = new HashMap<>();

                    paramsLiberadores.put("NUMCOTACAO", numCotacao);
                    paramsLiberadores.put("CODUSULIB", codUsuLib);
                    paramsLiberadores.put("CODUSUSOL", codUsuResponsavel);
                    paramsLiberadores.put("DHSOLICITACAO", TimeUtils.getNow());
                    paramsLiberadores.put("TIPAPROVADOR", "DI");

                    geraEmailCotacao.insereLiberadores(paramsLiberadores);

                    helperLog.info(showDebug, "Inseriu na AD_TGFCOTAPROVADORES");


                    geraEmailCotacao.geraEmailCotacaoComAnexo(numCotacao, codUsuLogado, null, usersLiberadores
                            , helperLog, showDebug);


                }


                contexto.setMensagemRetorno("E-mail enviado com sucesso!");


            }


        } catch (Exception e) {
            throw new Exception(e);
        }


    }

}
