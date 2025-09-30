package br.com.sankhya.jaco.integracao.dao;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Pas {


	@SerializedName(value = "IdPa")
	private BigDecimal idPa;
	
	@SerializedName(value = "NomeResultado")
	private String resultado;
	
	@SerializedName(value = "DataHoraPreenchimento")
	private String dataHoraPreenchimento;
	
	@SerializedName(value = "EtapaAcordoRealizado")
	private String etapaAcordoRealizado;
	
	@SerializedName(value = "DataHoraAcordoRealizado")
	private String dataHoraAcordoRealizado;
	
	@SerializedName(value = "OabAdvogadoAudienciaRedesgnada")
	private String oabAdvogadoAudienciaRedesgnada;
	
	@SerializedName(value = "UfOabAdvogadoAudienciaRedesgnada")
	private String ufOabAdvogadoAudienciaRedesgnada;
	
	@SerializedName(value = "NomeAdvogadoAudienciaRedesgnada")
	private String nomeAdvogadoAudienciaRedesgnada;
	
	@SerializedName(value = "DataAudiencia")
	private String dataAudiencia;
	
	@SerializedName(value = "NomeTipoCompromisso")
	private String nomeTipoCompromisso;
	
	@SerializedName(value = "NomePreposto")
	private String nomePreposto;
	
	@SerializedName(value = "NomeTipoExecucao")
	private String nomeTipoExecucao;
	
	@SerializedName(value = "ValorCalculoCondenacao")
	private BigDecimal valorCalculoCondenacao;
	
	@SerializedName(value = "DataPublicacao")
	private String dataPublicacao;
	
	@SerializedName(value = "ValorTermosLegais")
	private BigDecimal valorTermosLegais;
	
	@SerializedName(value = "DataSentenca")
	private String dataSentenca;
	
	@SerializedName(value = "NomeTipoExtincao")
	private String nomeTipoExtincao;
	
	@SerializedName(value = "QuemRecorreu")
	private String quemRecorreu;
	
	@SerializedName(value = "Instancia")
	private String instancia;
	
	@SerializedName(value = "NomeResultadoRecurso")
	private String nomeResultadoRecurso;
	
	@SerializedName(value = "DataDecisaoRecurso")
	private String dataDecisaoRecurso;
	
	
	
	public BigDecimal getIdPa() {
		return idPa;
	}

	public String getResultado() {
		return resultado;
	}

	public Timestamp getDataHoraPreenchimento() {
		Timestamp timeStampDate = stringToTimestamp(dataHoraPreenchimento);
		return timeStampDate;
	}

	public String getEtapaAcordoRealizado() {
		return etapaAcordoRealizado;
	}

	public Timestamp getDataHoraAcordoRealizado() {
		Timestamp timeStampDate = stringToTimestamp(dataHoraAcordoRealizado);
		return timeStampDate;
	}

	public String getOabAdvogadoAudienciaRedesgnada() {
		return oabAdvogadoAudienciaRedesgnada;
	}

	public String getUfOabAdvogadoAudienciaRedesgnada() {
		return ufOabAdvogadoAudienciaRedesgnada;
	}

	public String getNomeAdvogadoAudienciaRedesgnada() {
		return nomeAdvogadoAudienciaRedesgnada;
	}

	public Timestamp getDataAudiencia() {
		Timestamp timeStampDate = stringToTimestamp(dataAudiencia);
		return timeStampDate;
	}

	public String getNomeTipoCompromisso() {
		return nomeTipoCompromisso;
	}

	public String getNomePreposto() {
		return nomePreposto;
	}

	public String getNomeTipoExecucao() {
		return nomeTipoExecucao;
	}

	public BigDecimal getValorCalculoCondenacao() {
		return valorCalculoCondenacao;
	}

	public Timestamp getDataPublicacao() {
		Timestamp timeStampDate = stringToTimestamp(dataPublicacao);
		return timeStampDate;
	}

	public BigDecimal getValorTermosLegais() {
		return valorTermosLegais;
	}

	public Timestamp getDataSentenca() {
		Timestamp timeStampDate = stringToTimestamp(dataSentenca);
		return timeStampDate;
	}

	public String getNomeTipoExtincao() {
		return nomeTipoExtincao;
	}

	public String getQuemRecorreu() {
		return quemRecorreu;
	}

	public String getInstancia() {
		return instancia;
	}

	public String getNomeResultadoRecurso() {
		return nomeResultadoRecurso;
	}

	public Timestamp getDataDecisaoRecurso() {
		Timestamp timeStampDate = stringToTimestamp(dataDecisaoRecurso);
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
