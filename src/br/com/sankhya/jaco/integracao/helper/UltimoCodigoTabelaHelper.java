package br.com.sankhya.jaco.integracao.helper;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.sql.Types;

import java.math.BigDecimal;
import java.sql.CallableStatement;

public class UltimoCodigoTabelaHelper {

	public static BigDecimal ultimoCodigoSankhya(String tabela, String campo) throws Exception {

		return ultimoCodigoSankhya(tabela, campo, true);
	}

	public static BigDecimal ultimoCodigoSankhya(String tabela, String campo, boolean showLogs) throws Exception {

		JdbcWrapper jdbc = null;

		BigDecimal pkRegistro = BigDecimal.ZERO;
		try {

			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

			jdbc = dwfFacade.getJdbcWrapper();
			jdbc.openSession();

			CallableStatement cstmt = jdbc.getConnection().prepareCall("{call STP_KEYGEN_TGFNUM(?,?,?,?,?,?)}");
			cstmt.setQueryTimeout(60);

			cstmt.setString(1, tabela);
			cstmt.setBigDecimal(2, BigDecimal.ONE);
			cstmt.setString(3, tabela);
			cstmt.setString(4, campo);
			cstmt.setBigDecimal(5, BigDecimal.ZERO);
			cstmt.registerOutParameter(6, Types.DECIMAL);

			cstmt.execute();

			pkRegistro = (BigDecimal) cstmt.getObject(6);

		} catch (Exception extrx) {

			HelperLog helper = new HelperLog();
			helper.error(showLogs, "Erro ao enviar e-mail", extrx);
			MGEModelException.throwMe(extrx);
		} finally {
			JdbcWrapper.closeSession(jdbc);
		}

		HelperLog helper = new HelperLog();

		helper.info(showLogs, "Retorno PK Ultimo Número" + pkRegistro);

		return pkRegistro;
	}
}
