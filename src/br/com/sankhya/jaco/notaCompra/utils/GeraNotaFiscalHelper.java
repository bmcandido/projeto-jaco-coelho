package br.com.sankhya.jaco.notaCompra.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;

import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.SPBeanUtils;
import org.jdom.Element;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;
import com.sankhya.util.XMLUtils;
import br.com.sankhya.ws.ServiceContext;
import br.com.sankhya.jaco.notaCompra.models.CabecalhoNotaModel;
import br.com.sankhya.jaco.notaCompra.models.ItensNotaModel;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.dao.EntityPropertyDescriptor;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.modelcore.PlatformService;
import br.com.sankhya.modelcore.PlatformServiceFactory;
import br.com.sankhya.modelcore.comercial.AtributosRegras;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.BarramentoRegra.DadosBarramento;
import br.com.sankhya.modelcore.comercial.ClientEvent;
import br.com.sankhya.modelcore.comercial.LiberacaoSolicitada;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.comercial.util.LoteAutomaticoHelper;
import br.com.sankhya.modelcore.util.ListenerParameters;


import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Classe helper para geração de notas fiscais com as seguintes opções:
 * 1.Geração completa (cabeçalho + itens) 2.Adição de itens a uma nota existente
 */
public class GeraNotaFiscalHelper {


    private static final SimpleDateFormat ddMMyyyySkw = new SimpleDateFormat("dd/MM/yyyy");


    // ------------------------- MÉTODOS PRINCIPAIS ------------------------- //

    /**
     * Gera uma nota fiscal completa (cabeçalho + itens)
     *
     * @param dwfEntityFacade EntityFacade do sistema
     * @param cabecalho       Modelo com dados do cabeçalho
     * @param itens           Coleção de itens da nota
     * @return Número da nota gerada
     * @throws Exception Se ocorrer erro na geração
     */
    public static BigDecimal gerarNotaCompleta(EntityFacade dwfEntityFacade, CabecalhoNotaModel cabecalho,
                                               Collection<ItensNotaModel> itens) throws Exception {
        return gerarNotaCompleta(dwfEntityFacade, cabecalho, itens, null, false);
    }

    /**
     * Gera uma nota fiscal completa com opção de usar número existente
     *
     * @param dwfEntityFacade      EntityFacade do sistema
     * @param cabecalho            Modelo com dados do cabeçalho
     * @param itens                Coleção de itens da nota
     * @param numNotaExistente     Número de nota existente (opcional)
     * @param usarNumNotaExistente Se true, usa o número de nota existente
     * @return Número da nota gerada
     * @throws Exception Se ocorrer erro na geração
     */
    public static BigDecimal gerarNotaCompleta(EntityFacade dwfEntityFacade, CabecalhoNotaModel cabecalho,
                                               Collection<ItensNotaModel> itens, BigDecimal numNotaExistente, boolean usarNumNotaExistente)
            throws Exception {
        // Validações iniciais
        validarParametrosNotaCompleta(dwfEntityFacade, cabecalho, itens);

        // 1. Gera o cabeçalho da nota
        BigDecimal nunota = gerarCabecalhoNota(dwfEntityFacade, cabecalho, numNotaExistente, usarNumNotaExistente);

        // 2. Adiciona os itens
        adicionarItensNota(dwfEntityFacade, itens, nunota);

        return nunota;
    }

    /**
     * Gera o cabeçalho da nota com opção de usar número existente
     *
     * @param dwfEntityFacade      EntityFacade do sistema
     * @param cabecalho            Modelo com dados do cabeçalho
     * @param numNotaExistente     Número de nota existente (opcional)
     * @param usarNumNotaExistente Se true, usa o número de nota existente
     * @return Número da nota gerada
     * @throws Exception Se ocorrer erro na geração
     */
    public static BigDecimal gerarCabecalhoNota(EntityFacade dwfEntityFacade,
                                                CabecalhoNotaModel cabecalho,
                                                BigDecimal numNotaExistente,
                                                boolean usarNumNotaExistente) throws Exception {

        BigDecimal nunota = BigDecimalUtil.ZERO_VALUE;
        try {
            ServiceContext ctx = ServiceContext.getCurrent();

            if (ctx == null) {

                //? Utilizado para o contexto de ações agendadas

                ctx = new ServiceContext(null);


                ctx.setAutentication(AuthenticationInfo.getCurrent());
                ctx.makeCurrent();
                SPBeanUtils.setupContext(ctx);

            }

            validarParametrosCabecalho(dwfEntityFacade, cabecalho);


            Element elemCabecalho = criarElementoCabecalho(cabecalho, numNotaExistente, usarNumNotaExistente);
//        // Log do XML gerado
//        XMLOutputter out = new XMLOutputter();
//        out.setFormat(Format.getPrettyFormat());
//        helperLog.info(true, "===== XML DO CABEÇALHO GERADO =====");
//        helperLog.info(true, out.outputString(elemCabecalho));
//
//        helperLog.info(true, "Configurando CACHelper e inserindo cabeçalho com incluirAlterarCabecalho");

            // Definição obrigatória para uso dos serviços centrais (evita NullPointerException)
            JapeSessionContext.putProperty(ListenerParameters.CENTRAIS, Boolean.TRUE);

            CACHelper cacHelper = new CACHelper();

            // CHAMADA PRINCIPAL - Pode lançar NullPointer
            BarramentoRegra barra = cacHelper.incluirAlterarCabecalho(ctx, elemCabecalho);


            // Recupera o número da nota
            nunota = obterNumeroNota(barra, numNotaExistente, usarNumNotaExistente);

            if (nunota == null) {
                throw new Exception("Falha ao gerar cabeçalho da nota - NUNOTA não retornado");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return nunota;
    }


    /**
     * Adiciona itens a uma nota fiscal existente
     *
     * @param dwfEntityFacade EntityFacade do sistema
     * @param itens           Coleção de itens a adicionar
     * @param nunota          Número da nota existente
     * @throws Exception Se ocorrer erro na adição
     */
    public static void adicionarItensNota(EntityFacade dwfEntityFacade, Collection<ItensNotaModel> itens,
                                          BigDecimal nunota) throws Exception {
        // Validações
        validarParametrosItens(dwfEntityFacade, itens, nunota);

        // Configura o CACHelper
        CACHelper cacHelper = new CACHelper();
        JapeSessionContext.putProperty(ListenerParameters.CENTRAIS, Boolean.TRUE);

        // Processa os itens
        processarItens(dwfEntityFacade, cacHelper, itens, nunota);
    }

/**
 * Adiciona itens a uma nota existente com opção de confirmação
 *
 * @param dwfEntityFacade EntityFacade do sistema
 * @param itens           Coleção de itens a adicionar
 * @param nunota          Número da nota existente
 * @param confirmarNota   Se true, confirma a nota após adicionar itens
 * @throws Exception Se ocorrer erro na adição
 */

// ------------------------- MÉTODOS AUXILIARES ------------------------- //

    /**
     * Cria o elemento XML do cabeçalho da nota
     */
    private static Element criarElementoCabecalho(CabecalhoNotaModel cabecalho, BigDecimal numNotaExistente,
                                                  boolean usarNumNotaExistente) {
        Element elemCabecalho = new Element("Cabecalho");

        System.out.println("cabecalho nota " + cabecalho.toString());

        // Número da nota (novo ou existente)
        if (usarNumNotaExistente && numNotaExistente != null) {
            XMLUtils.addContentElement(elemCabecalho, "NUNOTA", numNotaExistente.toString());
        } else {
            XMLUtils.addContentElement(elemCabecalho, "NUNOTA", "");
        }


        // Demais campos do cabeçalho
        XMLUtils.addContentElement(elemCabecalho, "CODEMP", cabecalho.getCodEmp());
        XMLUtils.addContentElement(elemCabecalho, "CODPARC", cabecalho.getCodParc());
        XMLUtils.addContentElement(elemCabecalho, "CODTIPOPER", cabecalho.getCodTipOper());
        XMLUtils.addContentElement(elemCabecalho, "TIPMOV", cabecalho.getTipMov());
        XMLUtils.addContentElement(elemCabecalho, "CODTIPVENDA", cabecalho.getTipoNegociacao());
        XMLUtils.addContentElement(elemCabecalho, "DTNEG", ddMMyyyySkw.format(TimeUtils.getNow()));
        XMLUtils.addContentElement(elemCabecalho, "CODCENCUS", cabecalho.getCodCenCus());
        XMLUtils.addContentElement(elemCabecalho, "CODNAT", cabecalho.getCodNat());
        XMLUtils.addContentElement(elemCabecalho, "CODPROJ", cabecalho.getCodProj());
        XMLUtils.addContentElement(elemCabecalho, "OBSERVACAO", cabecalho.getObservacao());
        XMLUtils.addContentElement(elemCabecalho, "SERIENOTA", cabecalho.getSerie());
        XMLUtils.addContentElement(elemCabecalho, "NUMNOTA", cabecalho.getNumNota());
        XMLUtils.addContentElement(elemCabecalho, "CHAVENFE", cabecalho.getchaveNfe());
        XMLUtils.addContentElement(elemCabecalho, "AD_NROSOLICITACAO", cabecalho.getnumeroRequisicao());


        return elemCabecalho;
    }

    /**
     * Cria o elemento XML de um item da nota
     */
    private static Element criarElementoItem(ItensNotaModel item, BigDecimal nunota) {
        Element itemElem = new Element("item");


        // Calcula o valor total do item
        BigDecimal vlrTotal = null;
        if (item.getVlrunit() != null) {
            vlrTotal = BigDecimalUtil.getRounded(item.getVlrunit().multiply(item.getQtdNegociada()), 2);

        }

        // Preenche os campos do item
        XMLUtils.addContentElement(itemElem, "NUNOTA", nunota);
        XMLUtils.addContentElement(itemElem, "SEQUENCIA", item.getSequencia() != null ? item.getSequencia() : "");
        XMLUtils.addContentElement(itemElem, "CODPROD", item.getCodProd());
        XMLUtils.addContentElement(itemElem, "CODVOL", item.getCodVolume());
        XMLUtils.addContentElement(itemElem, "CONTROLE", item.getControle());
        XMLUtils.addContentElement(itemElem, "QTDNEG", item.getQtdNegociada());
        XMLUtils.addContentElement(itemElem, "CODLOCALORIG", item.getCodLocal());
        XMLUtils.addContentElement(itemElem, "PERCDESC", item.getPercentualDesconto());
        XMLUtils.addContentElement(itemElem, "VLRDESC", item.getVlrDesconto());
        XMLUtils.addContentElement(itemElem, "VLRUNIT", item.getVlrunit());
        XMLUtils.addContentElement(itemElem, "VLRTOT", vlrTotal);

        System.out.println("==== XML DE ITENS ANTES DA INCLUSAO ====");
        System.out.println("XML gerado para nota para os itens");
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        System.out.println(out.outputString(itemElem));

        return itemElem;
    }

    /**
     * Processa a coleção de itens da nota
     */
    private static void processarItens(EntityFacade dwfEntityFacade, CACHelper cacHelper,
                                       Collection<ItensNotaModel> itens, BigDecimal nunota) throws Exception {
        // Configuração inicial
        cacHelper.addIncluirAlterarListener(new LoteAutomaticoHelper());
        JapeSession.putProperty(AtributosRegras.INC_UPD_ITEM_CENTRAL, Boolean.TRUE);

        // Cria o elemento principal de itens
        Element itensElem = new Element("itens");
        itensElem.setAttribute("ATUALIZACAO_ONLINE", "false");

        // Adiciona cada item ao XML
        for (ItensNotaModel item : itens) {
            itensElem.addContent(criarElementoItem(item, nunota));
        }

        // Executa o serviço de inclusão de itens
        DadosBarramento dadosBarramento = cacHelper.incluirAlterarItem(nunota, ServiceContext.getCurrent(), itensElem,
                false);

        // Verifica se houve erros no processamento
        verificarErros(dadosBarramento);
    }

    /**
     * Obtém o número da nota gerada ou usa o existente
     */
    private static BigDecimal obterNumeroNota(BarramentoRegra barra, BigDecimal numNotaExistente,
                                              boolean usarNumNotaExistente) {
        if (usarNumNotaExistente) {
            return numNotaExistente;
        }

        // Obtém o número da nota gerada pelo sistema
        EntityPropertyDescriptor[] fds = barra.getState().getDao().getSQLProvider().getPkObjectUID()
                .getFieldDescriptors();
        Collection<EntityPrimaryKey> pksEnvolvidas = barra.getDadosBarramento().getPksEnvolvidas();

        if (pksEnvolvidas == null || pksEnvolvidas.isEmpty()) {
            return null;
        }

        EntityPrimaryKey cabKey = pksEnvolvidas.iterator().next();

        for (int i = 0; i < fds.length; i++) {
            EntityPropertyDescriptor cabEntity = fds[i];
            if ("NUNOTA".equals(cabEntity.getField().getName())) {
                return new BigDecimal(cabKey.getValues()[i].toString());
            }
        }
        return null;
    }

    /**
     * Confirma a nota fiscal no sistema
     */
    public static void confirmarNota(BigDecimal nunota) throws Exception {
//		BarramentoRegra barramentoConfirmacao = BarramentoRegra.build(CentralFaturamento.class,
//				"regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
//		JapeSession.putProperty("ignorar.liberacao.alcada", Boolean.FALSE);
//		barramentoConfirmacao.setValidarSilencioso(true);
//		ConfirmacaoNotaHelper.confirmarNota(nunota, barramentoConfirmacao);

        // JapeSession.SessionHandle hnd = null;
//
//
//
//            hnd = JapeSession.open();
//            hnd.setCanTimeout(false);
//
//            hnd.execWithTX(new JapeSession.TXBlock() {
//                public void doWithTx() throws Exception {
//

        PlatformService confirmaNotaService = PlatformServiceFactory.getInstance()
                .lookupService("@core:confirmacao.nota.service");

        confirmaNotaService.set("NUNOTA", nunota);
        confirmaNotaService.set("CODUSUAUTHINFO", BigDecimal.ZERO);

        confirmaNotaService.execute();

        // }
//            });
    }

// ------------------------- VALIDAÇÕES ------------------------- //

    private static void validarParametrosNotaCompleta(EntityFacade dwfEntityFacade, CabecalhoNotaModel cabecalho,
                                                      Collection<ItensNotaModel> itens) throws Exception {
        if (dwfEntityFacade == null) {
            throw new Exception("EntityFacade não pode ser nulo");
        }
        if (cabecalho == null) {
            throw new Exception("Cabeçalho não pode ser nulo");
        }
        if (itens == null || itens.isEmpty()) {
            throw new Exception("Lista de itens não pode ser nula ou vazia");
        }
    }

    private static void validarParametrosCabecalho(EntityFacade dwfEntityFacade, CabecalhoNotaModel cabecalho)
            throws Exception {
        if (dwfEntityFacade == null) {
            throw new Exception("EntityFacade não pode ser nulo");
        }
        if (cabecalho == null) {
            throw new Exception("Cabeçalho não pode ser nulo");
        }
    }

    private static void validarParametrosItens(EntityFacade dwfEntityFacade, Collection<ItensNotaModel> itens,
                                               BigDecimal nunota) throws Exception {
        if (dwfEntityFacade == null) {
            throw new Exception("EntityFacade não pode ser nulo");
        }
        if (itens == null || itens.isEmpty()) {
            throw new Exception("Lista de itens não pode ser nula ou vazia");
        }
        if (nunota == null) {
            throw new Exception("Número da nota (NUNOTA) não pode ser nulo");
        }
    }

    /**
     * Verifica erros retornados pelo barramento
     */
    private static void verificarErros(DadosBarramento dadosBarramento) throws Exception {
        if (dadosBarramento == null) {
            throw new Exception("Dados de barramento não retornados");
        }

        // Verifica erros comuns
        Collection<Exception> erros = dadosBarramento.getErros();
        if (erros != null && !erros.isEmpty()) {
            throw erros.iterator().next();
        }

        // Verifica liberações solicitadas
        Collection<LiberacaoSolicitada> liberacoes = dadosBarramento.getLiberacoesSolicitadas();
        if (liberacoes != null && !liberacoes.isEmpty()) {
            throw new Exception(
                    "Liberação solicitada para o movimento: " + liberacoes.iterator().next().getDescricao());
        }

        // Verifica eventos de cliente
        Collection<ClientEvent> clientEvents = dadosBarramento.getClientEvents();
        if (clientEvents != null && !clientEvents.isEmpty()) {
            throw new Exception("Evento solicitado: " + clientEvents.iterator().next().getEventID());
        }
    }
}