package br.com.sankhya.jaco.integracao.helper;

import java.math.BigDecimal;

import com.sankhya.util.Base64Impl;

public class GeraLinkNotaHelper {

	private static final String APP_LINK = "<a title=\"Abrir Tela\" href=\"/mge/system.jsp#app/{0}/{1}&pk-refresh={3}\" target=\"_top\"><u><b>{2}</b></u></a>";

	public String getLinkNota(String descricao, String parametro, BigDecimal pkTabelaCorrespondente,
			String resourceID) {
		String pk = "{\"PKTABELA\":\"{0}\"}".replace("{0}", pkTabelaCorrespondente.toString()).replace("PKTABELA",
				parametro);
		String url = APP_LINK.replace("{0}", Base64Impl.encode(resourceID.getBytes()).trim());
		url = url.replace("{1}", Base64Impl.encode(pk.getBytes()).trim());
		url = url.replace("{2}", descricao);
		url = url.replace("{3}", String.valueOf(System.currentTimeMillis()));

		return url;
	}

}
