package br.com.sankhya.jaco.notaCompra.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jaco.integracao.helper.DynamicFinderWrapperHelper;
import br.com.sankhya.jaco.integracao.helper.FormatExceptionHTMLHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jaco.notaCompra.models.CabecalhoNotaModel;
import br.com.sankhya.jaco.notaCompra.models.ItensNotaModel;
import br.com.sankhya.jaco.notaCompra.utils.GeraLinkNotaHelper;
import br.com.sankhya.jaco.notaCompra.utils.GeraNotaFiscalHelper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public class GerarNotaDeCompraViaRequisicao implements AcaoRotinaJava {


    HelperLog helperLog = new HelperLog();
    boolean showLog = true;
    private static final String RESURCE_ID = "br.com.sankhya.com.mov.CentralNotas";

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {

        helperLog.debug(showLog, "Entrando na rotina de geração de Pedido de Compra");


        if (contexto.getLinhas().length == 0) {
            throw new Exception(FormatExceptionHTMLHelper.formataHtml("Operação não permitida!",
                    "Selecione um registro antes!", "Verificar e refazer a operação!"));
        } else {

            for (Registro registro : contexto.getLinhas()) {

                String tipMov = (String) registro.getCampo("TIPMOV");


                if (!"J".equals(tipMov)) {

                    throw new Exception(FormatExceptionHTMLHelper.formataHtml("Operação não permitida!",
                            "Tipo de Movimento diferente de Pedidod e Requisição",
                            "Verificar e refazer a operação!"));

                }
                final BigDecimal nuNota = (BigDecimal) registro.getCampo("NUNOTA");
                final BigDecimal codemp = (BigDecimal) registro.getCampo("CODEMP");

                final BigDecimal codParc = new BigDecimal(contexto.getParam("CODPARC").toString());
                final BigDecimal codTipNeg = new BigDecimal(contexto.getParam("CODTIPNEG").toString());
                final BigDecimal codTipOper = new BigDecimal(contexto.getParam("CODTIPOPER").toString());

                final BigDecimal codNat = new BigDecimal(contexto.getParam("CODNAT").toString());
                final BigDecimal codCenCusto = new BigDecimal(contexto.getParam("CODCENCUSTO").toString());
                final BigDecimal codProj = new BigDecimal(contexto.getParam("CODPROJ").toString());

                helperLog.info(showLog, "Buscando itens do Pedido de Requisição Ref. Nro. Nota : " + nuNota);

                DynamicFinderWrapperHelper finderItens = new DynamicFinderWrapperHelper.Builder(
                        "ItemNota", DynamicFinderWrapperHelper.FinderOperationType.FIND_COLLECTION)
                        .withQueryCriteria("NUNOTA = ?", nuNota)
                        .build();
                Collection<DynamicVO> itensVO = (Collection<DynamicVO>) finderItens.execute();

                ArrayList<ItensNotaModel> itensNota = new ArrayList<ItensNotaModel>();


                for (DynamicVO item : itensVO) {
                    helperLog.info(showLog, "Montando Itens...");

                    itensNota.add(buildItemNotaModel(item));
                }

                helperLog.info(showLog, "Itens encontrados para a Nota " + nuNota);

                helperLog.info(showLog, "Montando Cabeçalho");

                CabecalhoNotaModel cabecalhoNotaModel = buildOutputNoteHeader(
                        codTipOper,
                        codParc, codemp, codNat, codCenCusto, codProj, codTipNeg, nuNota);


                final BigDecimal numeroNota = generateCompleteNote(cabecalhoNotaModel, itensNota);

                helperLog.info(showLog, "Saiu do Metodo de Geracao de Nota");


                if (numeroNota.compareTo(BigDecimal.ZERO) > 0) {
                    helperLog.info(showLog, "Gerando Link da Nota : " + numeroNota);


                    GeraLinkNotaHelper geraLinkNotaHelper = new GeraLinkNotaHelper();

                    final String link = geraLinkNotaHelper.getLinkNota("Clique aqui para abrir o link", "NUNOTA", numeroNota, RESURCE_ID);

                    StringBuilder mensagem = new StringBuilder();

                    mensagem.append("<div style='")
                            .append("background: linear-gradient(135deg, #87CEEB 0%, #ADD8E6 100%);")
                            .append("color: #2c3e50;")
                            .append("padding: 15px;")
                            .append("border-radius: 10px;")
                            .append("border-left: 5px solid #3498db;")
                            .append("font-family: Arial, sans-serif;")
                            .append("box-shadow: 0 4px 6px rgba(0,0,0,0.1);")
                            .append("'>")
                            .append("<h3 style='margin: 0 0 10px 0; color: #2c3e50;'>✅ Operação Concluída</h3>")
                            .append("<p style='margin: 5px 0; font-size: 14px; color: #2c3e50;'>")
                            .append("Nota de <b>Entrada</b> gerada com sucesso!")
                            .append("</p>")
                            .append("<p style='margin: 5px 0; font-size: 16px; color: #2c3e50;'>")
                            .append("<b>Número: </b><span style='background: rgba(52, 152, 219, 0.2); padding: 2px 8px; border-radius: 15px; color: #2c3e50;'>")
                            .append(numeroNota)
                            .append("</span>")
                            .append("</p>")
                            .append("<p style='margin: 10px 0 0 0;'>")
                            .append("<div style='")
                            .append("background: #e74c3c;")
                            .append("padding: 8px 16px;")
                            .append("border-radius: 5px;")
                            .append("font-weight: bold;")
                            .append("display: inline-block;")
                            .append("box-shadow: 0 2px 4px rgba(0,0,0,0.2);")
                            .append("'>")
                            .append("🔗 ")
                            .append(link.replace("<a ", "<a style='color: white; text-decoration: none;' "))
                            .append("</div>")
                            .append("</p>")
                            .append("</div>");

                    contexto.setMensagemRetorno(mensagem.toString());

                } else {

                    contexto.setMensagemRetorno("Nota de Entrada não foi gerada!");
                }


            }


        }

    }


    private BigDecimal generateCompleteNote(CabecalhoNotaModel cabecalho, ArrayList<ItensNotaModel> itens)
            throws Exception {
        helperLog.info(showLog, "Entrou no metodo generateCompleteNote()");
        return GeraNotaFiscalHelper.gerarNotaCompleta(EntityFacadeFactory.getDWFFacade(), cabecalho, itens);
    }


    private CabecalhoNotaModel buildOutputNoteHeader(BigDecimal codTipOper,
                                                     BigDecimal codParc,
                                                     BigDecimal codEmp,
                                                     BigDecimal codNat,
                                                     BigDecimal codCenCusto,
                                                     BigDecimal codProdj,
                                                     BigDecimal codTipNeg,
                                                     BigDecimal numeroRequisicao) {
        return new CabecalhoNotaModel.Builder()
                .codTipOper(codTipOper)
                .tipMov("C").serie(" ")
                .codParc(codParc)
                .codEmp(codEmp)
                .codNat(codNat)
                .codCenCus(codCenCusto)
                .codProj(codProdj)
                .tipoNegociacao(codTipNeg)
                .numeroRequisicao(numeroRequisicao)
                .build();
    }

    private ItensNotaModel buildItemNotaModel(DynamicVO item) {
        return new ItensNotaModel.Builder()
                .vlrunit(item.asBigDecimal("VLRUNIT"))
                .qtdNegociada(item.asBigDecimal("QTDNEG"))
                .codProd(item.asBigDecimal("CODPROD"))
                .codLocal(item.asBigDecimal("CODLOCALORIG"))
                .controle(item.asString("CONTROLE"))
                .codVolume(item.asString("CODVOL"))
                .codemp(item.asBigDecimal("CODEMP")).build();
    }
}
