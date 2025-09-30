package br.com.sankhya.jaco.integracao.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import br.com.sankhya.jaco.integracao.dao.Contratos;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;

public class ContratosHelper {


	public static Contratos importarContrato(Boolean debug, EntityFacade dwfFacade, Contratos contratos,
			BigDecimal sequencia) {
		
		HelperLog helperLog = new HelperLog();

		helperLog.debug(debug, "criacao do contrato dentro da tabela de Contrato AD_JURCONTRATOS");

		// HelperLog.debug(debug, "Importando contrato " + vo.asString("JSON"));

		// BigDecimal numContrato = BigDecimalUtil.ZERO_VALUE;
		// Contratos contratos = new Contratos();
		try {

			FinderWrapper finderClienteIntegracao = new FinderWrapper("AD_CLIENTES", "this.IDPESSOA = ?",
					new Object[] { contratos.getIdCliente() });

			Collection<DynamicVO> finderVOClienteIntegracao = dwfFacade
					.findByDynamicFinderAsVO(finderClienteIntegracao);

			// Procurar um contrato que tenha o cliente para garantir que nao existe
			FinderWrapper finderContratoCliente = new FinderWrapper("AD_JURCONTRATOS", "this.IDCONTRATOSEVEN = ?",
					new Object[] { contratos.getIdContrato() });

			Collection<DynamicVO> finderVOContratoCliente = dwfFacade.findByDynamicFinderAsVO(finderContratoCliente);

			boolean notExistsCliente = finderVOClienteIntegracao.isEmpty();
			boolean notExistsContrato = finderVOContratoCliente.isEmpty();

			if (contratos.getCodContratoSnk() == null) {

				helperLog.info(debug,
						"notExistsCliente = " + notExistsCliente + " notExistsContrato = " + notExistsContrato);

				FinderWrapper finderClienteContrato = new FinderWrapper("AD_JURCONTRATOS", "this.IDSEVEN = ?",
						new Object[] { contratos.getIdCliente() });

				Collection<DynamicVO> finderVOClienteContrato = dwfFacade
						.findByDynamicFinderAsVO(finderClienteContrato);

				helperLog.info(debug,
						"Verifica se existe o cliente dentro do contrato pois poderá não existir integração de cliente e existir contrato!");

				boolean existsClienteContrato = !finderVOClienteContrato.isEmpty();

				if (notExistsContrato || existsClienteContrato) {
					helperLog.info(debug, "Adiciona a Lista de Registros para tratar!");
					// Verifica se existe cliente caso nao encontre a integracao
					Collection<DynamicVO> registrosVO = new ArrayList<DynamicVO>();

					if (existsClienteContrato) {
						registrosVO.addAll(finderVOClienteContrato);
					} else {
						registrosVO.addAll(finderVOClienteIntegracao);
					}

					if (!registrosVO.isEmpty()) {

						DynamicVO registro = registrosVO.iterator().next();

						BigDecimal codparc = registro.asBigDecimalOrZero("CODPARC");

						if (codparc.compareTo(BigDecimalUtil.ZERO_VALUE) == 0 && !notExistsCliente) {
							helperLog.info(debug, "Gerando Cliente na Integração!");

							ParceiroHelper parceiro = new ParceiroHelper();
							codparc = parceiro.importarParceiro(registro);

						}

						contratos.setCodParceiroSnk(codparc);

						BigDecimal numContratoResponse = BigDecimalUtil.ZERO_VALUE;

						if (codparc.compareTo(BigDecimalUtil.ZERO_VALUE) > 0) {

							numContratoResponse = geraContrato(contratos, debug);

						} else {

							helperLog.info(debug, "Nao foi gerado nenhum Cliente por Algum Motivo!");

						}

						if (numContratoResponse.compareTo(BigDecimalUtil.ZERO_VALUE) > 0) {
							contratos.setCodContratoSnk(numContratoResponse);
							helperLog.info(debug, "Contrato gerado com sucesso!");

						} else {

							helperLog.info(debug, "Contrato não gerado por Algum Motivo!");
							GenerateLog.generateLog(debug, dwfFacade.getJdbcWrapper(), sequencia, BigDecimal.valueOf(2),
									"Contrato nao foi gerado, verificar!",
									"Por algum motivo no momento da geração do contrato ele não foi gerado! tente refazer a operação!",
									"");

						}

					}
				} else if (!notExistsCliente && sequencia != null) {
					String mensagemErro = "[CLASSE] : ContratosHelper\n" + "[METODO] : importarContrato()\n"
							+ "[LINHA] : 116" + "[ERRO] :" + "Cliente de Código: " + contratos.getIdCliente()
							+ " Contrato : " + contratos.getIdContrato() + " Não foi encontrado na Integração!";

					GenerateLog.generateLog(debug, dwfFacade.getJdbcWrapper(), sequencia, BigDecimal.valueOf(2),
							mensagemErro, "Problemas na Integração!", mensagemErro);

				} else {

					if (sequencia != null) {

						String mensagemErro = "[CLASSE] : ContratosHelper \n" + "[METODO] : importarContrato()\n"
								+ "[LINHA] : 124" + "[ERRO] :" + "Cliente de Código: " + contratos.getIdCliente()
								+ " Contrato : " + contratos.getIdContrato() + "Erro não esperado!";

						GenerateLog.generateLog(debug, dwfFacade.getJdbcWrapper(), sequencia, BigDecimal.valueOf(2),
								mensagemErro,
								"Problemas na Integração!\nVerificar pois houve um problema não identificado.\n",
								mensagemErro);

					}

				}

			}

		} catch (Exception e) {
			helperLog.error(debug, e.getMessage(), e);

		}
		return contratos;

	}

	private static BigDecimal geraContrato(Contratos contrato, Boolean debug) throws MGEModelException {
		
		HelperLog helperLog = new HelperLog();

		helperLog.info(debug, "Metodo geraContrato() \nGerando Contrato!", false);

		BigDecimal numeroContrato = BigDecimalUtil.ZERO_VALUE;

		JapeSession.SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();

			JapeWrapper contratosDAO = JapeFactory.dao("AD_JURCONTRATOS");

			DynamicVO saveVO = contratosDAO.create()
					.set("CODPARC", contrato.getCodParceiroSnk())
					.set("CODEMP", BigDecimal.valueOf(1))
					.set("CODNAT", BigDecimalUtil.ZERO_VALUE)
					.set("CODCENCUS", BigDecimalUtil.ZERO_VALUE)
					.set("CODPROJ", BigDecimalUtil.ZERO_VALUE)
					.set("DTCADASTRO", TimeUtils.getNow())
					//.set("DTVIGENCIA", contrato.getDataInicio())
					.set("ATIVO", "N")
					.set("CODTIPVENDA", BigDecimalUtil.ZERO_VALUE)
					.set("CODEMPRESP", BigDecimal.valueOf(1))
					.set("CODUSU", BigDecimalUtil.ZERO_VALUE)
					.set("DHINCLUSAO", TimeUtils.getNow())
					.set("CODUSUALTER", BigDecimalUtil.ZERO_VALUE)
					.set("IDSEVEN", new BigDecimal(contrato.getIdCliente()))
					.set("IDCONTRATOSEVEN", contrato.getIdContrato())
					.set("NUMEROCONTRATOSEVEN", contrato.getNumero())
					.save();

			helperLog.info(debug, "Salvou contrato no metodo geraContrato()");

			numeroContrato = saveVO.asBigDecimal("CODCONTRATO");

		} catch (Exception e) {
			helperLog.logErrorAndReturnMessage(true, "Erro ao gerar Contrato", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());
			MGEModelException.throwMe(e);

		}
		
		finally {
			JapeSession.close(hnd);
		}

		return numeroContrato;
	}

//	private static BigDecimal geraContrato(Contratos contrato, Boolean debug) throws Exception {
//
//		HelperLog.info(debug, "Metodo geraContrato() \nGerando Contrato!", false);
//
//		BigDecimal numeroContrato = BigDecimalUtil.ZERO_VALUE;
//
//		try {
//			
//			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
//			
//			EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("AD_JURCONTRATOS");
//			
//			DynamicVO dynamicVO = (DynamicVO) entityVO;
//
//
//			dynamicVO.setProperty("CODPARC", contrato.getCodParceiroSnk());
//			dynamicVO.setProperty("CODEMP", BigDecimal.valueOf(1));
//			dynamicVO.setProperty("CODNAT", BigDecimalUtil.ZERO_VALUE);
//			dynamicVO.setProperty("CODCENCUS", BigDecimalUtil.ZERO_VALUE);
//			dynamicVO.setProperty("CODPROJ", BigDecimalUtil.ZERO_VALUE);
//			dynamicVO.setProperty("DTCADASTRO", TimeUtils.getNow());
//			dynamicVO.setProperty("DTVIGENCIA", contrato.getDataInicio());
//			dynamicVO.setProperty("ATIVO", "N");
//			dynamicVO.setProperty("CODTIPVENDA", BigDecimalUtil.ZERO_VALUE);
//			dynamicVO.setProperty("CODEMPRESP", BigDecimal.valueOf(1));
//			dynamicVO.setProperty("CODUSU", BigDecimalUtil.ZERO_VALUE);
//			dynamicVO.setProperty("DHINCLUSAO", TimeUtils.getNow());
//			dynamicVO.setProperty("CODUSUALTER", BigDecimalUtil.ZERO_VALUE);
//			dynamicVO.setProperty("IDSEVEN", contrato.getIdCliente());
//			dynamicVO.setProperty("IDCONTRATOSEVEN", contrato.getIdContrato());
//			dynamicVO.setProperty("NUMEROCONTRATOSEVEN", contrato.getNumero());
//			
//			HelperLog.info(debug, "Salvou contrato no metodo geraContrato()");
//
//			PersistentLocalEntity entityCabecalho = dwfFacade.createEntity("AD_JURCONTRATOS",
//					(EntityVO) dynamicVO);
//
//			DynamicVO cabecalhoVO = (DynamicVO) entityCabecalho.getValueObject();
//
//			 numeroContrato = cabecalhoVO.asBigDecimal("CODCONTRATO");
//			
//
//		} catch (Exception e) {
//			HelperLog.logErrorAndReturnMessage(true, "Erro ao gerar Contrato", e);
//
//		}
//
//		return numeroContrato;
//	}

}
