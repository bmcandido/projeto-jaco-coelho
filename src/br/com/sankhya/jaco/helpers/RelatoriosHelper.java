package br.com.sankhya.jaco.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sankhya.util.SessionFile;

import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.util.print.PrintManager;
import br.com.sankhya.modelcore.comercial.util.print.converter.PrintConversionService;
import br.com.sankhya.modelcore.comercial.util.print.model.PrintInfo;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.Report;
import br.com.sankhya.modelcore.util.ReportManager;
import br.com.sankhya.sps.enumeration.DocTaste;
import br.com.sankhya.sps.enumeration.DocType;
import br.com.sankhya.ws.ServiceContext;
import net.sf.jasperreports.engine.JasperPrint;

public class RelatoriosHelper {




    //Pelo Agendador

    public String linkRelatorio(String relatorioName, BigDecimal codRelatorio, BigDecimal codUsuarioLogado,
                                List<AgendamentoRelatorioHelper.ParametroRelatorio> parametros, EntityFacade dwfFacade, HelperLog helperLog, boolean showLog) throws Exception {
        String responseLinkRelatorio = "";

        try {


            byte[] pdfBytes = null;

            helperLog.info(showLog, "Gerando Relatorio Bytes");
            parametros.forEach(parametroRelatorio -> helperLog.info(showLog, parametroRelatorio.toString()));
            pdfBytes = gerarRelatorio(codRelatorio, codUsuarioLogado, parametros, dwfFacade);

            helperLog.info(showLog, "Gerou Relatorio Bytes");


            SessionFile sessionFile = SessionFile.createSessionFile(relatorioName, relatorioName, pdfBytes);


            ServiceContext.getCurrent().putHttpSessionAttribute(relatorioName, sessionFile);


            responseLinkRelatorio = "<script>"
                    + "var newWindow = window.open('/mge/visualizadorArquivos.mge?chaveArquivo=" + relatorioName + "', " +
                    "'relatorio', 'width=1200,height=700,scrollbars=yes,resizable=yes');"
                    + "if (newWindow) { newWindow.focus(); }"
                    + "</script>" + "Arquivo Baixado com sucesso!";;

        } catch (Exception e) {
            throw new MGEModelException(e);
        }

        return responseLinkRelatorio;
    }

    public byte[] gerarRelatorio(BigDecimal codRelatorio, BigDecimal codUsuarioLogado,
                                 List<AgendamentoRelatorioHelper.ParametroRelatorio> parametros, EntityFacade dwfFacade)
            throws MGEModelException {
        try {
            if (parametros == null) {
                parametros = new ArrayList<>();
            }
            return AgendamentoRelatorioHelper.getPrintableReport(codRelatorio, parametros, codUsuarioLogado, dwfFacade);
        } catch (Exception e) {
            throw new MGEModelException("Erro ao gerar relatório: " + e.getMessage(), e);
        }
    }


    public void imprimir(Map<String, Object> param, BigDecimal codRelatorio, JdbcWrapper jdbc,
                         EntityFacade dwfEntityFacade) throws Exception {

        try {

            Report report = ReportManager.getInstance().getReport(codRelatorio, dwfEntityFacade);


            JasperPrint jasperPrint = report.buildJasperPrint(param, jdbc.getConnection());


            byte[] conteudo = PrintConversionService.getInstance().convert(jasperPrint, byte[].class);
            PrintManager printManager = PrintManager.getInstance();

            String printerName = "?";
            String jobDescription = "IMPRESSAO";
            int copies = 1;

            BigDecimal userId = AuthenticationInfo.getCurrent().getUserID();
            String userName = "SUP";
            BigDecimal codEmp = BigDecimal.ONE;
            String idDocumento = "0";

            PrintInfo printInfo = new PrintInfo(conteudo,
                    DocTaste.JASPER,
                    DocType.RELATORIO,
                    printerName,
                    jobDescription,
                    copies, userId, userName, codEmp, idDocumento);

            printManager.print(printInfo);

        } catch (Exception e) {
            throw new MGEModelException("Erro ao gerar relatório: " + e.getMessage(), e);
        }

    }


}
