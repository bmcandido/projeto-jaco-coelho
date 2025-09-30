package br.com.sankhya.jaco.integracao.helper;

import java.math.BigDecimal;
import java.util.Collection;

import com.sankhya.util.BigDecimalUtil;
import org.jdom.Element;

import com.sankhya.util.StringUtils;
import com.sankhya.util.XMLUtils;

import br.com.sankhya.dwf.services.ServiceUtils;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.facades.PesquisaCepSP;
import br.com.sankhya.modelcore.facades.PesquisaCepSPHome;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class ParceiroHelper {

	public BigDecimal importarParceiro(DynamicVO registroVO) throws Exception {
        BigDecimal codParc = BigDecimalUtil.ZERO_VALUE;
        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

        FinderWrapper finder = new FinderWrapper(DynamicEntityNames.PARCEIRO
                , "this.CGC_CPF = ?"
                , new Object[]{registroVO.asString("CPFCNPJ").replaceAll("[^0-9]", "")});
        finder.setOrderBy("this.CODPARC ASC");
        EntityVO entityVO = verificicarVO(dwfFacade, finder);

        DynamicVO parceiroVO = (DynamicVO) entityVO;

        parceiroVO.setProperty("TIPPESSOA", (registroVO.asString("TIPOPESSOA").equals("1")) ? "F" : "J");
        parceiroVO.setProperty("ATIVO", (registroVO.asString("ISOK").equals("1")) ? "S" : "N");
        parceiroVO.setProperty("CGC_CPF", registroVO.asString("CPFCNPJ").replaceAll("[^0-9]", ""));

        parceiroVO.setProperty("NOMEPARC", StringUtils.removerCaracteresEspeciais(registroVO.asString("NOMEPESSOA").toUpperCase()));
        parceiroVO.setProperty("RAZAOSOCIAL", StringUtils.removerCaracteresEspeciais(registroVO.asString("NOMEPESSOA").toUpperCase()));

        parceiroVO.setProperty("CLASSIFICMS", (parceiroVO.asString("CLASSIFICMS") != null) ? parceiroVO.asString("CLASSIFICMS") : "C");

        BigDecimal codBairro = BigDecimal.ZERO;
        BigDecimal codCid = BigDecimal.valueOf(4);
        BigDecimal codEnd = BigDecimal.ZERO;

        if (registroVO.asString("CEP") != null) {

            PesquisaCepSP pesquisaCEP = (PesquisaCepSP) ServiceUtils.getStatelessFacade(PesquisaCepSPHome.JNDI_NAME, PesquisaCepSPHome.class);
			
			/*
			<serviceRequest serviceName="PesquisaCepSP.obterDadosDoCep">
				<requestBody>
					<cep>74230050</cep>
				</requestBody>
			</serviceRequest>
			*/

            ServiceContext sctx = ServiceContext.getCurrent();

            try {
                ServiceContext ctx = new ServiceContext(null);
                ctx.setAutentication(AuthenticationInfo.getCurrent());
                ctx.makeCurrent();
                ctx.setRequestBody(new Element("requestBody"));
                Element requestBody = ctx.getRequestBody();
                XMLUtils.addContentElement(requestBody, "cep", StringUtils.removerCaracteresEspeciais(registroVO.asString("CEP")));


                pesquisaCEP.obterDadosDoCep(ctx);

                Element responseBody = ctx.getBodyElement();

                Element ceps = XMLUtils.getChild(responseBody, "ceps");
                Element enderecos = XMLUtils.getChild(ceps, "enderecos");

                codBairro = XMLUtils.getAttributeAsBigDecimal(enderecos, "codBairro");
                codCid = XMLUtils.getAttributeAsBigDecimal(enderecos, "codCid");
                codEnd = XMLUtils.getAttributeAsBigDecimal(enderecos, "codEnd");

            } catch (Exception e) {

                codBairro = BigDecimal.ZERO;
                codCid = BigDecimal.valueOf(4); // Goiania
                codEnd = BigDecimal.ZERO;

            }

            sctx.makeCurrent();

        }
		/*
		<?xml version="1.0" encoding="ISO-8859-1"?>
		<serviceResponse serviceName="PesquisaCepSP.obterDadosDoCep" status="1" pendingPrinting="false" transactionId="7149F9E8C9B1EAFB761E4F7772283D9E">
			<responseBody>
				<ceps quantidade="1">
					<enderecos cep="74230050" codBairro="187" codCid="4" codEnd="5845" codUf="4" descBairro="SETOR BUENO" descCid="Goi�nia" descEnd="T 13" descUf="Goi�s" />
				</ceps>
			</responseBody>
		</serviceResponse>
		*/

        parceiroVO.setProperty("CEP", registroVO.asString("CEP") != null ? registroVO.asString("CEP").replaceAll("[^0-9]", "") : null);
        parceiroVO.setProperty("CODCID", codCid);
        parceiroVO.setProperty("CODBAI", codBairro);
        parceiroVO.setProperty("CODEND", codEnd);

        String complemento = StringUtils.removerCaracteresEspeciais(registroVO.asString("COMPLEMENTO"));
        complemento = StringUtils.secureSubstring(complemento, 0, 29);

        parceiroVO.setProperty("COMPLEMENTO", complemento);

        PersistentLocalEntity pleProcessos = null;

        if (parceiroVO.asBigDecimal("CODPARC") == null) {
            pleProcessos = dwfFacade.createEntity(DynamicEntityNames.PARCEIRO, entityVO);
        } else {
            pleProcessos = dwfFacade.saveEntity(DynamicEntityNames.PARCEIRO, entityVO);
        }

        parceiroVO = (DynamicVO) pleProcessos.getValueObject();


        // Atualizando o Registro de Origem
        registroVO.setProperty("CODPARC", parceiroVO.asBigDecimal("CODPARC"));

        codParc = parceiroVO.asBigDecimal("CODPARC");
        return codParc;

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
