package br.com.sankhya.jaco.cotacao.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jaco.cotacao.dao.GeraEmailCotacaoDao;
import br.com.sankhya.jaco.integracao.helper.DynamicFinderWrapperHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AvisaResponsaveisCotacaoEvent implements EventoProgramavelJava {

    HelperLog helperLog = new HelperLog();
    boolean showDebug = true;

    @Override
    public void beforeUpdate(PersistenceEvent context) throws Exception {


        try {

            helperLog.info(true, "Entrou no update para o beforeUpdate dentro da tabela TSILIB");
            EntityVO entityVO = context.getVo();
            DynamicVO liberacaoLimitesVO = (DynamicVO) entityVO;

            final Timestamp dataLiberacaoLimites = liberacaoLimitesVO.asTimestamp("DHLIB");
            final String nomeTabela = liberacaoLimitesVO.asString("TABELA");
            final BigDecimal numCotacao = liberacaoLimitesVO.asBigDecimal("NUCHAVE");
            final BigDecimal codUsuSol = liberacaoLimitesVO.asBigDecimal("CODUSUSOLICIT");
            final BigDecimal codUsuLib = liberacaoLimitesVO.asBigDecimal("CODUSULIB");
            final BigDecimal evento = liberacaoLimitesVO.asBigDecimal("EVENTO");


            if (dataLiberacaoLimites != null && nomeTabela.equals("TGFCOT")) {
                GeraEmailCotacaoDao geraEmailCotacaoDao = new GeraEmailCotacaoDao();

                final Collection<DynamicVO> usuariosLiberadores = geraEmailCotacaoDao.responseUsersLiberadores();

                helperLog.info(showDebug, "Enviando cotação com mapa em anexo");
                geraEmailCotacaoDao.geraEmailCotacaoComAnexo(numCotacao, codUsuSol,
                        null, usuariosLiberadores, helperLog, showDebug);

                helperLog.info(showDebug, "Faz o update para validar a aprovação!");


                new DynamicFinderWrapperHelper.Builder(
                        null, DynamicFinderWrapperHelper.FinderOperationType.NATIVE_UPDATE)
                        .withNativeSql(
                                "UPDATE AD_TGFCOTAPROVADORES C SET C.DHAPROVACAO = :DHAPROVACAO, C.CODUSULIB = :CODUSULIB  WHERE C.NUMCOTACAO = :NUMCOTACAO AND (C.CODUSULIB = :CODUSULIB  OR C.CODUSULIB = 0 )")
                        .withNamedParameter("DHAPROVACAO", TimeUtils.getNow())
                        .withNamedParameter("NUMCOTACAO", numCotacao)
                        .withNamedParameter("CODUSULIB", codUsuLib)
                        .build().execute();


                GeraEmailCotacaoDao geraEmailCotacao = new GeraEmailCotacaoDao();


                if (evento.compareTo(new BigDecimal(1006)) == 0 && dataLiberacaoLimites != null) {

                    helperLog.info(showDebug, "Envia o e-mail avisando que a liberação do gerente foi feita!");


                    helperLog.info(showDebug, "Faz o insert do evento 1007 p/ aprovação do gerente");

                    //?Gera info Liberadores

                    helperLog.info(showDebug, "Inserindo informações na TSILIB p/ o liberador que é um gerente!");

                    Map<String, Object> paramsTsiLib = new HashMap<>();


                    paramsTsiLib.put("NUCHAVE", numCotacao);
                    paramsTsiLib.put("TABELA", "TGFCOT");
                    paramsTsiLib.put("EVENTO", new BigDecimal(1007));
                    paramsTsiLib.put("CODUSUSOLICIT", codUsuSol);
                    paramsTsiLib.put("DHSOLICIT", TimeUtils.getNow());
                    paramsTsiLib.put("VLRLIMITE", BigDecimalUtil.valueOf(1));
                    paramsTsiLib.put("VLRATUAL", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("VLRLIBERADO", BigDecimalUtil.ZERO_VALUE);
                    paramsTsiLib.put("CODUSULIB", new BigDecimal(0));
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
                    paramsLiberadores.put("CODUSULIB", new BigDecimal(0));
                    paramsLiberadores.put("CODUSUSOL", codUsuSol);
                    paramsLiberadores.put("DHSOLICITACAO", TimeUtils.getNow());
                    paramsLiberadores.put("TIPAPROVADOR", "DI");

                    geraEmailCotacao.insereLiberadores(paramsLiberadores);

                    helperLog.info(showDebug, "Inseriu na AD_TGFCOTAPROVADORES");


                }


                if (evento.compareTo(new BigDecimal(1007)) == 0) {


                    new DynamicFinderWrapperHelper.Builder(
                            null, DynamicFinderWrapperHelper.FinderOperationType.NATIVE_UPDATE)
                            .withNativeSql(
                                    "UPDATE TGFITC C\n" +
                                            "   SET C.SITUACAO      = CASE \n" +
                                            "                            WHEN C.MELHOR = 'S'\n" +
                                            "                             AND C.CODPARC > 0 \n" +
                                            "                             AND C.CABECALHO = 'N'\n" +
                                            "                            THEN 'A' \n" +
                                            "                            ELSE C.SITUACAO\n" +
                                            "                         END,\n" +
                                            "       C.STATUSPRODCOT = CASE \n" +
                                            "                            WHEN C.CABECALHO = 'S'\n" +
                                            "                            THEN 'A' \n" +
                                            "                            ELSE C.STATUSPRODCOT\n" +
                                            "                         END\n" +
                                            " WHERE C.NUMCOTACAO = :NUMCOTACAO")

                            .withNamedParameter("NUMCOTACAO", numCotacao)
                            .build().execute();


                    new DynamicFinderWrapperHelper.Builder(
                            null, DynamicFinderWrapperHelper.FinderOperationType.NATIVE_UPDATE)
                            .withNativeSql(
                                    "UPDATE TGFCOT T SET T.SITUACAO = 'P' WHERE T.NUMCOTACAO = :NUMCOTACAO")

                            .withNamedParameter("NUMCOTACAO", numCotacao)
                            .build().execute();


                }

                try {
                    ResultSet resultSetInfo = buscaInfoLiberacao(numCotacao, evento);

                    if (resultSetInfo != null && resultSetInfo.next()) {

                        String emailSol = resultSetInfo.getString("emailsol");
                        String liberador = resultSetInfo.getString("liberador");
                        String cargo = "";

                        if (evento.compareTo(new BigDecimal(1007)) == 0) {
                            cargo = "Diretoria Compras";
                        } else {
                            cargo = "Diretoria Solicitação";
                        }

                        geraEmailCotacao.geraEmailCotacaoLiberada(numCotacao, emailSol, liberador, cargo, helperLog, showDebug);

                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }

        } catch (Exception e) {
            helperLog.error(showDebug, "Erro ao fazer update para validar a aprovação!", e);
            throw new Exception(e);
        }


    }


    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {


    }


    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

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


    private ResultSet buscaInfoLiberacao(BigDecimal numCotacao, BigDecimal evento) throws Exception {
        helperLog.info(showDebug, "Iniciado");

        try {

            DynamicFinderWrapperHelper.Builder builder = new DynamicFinderWrapperHelper.Builder(null,
                    DynamicFinderWrapperHelper.FinderOperationType.NATIVE_SQL)
                    .withNativeSql(queryInfoUsuarios(numCotacao, evento));

            DynamicFinderWrapperHelper.NativeQueryResult queryResult = (DynamicFinderWrapperHelper.NativeQueryResult) builder
                    .build().execute();

            ResultSet resultSet = queryResult.getResultSet();

            return resultSet;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String queryInfoUsuarios(BigDecimal numCotacao, BigDecimal evento) {

        return "select ususol.email emailsol,\n" +
                "       NVL(NVL(parlib.nomeparc, USULIB.NOMEUSUCPLT), usulib.Nomeusu) liberador\n" +
                "  from tsilib b\n" +
                " inner join tsiusu usulib\n" +
                "    on usulib.codusu = b.codusulib\n" +
                " inner join tsiusu ususol\n" +
                "    on ususol.codusu = b.codususolicit\n" +
                "  LEFT join tgfpar parLib\n" +
                "    on parlib.codparc = usulib.codparc\n" +
                " where b.nuchave = " + numCotacao.toString() + "\n" +
                "   and b.evento = " + evento.toString() + "\n" +
                "   and b.tabela = 'TGFCOT'";

    }
}
