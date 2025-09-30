package br.com.sankhya.jaco.integracao.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Recursos {

	@SerializedName(value = "IdRecurso")
	private BigDecimal idRecurso;
	
	@SerializedName(value = "ProtocoloAtual")
	private String protocoloAtual;
	
	@SerializedName(value = "NomeEstado")
	private String nomeEstado;
	
	@SerializedName(value = "NomeOrigem")
	private String nomeOrigem;
	
	@SerializedName(value = "NomeCidade")
	private String nomeCidade;
	
	@SerializedName(value = "NomeJuizado")
	private String nomeJuizado;
	
	@SerializedName(value = "NomeTipoRecurso")
	private String nomeTipoRecurso;
	
	@SerializedName(value = "NumJurisdicao")
	private String numJurisdicao;
	
	@SerializedName(value = "NomeNatureza")
	private String nomeNatureza;
	
	@SerializedName(value = "Instancia")
	private String instancia;
	
	@SerializedName(value = "DataRecebimento")
	private String dataRecebimento;
	
	
	public BigDecimal getIdRecurso() {
		return idRecurso;
	}

	public String getProtocoloAtual() {
		return protocoloAtual;
	}

	public String getNomeOrigem() {
		return nomeOrigem;
	}

	
	public String getNomeEstado() {
		return nomeEstado;
	}

	public String getNomeCidade() {
		return nomeCidade;
	}

	public String getNomeJuizado() {
		return nomeJuizado;
	}

	public String getNomeTipoRecurso() {
		return nomeTipoRecurso;
	}

	public String getNumJurisdicao() {
		return numJurisdicao;
	}

	public String getNomeNatureza() {
		return nomeNatureza;
	}

	public String getInstancia() {
		return instancia;
	}

	public Timestamp getDataRecebimento() {
		Timestamp timeStampDate = stringToTimestamp(dataRecebimento);
		return timeStampDate;
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
