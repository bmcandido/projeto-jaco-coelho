package br.com.sankhya.jaco.integracao.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Pedidos {



	@SerializedName(value = "IdPedido")
	private BigDecimal idPedido;
	
	@SerializedName(value = "NomeTipoPedido")
	private String nomeTipoPedido;
	
	@SerializedName(value = "ValorPedido")
	private BigDecimal valorPedido;
	
	@SerializedName(value = "DataPedido")
	private String dataPedido;

	@SerializedName(value = "NomeClassificacaoPedido")
	private String nomeClassificacaoPedido;

	@SerializedName(value = "ResultadoPA")
	private String resultadoPA;
	
	@SerializedName(value = "ValorCondenacao")
	private BigDecimal valorCondenacao;
	
	@SerializedName(value = "ResultadoPedidoPA")
	private String resultadoPedidoPA;
	
	@SerializedName(value = "ProbabilidadeExito")
	private String probabilidadeExito;
	
	@SerializedName(value = "NomeTeseAlegadaAcolhida")
	private String nomeTeseAlegadaAcolhida;
	
	@SerializedName(value = "DataCadastro")
	private String dataCadastro;
	
	@SerializedName(value = "Instancia")
	private String instancia;

	
	
	public BigDecimal getIdPedido() {
		return idPedido;
	}

	public String getNomeTipoPedido() {
		return nomeTipoPedido;
	}

	public BigDecimal getValorPedido() {
		return valorPedido;
	}

	public Timestamp getDataPedido() {
		Timestamp timeStampDate = stringToTimestamp(dataPedido);
		return timeStampDate;
	}

	public String getNomeClassificacaoPedido() {
		return nomeClassificacaoPedido;
	}

	public String getResultadoPA() {
		return resultadoPA;
	}

	public BigDecimal getValorCondenacao() {
		return valorCondenacao;
	}

	public String getResultadoPedidoPA() {
		return resultadoPedidoPA;
	}

	public String getProbabilidadeExito() {
		return probabilidadeExito;
	}

	public String getNomeTeseAlegadaAcolhida() {
		return nomeTeseAlegadaAcolhida;
	}

	public Timestamp getDataCadastro() {
		Timestamp timeStampDate = stringToTimestamp(dataCadastro);
		return timeStampDate;
	}

	public String getInstancia() {
		return instancia;
	}
	
	
	private Timestamp stringToTimestamp(String data) {
		if (data == null) {
			return null;
		}
		
	    try {
	    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    Date date = (Date) dateFormat.parse(data);
			Timestamp timeStampDate = new Timestamp(date.getTime());
			return timeStampDate;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
