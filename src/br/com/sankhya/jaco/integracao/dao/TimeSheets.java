package br.com.sankhya.jaco.integracao.dao;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class TimeSheets {


	@SerializedName(value = "IdTimeSheet")
	private BigDecimal idTimeSheet;
	
	@SerializedName(value = "NomeAtividadeTimeSheet")
	private String nomeAtividadeTimeSheet;
	
	@SerializedName(value = "NomeGrupoCliente")
	private String nomeGrupoCliente;
	
	@SerializedName(value = "Observacao")
	private String observacao;
	
	@SerializedName(value = "DataInicio")
	private String dataInicio;
	
	@SerializedName(value = "DataConclusao")
	private String dataConclusao;
	
	
	@SerializedName(value = "NomeResponsavel")
	private String nomeResponsavel;
	
	@SerializedName(value = "NomeUsuarioCadastro")
	private String nomeUsuarioCadastro;
	
	@SerializedName(value = "DataCadastro")
	private String dataCadastro;
	
	@SerializedName(value = "TotalHoras")
	private String totalHoras;
	
	@SerializedName(value = "Status")
	private String status;
	
	
	public BigDecimal getIdTimeSheet() {
		return idTimeSheet;
	}

	public String getNomeAtividadeTimeSheet() {
		return nomeAtividadeTimeSheet;
	}

	public String getNomeGrupoCliente() {
		return nomeGrupoCliente;
	}

	public String getObservacao() {
		
		if (observacao == null) {
			return ""; 
		}
		
		return observacao;
	}

	public Timestamp getDataInicio() {
		Timestamp timeStampDate = stringToTimestamp(dataInicio);
		return timeStampDate;
	}

	public Timestamp getDataConclusao() {
		Timestamp timeStampDate = stringToTimestamp(dataConclusao);
		return timeStampDate;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public String getNomeUsuarioCadastro() {
		return nomeUsuarioCadastro;
	}

	public Timestamp getDataCadastro() {
		Timestamp timeStampDate = stringToTimestamp(dataCadastro);
		return timeStampDate;
	}

	public String getTotalHoras() {
		return totalHoras;
	}

	public String getStatus() {
		return status;
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
