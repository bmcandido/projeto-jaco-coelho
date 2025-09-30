package br.com.sankhya.jaco.integracao.helper;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Collection;

public class GenerateLog {
	


	public static void generateLog(Boolean debug, 
			JdbcWrapper jdbc, 
			BigDecimal sequenciaRegistroLog, 
			BigDecimal status,
			String mensagemLog, 
			String assuntoEmail, 
			String corpoEmail) {
		
		HelperLog helperLog = new HelperLog();

		helperLog.debug(debug, "Gerando Log de Erros e Enviando E-mail de Avisos para o status : " + status.intValue());

		try {

			int sendEmailCount = 0;
			String sendEmailYesNo = "N";

			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			ArrayList<String> usersEmails = new ArrayList<>();

			if (status.intValue() > 1) {
				helperLog.info(debug, "Gerando Log de Erros e Enviando E-mail de Avisos");

				FinderWrapper finderUsers = new FinderWrapper(DynamicEntityNames.USUARIO,
						"this.AD_RECEBEEMAILLOGERROSINT = ?", new Object[] { "S" });
				Collection<DynamicVO> usuariosRecebemEmailVO = dwfFacade.findByDynamicFinderAsVO(finderUsers);

				for (DynamicVO voUser : usuariosRecebemEmailVO) {
					// Envia e-mail
					String email = voUser.asString("EMAIL");
					if (email.isEmpty()) {

						sendEmailCount++;
						CallableStatement cstmt = jdbc.getConnection().prepareCall("{call STP_ENVIAEMAIL_PS(?,?,?)}");
						cstmt.setQueryTimeout(60);
						cstmt.setString(1, email);
						cstmt.setString(2, assuntoEmail);
						cstmt.setString(3, corpoEmail);
						cstmt.execute();
						usersEmails.add(voUser.asString("EMAIL"));
					}
				}
				if (sendEmailCount > 0) {
					sendEmailYesNo = "S";
				}

			}

			EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("AD_TGJIMPLOG");
			DynamicVO dynamicVO = (DynamicVO) entityVO;

//            dynamicVO.setProperty("IDLOG", null);
			dynamicVO.setProperty("SEQUENCIA", sequenciaRegistroLog);
			dynamicVO.setProperty("STATUS", status);
			dynamicVO.setProperty("STACKTRACE", null);
			dynamicVO.setProperty("CAUSA", mensagemLog.toCharArray());
			dynamicVO.setProperty("DHLOG", TimeUtils.getNow());

			if (sendEmailYesNo.equals("S")) {
				String emailsString = String.join(", ", usersEmails);
				dynamicVO.setProperty("AVISARESPONSAVEL", sendEmailYesNo);
				dynamicVO.setProperty("RESPONSAVEIS", emailsString);
			}

			dwfFacade.createEntity("AD_TGJIMPLOG", entityVO);

		} catch (Exception e) {
			helperLog.error(debug, "Erro ao gerar Log de Erros", e);

		}
	}
}
