package br.com.sankhya.jaco.cotacao.dao;

import br.com.sankhya.jaco.integracao.helper.DynamicCrudWraperHelper;
import br.com.sankhya.jaco.integracao.helper.DynamicFinderWrapperHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;

import br.com.sankhya.jaco.integracao.helper.SendEmailHelper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.*;

public class GeraEmailCotacaoDao {


    public void geraEmailCotacaoSemAnexo(BigDecimal numCotacao,
                                         String email,
                                         String infoComplementares,
                                         HelperLog helperLog,
                                         boolean showDebug) throws Exception {


        try {

            helperLog.debug(showDebug, "Enviando E-mail sem Anexo");

            SendEmailHelper sendEmail = new SendEmailHelper(new BigDecimal(4), // SMTP diferente
                    new BigDecimal(2), // Máximo de tentativas
                    new BigDecimal(1) // Código de conexão
            );


            String template = sendEmail.loadTemplate("br/com/sankhya/jaco/templates/cotacao_sem_mapa.html");
            helperLog.info(showDebug, "Carregou template");


            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("NUMERO_COTACAO", numCotacao.toString());
            placeholders.put("DATA_ENVIO", TimeUtils.formataDDMMYYYY(TimeUtils.getNow()));
            placeholders.put("INFO_COMPLEMENTARES", infoComplementares);

            String mensagem = sendEmail.fillTemplate(template, placeholders);

            helperLog.info(showDebug, "Achou template");


            helperLog.info(showDebug, "Enviando E-mail Cotação de Nro . " + numCotacao);

            sendEmail.enviarEmail("Mapa de Cotação Nro. " + numCotacao, email, mensagem, null,
                    "", "cotacao_" + numCotacao + ".pdf");


        } catch (Exception e) {

            helperLog.error(showDebug, "Erro ao enviar e-mail de cotação", e);
            throw new Exception(e);

        }


    }


    public void geraEmailCotacaoComAnexo(BigDecimal numCotacao,
                                         BigDecimal codUsuLog,
                                         String infoComplementares,
                                         Collection<DynamicVO> usuariosLiberadores,
                                         HelperLog helperLog,

                                         boolean showDebug) throws Exception {


        try {

            helperLog.debug(showDebug, "Enviando E-mail mapa de Cotação!");

            SendEmailHelper sendEmail = new SendEmailHelper(new BigDecimal(4), // SMTP diferente
                    new BigDecimal(2), // Máximo de tentativas
                    new BigDecimal(1) // Código de conexão
            );

            BigDecimal numeroRelatorio = BigDecimal.valueOf(203);


            String template = sendEmail.loadTemplate("br/com/sankhya/jaco/templates/cotacao.html");
            helperLog.info(showDebug, "Carregou template");


            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("NUMERO_COTACAO", numCotacao.toString());
            placeholders.put("DATA_ENVIO", TimeUtils.formataDDMMYYYY(TimeUtils.getNow()));
            placeholders.put("INFO_COMPLEMENTARES", infoComplementares);

            String mensagem = sendEmail.fillTemplate(template, placeholders);

            helperLog.info(showDebug, "Achou template");


            for (DynamicVO item : usuariosLiberadores) {

                final String email = item.asString("EMAIL");

                // Exemplo 2: Enviar email com relatório anexado
                List<AgendamentoRelatorioHelper.ParametroRelatorio> parametros = new ArrayList<>();
                parametros.add(new AgendamentoRelatorioHelper.ParametroRelatorio("PK_NUMCOTACAO",
                        BigDecimal.class.getName(), numCotacao));

                parametros.add(new AgendamentoRelatorioHelper.ParametroRelatorio("desconsiderar_nao_resp",
                        String.class.getName(), "S"));
                helperLog.info(showDebug, "Gerando Relatorio");

                byte[] relatorio = sendEmail.gerarRelatorio(numeroRelatorio, codUsuLog, parametros);

                helperLog.info(showDebug, "Enviando E-mail Cotação de Nro . " + numCotacao);

                sendEmail.enviarEmail("Mapa de Cotação Nro. " + numCotacao, email, mensagem, relatorio,
                        "application/pdf", "cotacao_" + numCotacao + ".pdf");


            }

        } catch (Exception e) {

            helperLog.error(showDebug, "Erro ao enviar e-mail de cotação", e);
            throw new Exception(e);

        }


    }


    public DynamicVO responseUserLiberador(BigDecimal codUsuLib) throws Exception {

        try {

            DynamicFinderWrapperHelper finder = new DynamicFinderWrapperHelper.Builder(DynamicEntityNames.USUARIO,
                    DynamicFinderWrapperHelper.FinderOperationType.FIND_BY_PK)
                    .withPrimaryKey(codUsuLib).build();

            DynamicVO usuariosLiberadores = (DynamicVO) finder.execute();

            return usuariosLiberadores;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public Collection<DynamicVO> responseUsersLiberadores() throws Exception {

        try {

            DynamicFinderWrapperHelper finder = new DynamicFinderWrapperHelper.Builder(DynamicEntityNames.USUARIO,
                    DynamicFinderWrapperHelper.FinderOperationType.FIND_COLLECTION)
                    .withQueryCriteria("AD_LIBERACOTACAO = ?", "S").build();

            Collection<DynamicVO> usuariosLiberadores = (Collection<DynamicVO>) finder.execute();

            return usuariosLiberadores;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void insercaoLiberacao(Map<String, Object> params) throws Exception {

        try {

            new DynamicCrudWraperHelper.Builder("LiberacaoLimite", DynamicCrudWraperHelper.OperationType.INSERT)
                    .withFieldsToSet(params)
                    .build()
                    .execute();

        } catch (Exception e) {
            throw new Exception(e);
        }


    }


    public void insereLiberadores(Map<String, Object> params) throws Exception {

        try {

            new DynamicCrudWraperHelper.Builder("AD_TGFCOTAPROVADORES", DynamicCrudWraperHelper.OperationType.INSERT)
                    .withFieldsToSet(params)
                    .build()
                    .execute();

        } catch (Exception e) {
            throw new Exception(e);
        }


    }


    //! Validações

    public boolean consultaEventoDeLiberacaoVinculadoUsuario(BigDecimal numCotacao, HelperLog helperLog, boolean showLog) throws Exception {

        boolean liberacao = true;

        try {

            DynamicFinderWrapperHelper finder = new DynamicFinderWrapperHelper.Builder(DynamicEntityNames.LIBERACAO_LIMITE,
                    DynamicFinderWrapperHelper.FinderOperationType.FIND_COLLECTION)
                    .withQueryCriteria("NUCHAVE = ?", numCotacao).build();

            Collection<DynamicVO> existeLiberacaoVO = (Collection<DynamicVO>) finder.execute();

            if (existeLiberacaoVO == null && existeLiberacaoVO.isEmpty()) {
                liberacao = false;
            }


        } catch (Exception e) {
            helperLog.error(showLog, "Erro ao consultar liberacao", e);
            throw new Exception(e);
        }


        return liberacao;

    }

    public boolean consultaEventoDeLiberacaoVinculadoUsuario(BigDecimal codUsuLib, BigDecimal codEvento, HelperLog helperLog, boolean showLog) throws Exception {

        boolean liberacao = true;

        try {


            DynamicFinderWrapperHelper finderUserLibEvent = new DynamicFinderWrapperHelper.Builder("LimiteLiberacao",
                    DynamicFinderWrapperHelper.FinderOperationType.FIND_COLLECTION)
                    .withQueryCriteria("CODUSU = ? AND EVENTO = ?", codUsuLib, codEvento).build();

            Collection<DynamicVO> userLibEvent = (Collection<DynamicVO>) finderUserLibEvent.execute();

            if (userLibEvent == null || userLibEvent.isEmpty()) {
                liberacao = false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return liberacao;

    }


    public Collection<DynamicVO> consultaEValidaSolicitacaoDeLiberacao(BigDecimal nuCotacao,
                                                                       HelperLog helperLog, boolean showLog) throws Exception {


        try {


            DynamicFinderWrapperHelper finder = new DynamicFinderWrapperHelper.Builder("AD_TGFCOTAPROVADORES",
                    DynamicFinderWrapperHelper.FinderOperationType.FIND_COLLECTION)
                    .withQueryCriteria("NUMCOTACAO = ?", nuCotacao).build();

            Collection<DynamicVO> finderResult = (Collection<DynamicVO>) finder.execute();

            return finderResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }




    public void geraEmailCotacaoLiberada(BigDecimal numCotacao,
                                         String email,
                                         String usuarioLiberador,
                                         String cargo,
                                         HelperLog helperLog,
                                         boolean showDebug) throws Exception {


        try {

            helperLog.debug(showDebug, "Enviando E-mail sem Anexo");

            SendEmailHelper sendEmail = new SendEmailHelper(new BigDecimal(4), // SMTP diferente
                    new BigDecimal(2), // Máximo de tentativas
                    new BigDecimal(1) // Código de conexão
            );


            String template = sendEmail.loadTemplate("br/com/sankhya/jaco/templates/liberacao.html");
            helperLog.info(showDebug, "Carregou template");


            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("NUMERO_COTACAO", numCotacao.toString());
            placeholders.put("NOME_USUARIO", usuarioLiberador);
            placeholders.put("CARGO_USUARIO", cargo);


            String mensagem = sendEmail.fillTemplate(template, placeholders);

            helperLog.info(showDebug, "Achou template");


            helperLog.info(showDebug, "Enviando E-mail Liberação de Cotação de Nro . " + numCotacao);

            sendEmail.enviarEmail("Liberação de Cotação Nro. " + numCotacao, email, mensagem, null,
                    "", null);


        } catch (Exception e) {

            helperLog.error(showDebug, "Erro ao enviar e-mail de cotação", e);
            throw new Exception(e);

        }


    }
}