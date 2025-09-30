package br.com.sankhya.jaco.integracao.helper;

import br.com.sankhya.modelcore.PlatformService;
import br.com.sankhya.modelcore.PlatformServiceFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;

public class FaturaNotaHelper {


    public void faturanota(Collection<BigDecimal> notasParaFaturamento,
                           BigDecimal nunotaAfaturar,
                           BigDecimal codTipOper,
                           String serieNota,
                           HelperLog helperLog,
                           boolean showLog) throws Exception {

        try {


            PlatformService faturaNotaService = PlatformServiceFactory.getInstance().lookupService("@core:faturamento.service");


            faturaNotaService.set("NUNOTA", nunotaAfaturar);
            faturaNotaService.set("NUNOTACOLLECTION", notasParaFaturamento);
            faturaNotaService.set("CODTIPOPER", codTipOper);
            faturaNotaService.set("DTENTRADASAIDA", new Timestamp(System.currentTimeMillis()));
            faturaNotaService.set("HRENTRADASAIDA", new Timestamp(System.currentTimeMillis()));
            faturaNotaService.set("DTFATURAMENTO", new Timestamp(System.currentTimeMillis()));
            faturaNotaService.set("SERIE", serieNota);
            faturaNotaService.set("CODUSUAUTHINFO", BigDecimal.ZERO);

            faturaNotaService.execute();
        } catch (Exception e) {

            helperLog.error(showLog, "Erro no objeto faturanota()", e);
            throw e;

        }
    }


}
