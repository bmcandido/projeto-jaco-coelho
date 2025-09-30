package br.com.sankhya.jaco.integracao.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sankhya.util.JsonUtils;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.jaco.integracao.dao.Compromisso;
import br.com.sankhya.jaco.integracao.dao.Contratos;
import br.com.sankhya.jaco.integracao.dao.Parceiro;
import br.com.sankhya.jaco.integracao.dao.Pas;
import br.com.sankhya.jaco.integracao.dao.Pedidos;
import br.com.sankhya.jaco.integracao.dao.Processo;
import br.com.sankhya.jaco.integracao.dao.Recursos;
import br.com.sankhya.jaco.integracao.dao.TimeSheets;
import br.com.sankhya.jaco.integracao.utils.BeautifulJson;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ImportacaoHelper {
	
	HelperLog helperLog = new HelperLog();

	public void importaDados(DynamicVO registroVO) throws Exception {
		


		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

		boolean debug = true;

	helperLog.debug(debug, "Entrando no objeto importaDados");

		try {

			if (registroVO.asString("TIPO") == null) {

			helperLog.info(debug, "Processando integração processos!");

				String json = registroVO.asString("JSON");

				JsonObject jsonObject = JsonUtils.convertStringToJsonObject(json);
				Processo processoDao = new Gson().fromJson(jsonObject, Processo.class);

				FinderWrapper finder = new FinderWrapper("AD_PROCESSOS", "this.IDPROCESSO = ?",
						new Object[] { processoDao.getIdProcesso() });
				EntityVO entityVO = verificicarVO(dwfFacade, finder);

				DynamicVO processoVO = (DynamicVO) entityVO;

				processoVO.setProperty("IDPROCESSO", processoDao.getIdProcesso());
				processoVO.setProperty("STATUS", processoDao.getStatus());
				processoVO.setProperty("NOMEORIGEM", processoDao.getNomeOrigem());
				processoVO.setProperty("NUMJURISDICAO", processoDao.getNumJurisdicao());
				processoVO.setProperty("INSTANCIA", processoDao.getInstancia());
				processoVO.setProperty("NOMEESCRITORIO", processoDao.getNomeEscritorio());
				processoVO.setProperty("NUMEROCONTRATOCLIENTE", processoDao.getNumeroContratoCliente());
				processoVO.setProperty("NOMEESTADO", processoDao.getNomeEstado());
				processoVO.setProperty("NOMEJUIZADO", processoDao.getNomeJuizado());
				processoVO.setProperty("NUMEROPROCESSOANTIGO", processoDao.getNumeroProcessoAntigo());
				processoVO.setProperty("NOMEADVRESPONSAVEL", processoDao.getNomeAdvResponsavel());
				processoVO.setProperty("NOMENATUREZA", processoDao.getNomeNatureza());
				processoVO.setProperty("NOMECIDADE", processoDao.getNomeCidade());
				processoVO.setProperty("PROTOCOLOATUAL", processoDao.getProtocoloAtual());
				processoVO.setProperty("ISFORMULARIODEFESAPENDENTE", processoDao.isFormularioDefesaPendente());
				processoVO.setProperty("NOMEGRUPOCLIENTE", processoDao.getNomeGrupoCliente());
				processoVO.setProperty("NOMECLIENTEPRINCIPAL", processoDao.getNomeClientePrincipal());
				processoVO.setProperty("NPC", processoDao.getNpc());
				processoVO.setProperty("CPFCNPJCLIENTEPRINCIPAL", processoDao.getCpfCnpjClientePrincipal());
				processoVO.setProperty("CPFCNPJGRUPOCLIENTE", processoDao.getCpfCnpjGrupoCliente());
				processoVO.setProperty("NOMEADVERSOPRINCIPAL", processoDao.getNomeAdversoPrincipal());
				processoVO.setProperty("DATACADESCRITORIOSISTEMASEGURA",
						processoDao.getDataCadEscritorioSistemaSeguradora());
				processoVO.setProperty("NOMESISTEMACLIENTE", processoDao.getNomeSistemaCliente());
				processoVO.setProperty("DATADISTRIBUICAO", processoDao.getDataDistribuicao());
				processoVO.setProperty("DATAHORACADASTRO", processoDao.getDataHoraCadastro());
				processoVO.setProperty("DATACITACAO", processoDao.getDataCitacao());
				processoVO.setProperty("VALORCAUSA", processoDao.getValorCausa());
				processoVO.setProperty("VALORPEDIDO", processoDao.getValorPedido());
				processoVO.setProperty("DATAAJUIZAMENTO", processoDao.getDataAjuizamento());
				processoVO.setProperty("PASTACPPRO", processoDao.getPastaCPPRO());
				processoVO.setProperty("NOMESUBGRUPOSEGURADORA", processoDao.getNomeSubGrupoSeguradora());
				processoVO.setProperty("VALORRISCOCALCULOHONORARIOS", processoDao.getValorRiscoCalculoHonorarios());
				processoVO.setProperty("NOMESEGMENTOSEGURADORA", processoDao.getNomeSegmentoSeguradora());
				processoVO.setProperty("ISCLIENTEASSISTENCIAJUDICIARIA", processoDao.isClienteAssistenciaJudiciaria());
				processoVO.setProperty("NUMEROPROCESSORPVPRECATORIO", processoDao.getNumeroProcessoRPVPrecatorio());
				processoVO.setProperty("DATAEXPEDICAORPVPRECATORIO", processoDao.getDataExpedicaoRPVPrecatorio());
				processoVO.setProperty("VALORFIXADOCLIENTE", processoDao.getValorFixadoCliente());
				processoVO.setProperty("APOLICE", processoDao.getApolice());
				processoVO.setProperty("RAMO", processoDao.getRamo());
				processoVO.setProperty("PRODUTO", processoDao.getProduto());
				processoVO.setProperty("NOMETIPOCONTRATACAO", processoDao.getNomeTipoContratacao());
				processoVO.setProperty("NOMECENTROCUSTO", processoDao.getNomeCentrocusto());
				processoVO.setProperty("SINISTROJUDICIAL", processoDao.getSinistroJudicial());
				processoVO.setProperty("STATUSACORDO", processoDao.getStatusAcordo());
				processoVO.setProperty("ISACORDADOCOMAUTOR", processoDao.getAcordadoComAutor());
				processoVO.setProperty("VALORACORDADO", processoDao.getValorAcordado());
				processoVO.setProperty("DATAPAGAMENTOACORDO", processoDao.getDataPagamentoAcordo());
				processoVO.setProperty("DATAASSINATURAMINUTA", processoDao.getDataAssinaturaMinuta());
				processoVO.setProperty("SEGUNDOTITULARCC", processoDao.getSegundoTitularCc());
				processoVO.setProperty("CPFSEGUNDOTITULARCC", processoDao.getCpfSegundoTitularCc());
				processoVO.setProperty("IDCLIENTE", processoDao.getIdCliente());
				processoVO.setProperty("SINISTROADMINISTRATIVO", processoDao.getSinistroAdministrativo());
				processoVO.setProperty("NOMEASSUNTO", processoDao.getNomeAssunto());
				processoVO.setProperty("TIPOACAO", processoDao.getTipoAcao());
				processoVO.setProperty("JURIDICOSEGURADORA", processoDao.getJuridicoSeguradora());
				processoVO.setProperty("PASTAMIGRADA", processoDao.getPastaMigrada());
				processoVO.setProperty("OBJETO", processoDao.getObjeto());
				processoVO.setProperty("OBJETOID", processoDao.getObjetoId());
				processoVO.setProperty("IDCONTRATO", processoDao.getIdContrato());
				processoVO.setProperty("NUMEROCONTRATO", processoDao.getNumerContrato());
				processoVO.setProperty("IDCIDADE", processoDao.getIdCidade());
				processoVO.setProperty("DTHORAENCERRAMENTO", processoDao.getDataHoraEncerramento());
				PersistentLocalEntity pleProcessos = dwfFacade.saveEntity("AD_PROCESSOS", entityVO);
				entityVO = pleProcessos.getValueObject();
				processoVO = (DynamicVO) entityVO;

				List<Compromisso> listCompromisso = processoDao.getCompromissos();
				
				listCompromisso.sort(Comparator.comparing(c -> c.getIdCompromisso()));
				
				for (Compromisso compromissoDao : listCompromisso) {
					
					

					finder = new FinderWrapper("AD_COMPROMISSOS", "this.IDPROCESSO = ? And this.IDCOMPROMISSO = ? ",
							new Object[] { processoVO.getProperty("IDPROCESSO"), compromissoDao.getIdCompromisso() });
					EntityVO compEntityVO = verificicarVO(dwfFacade, finder);
					DynamicVO compromissoVO = (DynamicVO) compEntityVO;

					// Chave M�e
					compromissoVO.setProperty("IDPROCESSO", processoVO.getProperty("IDPROCESSO"));

					compromissoVO.setProperty("IDCOMPROMISSO", compromissoDao.getIdCompromisso());
					compromissoVO.setProperty("DATAHORACOMPROMISSO", compromissoDao.getDataCompromisso());
					compromissoVO.setProperty("DATAHORACONCLUSAO", compromissoDao.getDataHoraConclusao());
					compromissoVO.setProperty("NOMETIPOCOMPROMISSO", compromissoDao.getNomeTipoCompromisso());
					compromissoVO.setProperty("NOMESUBTIPOCOMPROMISSO", compromissoDao.getNomeSubTipoCompromisso());
					compromissoVO.setProperty("NOMERESPONSAVEL", compromissoDao.getNomeResponsavel());
					compromissoVO.setProperty("ISAUTOMATICO", compromissoDao.isAutomatico());
					compromissoVO.setProperty("NOMERESPONSAVELPROTOCOLO", compromissoDao.getNomeResponsavelProtocolo());
					compromissoVO.setProperty("PROTOCOLOATUAL", compromissoDao.getProtocoloAtual());
					compromissoVO.setProperty("STATUS", compromissoDao.getStatus());
					compromissoVO.setProperty("IDSUBTIPOCOMPROMISSO", compromissoDao.getIdSubtipoCompromisso());
					compromissoVO.setProperty("FORMATOAUDIENCIA", compromissoDao.getFormatoAudiencia());

					dwfFacade.saveEntity("AD_COMPROMISSOS", compEntityVO);

				}

				List<TimeSheets> listTimeSheets = processoDao.getTimeSheets();
				for (TimeSheets timeSheetsDao : listTimeSheets) {

					finder = new FinderWrapper("AD_TIMESHEETS", "this.IDPROCESSO = ? And this.IDTIMESHEET = ? ",
							new Object[] { processoVO.getProperty("IDPROCESSO"), timeSheetsDao.getIdTimeSheet() });
					EntityVO timeEntityVO = verificicarVO(dwfFacade, finder);
					DynamicVO timeSheetsVO = (DynamicVO) timeEntityVO;

					// Chave M�e
					timeSheetsVO.setProperty("IDPROCESSO", processoVO.getProperty("IDPROCESSO"));

					timeSheetsVO.setProperty("IDTIMESHEET", timeSheetsDao.getIdTimeSheet());
					timeSheetsVO.setProperty("NOMEATIVIDADETIMESHEET", timeSheetsDao.getNomeAtividadeTimeSheet());
					timeSheetsVO.setProperty("NOMEGRUPOCLIENTE", timeSheetsDao.getNomeGrupoCliente());
					timeSheetsVO.setProperty("OBSERVACAO", timeSheetsDao.getObservacao().toCharArray());
					timeSheetsVO.setProperty("DATAINICIO", timeSheetsDao.getDataInicio());
					timeSheetsVO.setProperty("DATACONCLUSAO", timeSheetsDao.getDataConclusao());
					timeSheetsVO.setProperty("NOMERESPONSAVEL", timeSheetsDao.getNomeResponsavel());
					timeSheetsVO.setProperty("NOMEUSUARIOCADASTRO", timeSheetsDao.getNomeUsuarioCadastro());
					timeSheetsVO.setProperty("DATACADASTRO", timeSheetsDao.getDataCadastro());
					timeSheetsVO.setProperty("TOTALHORAS", timeSheetsDao.getTotalHoras());
					timeSheetsVO.setProperty("STATUS", timeSheetsDao.getStatus());

					dwfFacade.saveEntity("AD_TIMESHEETS", timeEntityVO);

				}

				List<Recursos> listRecursos = processoDao.getRecursos();
				for (Recursos recursosDao : listRecursos) {

					finder = new FinderWrapper("AD_RECURSOS", "this.IDPROCESSO = ? And this.IDRECURSO = ? ",
							new Object[] { processoVO.getProperty("IDPROCESSO"), recursosDao.getIdRecurso() });
					EntityVO recursosEntityVO = verificicarVO(dwfFacade, finder);
					DynamicVO recursosVO = (DynamicVO) recursosEntityVO;

					// Chave M�e
					recursosVO.setProperty("IDPROCESSO", processoVO.getProperty("IDPROCESSO"));

					recursosVO.setProperty("IDRECURSO", recursosDao.getIdRecurso());
					recursosVO.setProperty("PROTOCOLOATUAL", recursosDao.getProtocoloAtual());
					recursosVO.setProperty("NOMEESTADO", recursosDao.getNomeEstado());
					recursosVO.setProperty("NOMEORIGEM", recursosDao.getNomeOrigem());
					recursosVO.setProperty("NOMECIDADE", recursosDao.getNomeCidade());
					recursosVO.setProperty("NOMEJUIZADO", recursosDao.getNomeJuizado());
					recursosVO.setProperty("NOMETIPORECURSO", recursosDao.getNomeTipoRecurso());
					recursosVO.setProperty("NUMJURISDICAO", recursosDao.getNumJurisdicao());
					recursosVO.setProperty("NOMENATUREZA", recursosDao.getNomeNatureza());
					recursosVO.setProperty("INSTANCIA", recursosDao.getInstancia());
					recursosVO.setProperty("DATARECEBIMENTO", recursosDao.getDataRecebimento());

					dwfFacade.saveEntity("AD_RECURSOS", recursosEntityVO);

				}

				List<Pedidos> listPedidos = processoDao.getPedidos();
				for (Pedidos pedidosDao : listPedidos) {

					finder = new FinderWrapper("AD_PEDIDOS", "this.IDPROCESSO = ? And this.IDPEDIDO = ? ",
							new Object[] { processoVO.getProperty("IDPROCESSO"), pedidosDao.getIdPedido() });
					EntityVO pedidosEntityVO = verificicarVO(dwfFacade, finder);
					DynamicVO pedidosVO = (DynamicVO) pedidosEntityVO;

					// Chave M�e
					pedidosVO.setProperty("IDPROCESSO", processoVO.getProperty("IDPROCESSO"));

					pedidosVO.setProperty("IDPEDIDO", pedidosDao.getIdPedido());
					pedidosVO.setProperty("NOMETIPOPEDIDO", pedidosDao.getNomeTipoPedido());
					pedidosVO.setProperty("VALORPEDIDO", pedidosDao.getValorPedido());
					pedidosVO.setProperty("DATAPEDIDO", pedidosDao.getDataPedido());
					pedidosVO.setProperty("NOMECLASSIFICACAOPEDIDO", pedidosDao.getNomeClassificacaoPedido());
					pedidosVO.setProperty("RESULTADO", pedidosDao.getResultadoPA());
					pedidosVO.setProperty("RESULTADOPEDIDOPA", pedidosDao.getResultadoPedidoPA());
					pedidosVO.setProperty("PROBABILIDADEEXITO", pedidosDao.getProbabilidadeExito());
					pedidosVO.setProperty("NOMETESEALEGADAACOLHIDA", pedidosDao.getNomeTeseAlegadaAcolhida());
					pedidosVO.setProperty("DATACADASTRO", pedidosDao.getDataCadastro());
					pedidosVO.setProperty("INSTANCIA", pedidosDao.getInstancia());

					dwfFacade.saveEntity("AD_PEDIDOS", pedidosEntityVO);

				}

				List<Pas> listPas = processoDao.getPas();
				for (Pas pasDao : listPas) {

					finder = new FinderWrapper("AD_PAS", "this.IDPROCESSO = ? And this.IDPA = ? ",
							new Object[] { processoVO.getProperty("IDPROCESSO"), pasDao.getIdPa() });
					EntityVO pasEntityVO = verificicarVO(dwfFacade, finder);
					DynamicVO pasVO = (DynamicVO) pasEntityVO;

					// Chave M�e
					pasVO.setProperty("IDPROCESSO", processoVO.getProperty("IDPROCESSO"));

					pasVO.setProperty("IDPA", pasDao.getIdPa());
					pasVO.setProperty("RESULTADO", pasDao.getResultado());
					pasVO.setProperty("DATAHORAPREENCHIMENTO", pasDao.getDataHoraPreenchimento());
					pasVO.setProperty("ETAPAACORDOREALIZADO", pasDao.getEtapaAcordoRealizado());
					pasVO.setProperty("DATAHORAACORDOREALIZADO", pasDao.getDataHoraAcordoRealizado());
					pasVO.setProperty("OABADVOGADOAUDIENCIAREDESGNADA", pasDao.getOabAdvogadoAudienciaRedesgnada());
					pasVO.setProperty("UFOABADVOGADOAUDIENCIAREDESGNA", pasDao.getUfOabAdvogadoAudienciaRedesgnada());
					pasVO.setProperty("NOMEADVOGADOAUDIENCIAREDESGNAD", pasDao.getNomeAdvogadoAudienciaRedesgnada());
					pasVO.setProperty("DATAAUDIENCIA", pasDao.getDataAudiencia());
					pasVO.setProperty("NOMETIPOCOMPROMISSO", pasDao.getNomeTipoCompromisso());
					pasVO.setProperty("NOMEPREPOSTO", pasDao.getNomePreposto());
					pasVO.setProperty("NOMETIPOEXECUCAO", pasDao.getNomeTipoExecucao());
					pasVO.setProperty("VALORCALCULOCONDENACAO", pasDao.getValorCalculoCondenacao());
					pasVO.setProperty("DATAPUBLICACAO", pasDao.getDataPublicacao());
					pasVO.setProperty("VALORTERMOSLEGAIS", pasDao.getValorTermosLegais());
					pasVO.setProperty("DATASENTENCA", pasDao.getDataSentenca());
					pasVO.setProperty("NOMETIPOEXTINCAO", pasDao.getNomeTipoExtincao());
					pasVO.setProperty("QUEMRECORREU", pasDao.getQuemRecorreu());
					pasVO.setProperty("INSTANCIA", pasDao.getInstancia());
					pasVO.setProperty("NOMERESULTADORECURSO", pasDao.getNomeResultadoRecurso());
					pasVO.setProperty("DATADECISAORECURSO", pasDao.getDataDecisaoRecurso());

					dwfFacade.saveEntity("AD_PAS", pasEntityVO);

				}

				// ! Pela forma que a pessoa fez nao entra no objeto de update logo acresci a
				// seguinte funcionalidade

				SessionHandle hnd = null;
				try {

				helperLog.info(debug,
							"Entrou para fazer o update referente ao processo de Nro : " + processoDao.getIdProcesso());
					hnd = JapeSession.open();

					JapeFactory.dao("AD_PROCESSOS").prepareToUpdateByPK(processoDao.getIdProcesso())
							.set("CODUSUALTER", AuthenticationInfo.getCurrent().getUserID())
							.set("DHALTER", TimeUtils.getNow()).update();

				helperLog.info(debug, "Fez o update");

				} catch (Exception e) {
				helperLog.error(true, "Erro no update da tabela AD_PROCESSOS");
				} finally {
					JapeSession.close(hnd);
				}

			} else if (registroVO.asString("TIPO").equalsIgnoreCase("Parceiro")) {
			helperLog.info(debug, "Processando integração parceiros!");
				String json = registroVO.asString("JSON");

				JsonObject jsonObject = JsonUtils.convertStringToJsonObject(json);
				Parceiro parceiroDao = new Gson().fromJson(jsonObject, Parceiro.class);

				FinderWrapper finder = new FinderWrapper("AD_CLIENTES", "this.IDPESSOA = ?",
						new Object[] { parceiroDao.getIdPessoa() });
				
				EntityVO entityVO = verificicarVO(dwfFacade, finder);

				DynamicVO parceiroVO = (DynamicVO) entityVO;

				parceiroVO.setProperty("IDPESSOA", parceiroDao.getIdPessoa());
				parceiroVO.setProperty("CPFCNPJ", parceiroDao.getCpfCnpj());
				parceiroVO.setProperty("ISOK", parceiroDao.isOk());
				parceiroVO.setProperty("NOMEPESSOA", parceiroDao.getNomePessoa());
				parceiroVO.setProperty("TIPOPESSOA", parceiroDao.getTipoPessoa());
				parceiroVO.setProperty("CEP", parceiroDao.getCep());
				parceiroVO.setProperty("COMPLEMENTO", parceiroDao.getComplemento());
				parceiroVO.setProperty("LOGRADOURO", parceiroDao.getLogradouro());
				parceiroVO.setProperty("CIDADE", parceiroDao.getCidade());
				parceiroVO.setProperty("BAIRRO", parceiroDao.getBairro());
				parceiroVO.setProperty("NOMEESTADO", parceiroDao.getNomeEstado());
				parceiroVO.setProperty("NUMEROCONTRATOCLIENTE", parceiroDao.getNumeroContratoCliente());

				dwfFacade.saveEntity("AD_CLIENTES", entityVO);
			} else if (registroVO.asString("TIPO").equalsIgnoreCase("Contrato")) {

			helperLog.info(debug, "Processando integração contratos!");

				// Corrigir na agenda
				String json = registroVO.asString("JSON");

				JsonObject jsonObject = JsonUtils.convertStringToJsonObject(json);
				Contratos contratos = new Gson().fromJson(jsonObject, Contratos.class);

				FinderWrapper finder = new FinderWrapper("AD_CONTRATOS", "this.IDCONTRATO = ?",
						new Object[] { contratos.getIdContrato() });
				EntityVO entityVO = verificicarVO(dwfFacade, finder);

				DynamicVO contratoVO = (DynamicVO) entityVO;

				String ativo = contratos.isAtivo() ? "S" : "N";
				
				//BigDecimal ultimoCodigo = UltimoCodigoSankhya.ultimoCodigoSankhya("AD_CONTRATOS", "IDCONTRATO");
                
				contratoVO.setProperty("IDCONTRATO", contratos.getIdContrato());
				contratoVO.setProperty("ISATIVO", ativo);
				//contratoVO.setProperty("IDCONTRATOSEVEN", contratos.getIdContrato().toString());
				contratoVO.setProperty("NUMERO", contratos.getNumero());
				contratoVO.setProperty("ALIAS", contratos.getAlias());
				contratoVO.setProperty("DATAINICIO", contratos.getDataInicio());
				contratoVO.setProperty("DATAFIM", contratos.getDataFim());
				contratoVO.setProperty("IDCLIENTE", contratos.getIdCliente());
				contratoVO.setProperty("NOMECLIENTE", contratos.getNomeCliente());
				contratoVO.setProperty("TIPOCONTRATO", contratos.getTipoContrato().get(0));
				contratoVO.setProperty("SEQUENCIA",registroVO.asBigDecimal("SEQUENCIA") );
				dwfFacade.saveEntity("AD_CONTRATOS", entityVO);

			}

			EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("AD_TGJIMPLOG");
			DynamicVO dynamicVO = (DynamicVO) entityVO;

			dynamicVO.setProperty("IDLOG", null);
			dynamicVO.setProperty("SEQUENCIA", registroVO.asBigDecimal("SEQUENCIA"));
			dynamicVO.setProperty("STATUS", BigDecimal.valueOf(1));
			dynamicVO.setProperty("STACKTRACE", "Registro Criado com Sucesso".toCharArray());
			dynamicVO.setProperty("DHLOG", TimeUtils.getNow());

			dwfFacade.createEntity("AD_TGJIMPLOG", entityVO);

			registroVO.setProperty("STATUS", BigDecimal.valueOf(1));

		} catch (Exception e) {

			e.printStackTrace();

			EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("AD_TGJIMPLOG");
			DynamicVO dynamicVO = (DynamicVO) entityVO;

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			dynamicVO.setProperty("IDLOG", null);
			dynamicVO.setProperty("SEQUENCIA", registroVO.asBigDecimal("SEQUENCIA"));
			dynamicVO.setProperty("STATUS", BigDecimal.valueOf(2));
			dynamicVO.setProperty("STACKTRACE", sw.toString().toCharArray());
			dynamicVO.setProperty("CAUSA", e.getCause().toString().toCharArray());
			dynamicVO.setProperty("DHLOG", TimeUtils.getNow());

			dwfFacade.createEntity("AD_TGJIMPLOG", entityVO);

			registroVO.setProperty("STATUS", BigDecimal.valueOf(2));

		} finally {
			registroVO.setProperty("DHCOMP", TimeUtils.getNow());
			registroVO.setProperty("CODUSUCOMP", AuthenticationInfo.getCurrent().getUserID());

			try {
				BeautifulJson beat = new BeautifulJson();
				String beatiful = beat.beautiful(registroVO.asString("JSON"));

				registroVO.setProperty("JSONBEATIFUL", beatiful.toString().toCharArray());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private EntityVO verificicarVO(EntityFacade dwfFacade, FinderWrapper finder) throws Exception {

		@SuppressWarnings("rawtypes")
		Collection coll = dwfFacade.findByDynamicFinderAsVO(finder);
		boolean ehVazio = coll.isEmpty();

		EntityVO entityVO;

		if (ehVazio) {
			entityVO = dwfFacade.getDefaultValueObjectInstance(finder.getEntity());
		} else {
			entityVO = (EntityVO) coll.iterator().next();
		}

		return entityVO;
	}
	
	

}
