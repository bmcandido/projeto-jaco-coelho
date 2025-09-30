package br.com.sankhya.jaco.integracao.dao;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Contratos {

	@SerializedName("IdContrato")
	private BigDecimal idContrato;
	@SerializedName("IsAtivo")
	private boolean isAtivo;
	@SerializedName("Numero")
	private String numero;
	@SerializedName("Alias")
	private String alias;
	@SerializedName("DataInicio")
	private String dataInicio;
	@SerializedName("DataFim")
	private String dataFim;
	@SerializedName("IdCliente")
	private String idCliente;
	@SerializedName("NomeCliente")
	private String nomeCliente;
	@SerializedName("TipoContrato")
	private List<String> tipoContrato;

	private BigDecimal codParceiroSnk;
	private BigDecimal codContratoSnk;

	public BigDecimal getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(BigDecimal idContrato) {
		this.idContrato = idContrato;
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	public String getNomePessoa() {
		return nomeCliente;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomeCliente = nomePessoa;
	}

	public List<String> getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(List<String> tipoContrato) {
		this.tipoContrato = tipoContrato;
	}

	public Timestamp getDataInicio() {
		Timestamp timeStampDate = stringToTimestamp(dataInicio);
		return timeStampDate;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Timestamp getDataFim() {
		Timestamp timeStampDate = stringToTimestamp(dataFim);
		return timeStampDate;
	}

	public BigDecimal getCodParceiroSnk() {
		return codParceiroSnk;
	}

	public void setCodParceiroSnk(BigDecimal codParceiroSnk) {
		this.codParceiroSnk = codParceiroSnk;
	}

	public BigDecimal getCodContratoSnk() {
		return codContratoSnk;
	}

	public void setCodContratoSnk(BigDecimal codContratoSnk) {
		this.codContratoSnk = codContratoSnk;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	public boolean isAtivo() {
		return isAtivo;
	}

	public void setAtivo(boolean isAtivo) {
		this.isAtivo = isAtivo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
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
