package br.com.sankhya.jaco.actions.reajustes.rotina;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;


public class CarregaContratosDAO {
	
	  private final JdbcWrapper jdbcWrapper;
	  private final SimpleDateFormat mesAno;
	  
	  
	    public CarregaContratosDAO(final JdbcWrapper jdbcWrapper,SimpleDateFormat  mesAno) {
	        this.jdbcWrapper = jdbcWrapper;
	        this.mesAno =  new SimpleDateFormat("MM/yyyy");
	    }
	  
	    
	    
		


	

	    public List<BigDecimal> getContratos(List<BigDecimal> lista) throws Exception {
	        final List<BigDecimal> contratos = new ArrayList<>();

	        String registros = null;
	        for(final BigDecimal codContratos : lista) {
	            registros = (registros == null ? "" : (registros + ", ")) + codContratos.toString();
	        }

	        final NativeSql nativeSql = new NativeSql(jdbcWrapper);
	        
	        nativeSql.appendSql("SELECT CON.CODCONTRATO      AS CODCONTRATO,\r\n"
					+ "       CON.CODMOEDA         AS CODMOEDA,\r\n"
					+ "       CON.DTULTIMOREAJUSTE AS DTULTIMOREAJUSTE,\r\n"
					+ "       CON.DTPROXREAJUSTE   AS DTPROXREAJUSTE, \r\n"
					+ "       CON.CODPARC          AS CODPARC  \r\n" + "  FROM AD_JURCONTRATOS CON\r\n"
					+ " WHERE to_char(CON.DTPROXREAJUSTE, 'MM/yyyy') <= " + mesAno + "\r\n"
					+ "   and CON.ATIVO = 'S'\r\n" + "   AND EXISTS (SELECT 1\r\n"
					+ "          FROM AD_JURSUBTIPOSCONTRATOS SUB\r\n"
					+ "         INNER JOIN AD_JURSUBTIPOS TIP\r\n" + "            ON TIP.SEQUENCIA = SUB.CODSUB\r\n"
					+ "         WHERE SUB.CODCONTRATO = CON.CODCONTRATO\r\n"
					+ "           AND NVL(TIP.ACEITAREAJUSTE, 'N') = 'S')\r\n");
	        try (final ResultSet resultSet = nativeSql.executeQuery()) {
	            while (resultSet.next()) {
	                final BigDecimal codcontrato = resultSet.getBigDecimal("CODCONTRATO");
	    
	                contratos.add(codcontrato);
	            }
	        }

	        return contratos;
	    }

	   
}
