package br.com.sankhya.jaco.integracao.dao;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Processo {

	// @SerializedName(value = "IdProcesso", alternate = "IdProcesso2")
	@SerializedName(value = "IdProcesso")
	private BigDecimal idProcesso;

	@SerializedName(value = "Status")
	private String status;

	@SerializedName(value = "NomeOrigem")
	private String nomeOrigem;

	@SerializedName(value = "NumJurisdicao")
	private String numJurisdicao;

	@SerializedName(value = "Instancia")
	private String instancia;

	@SerializedName(value = "NomeEscritorio")
	private String nomeEscritorio;

	@SerializedName(value = "NumeroContratoCliente")
	private String numeroContratoCliente;

	@SerializedName(value = "NomeEstado")
	private String nomeEstado;

	@SerializedName(value = "NomeJuizado")
	private String nomeJuizado;

	@SerializedName(value = "NumeroProcessoAntigo")
	private String numeroProcessoAntigo;

	@SerializedName(value = "NomeAdvResponsavel")
	private String nomeAdvResponsavel;

	@SerializedName(value = "NomeNatureza")
	private String nomeNatureza;

	@SerializedName(value = "NomeCidade")
	private String nomeCidade;

	@SerializedName(value = "ProtocoloAtual")
	private String protocoloAtual;

	@SerializedName(value = "IsFormularioDefesaPendente")
	private boolean isFormularioDefesaPendente;

	@SerializedName(value = "NomeGrupoCliente")
	private String nomeGrupoCliente;

	@SerializedName(value = "CpfCnpjGrupoCliente")
	private String cpfCnpjGrupoCliente;

	@SerializedName(value = "NomeClientePrincipal")
	private String nomeClientePrincipal;

	@SerializedName(value = "NPC")
	private String npc;

	@SerializedName(value = "NomeAdversoPrincipal")
	private String nomeAdversoPrincipal;

	@SerializedName(value = "CpfCnpjClientePrincipal")
	private String cpfCnpjClientePrincipal;

	@SerializedName(value = "DataCadEscritorioSistemaSeguradora")
	private String dataCadEscritorioSistemaSeguradora;

	@SerializedName(value = "NomeSistemaCliente")
	private String nomeSistemaCliente;

	@SerializedName(value = "DataDistribuicao")
	private String dataDistribuicao;

	@SerializedName(value = "DataHoraCadastro")
	private String dataHoraCadastro;

	@SerializedName(value = "DataCitacao")
	private String dataCitacao;

	@SerializedName(value = "ValorCausa")
	private BigDecimal valorCausa;

	@SerializedName(value = "ValorPedido")
	private BigDecimal valorPedido;

	@SerializedName(value = "DataAjuizamento")
	private String dataAjuizamento;

	@SerializedName(value = "PastaCPPRO")
	private String pastaCPPRO;

	@SerializedName(value = "NomeSubGrupoSeguradora")
	private String nomeSubGrupoSeguradora;

	@SerializedName(value = "ValorRiscoCalculoHonorarios")
	private BigDecimal valorRiscoCalculoHonorarios;

	@SerializedName(value = "NomeSegmentoSeguradora")
	private String nomeSegmentoSeguradora;

	@SerializedName(value = "IsClienteAssistenciaJudiciaria")
	private boolean isClienteAssistenciaJudiciaria;

	@SerializedName(value = "NumeroProcessoRPVPrecatorio")
	private String numeroProcessoRPVPrecatorio;

	@SerializedName(value = "DataExpedicaoRPVPrecatorio")
	private String dataExpedicaoRPVPrecatorio;

	@SerializedName(value = "ValorFixadoCliente")
	private BigDecimal valorFixadoCliente;

	@SerializedName(value = "Apolice")
	private String apolice;

	@SerializedName(value = "Ramo")
	private String ramo;

	@SerializedName(value = "Produto")
	private String produto;

	@SerializedName(value = "NomeTipoContratacao")
	private String nomeTipoContratacao;

	@SerializedName(value = "NomeCentrocusto")
	private String nomeCentrocusto;

	@SerializedName(value = "SinistroJudicial")
	private String sinistroJudicial;

	@SerializedName(value = "StatusAcordo")
	private String statusAcordo;

	@SerializedName(value = "IsAcordadoComAutor")
	private boolean isAcordadoComAutor;

	@SerializedName(value = "ValorAcordado")
	private BigDecimal valorAcordado;

	@SerializedName(value = "DataPagamentoAcordo")
	private String dataPagamentoAcordo;

	@SerializedName(value = "DataAssinaturaMinuta")
	private String dataAssinaturaMinuta;

	@SerializedName(value = "SegundoTitularCc")
	private String segundoTitularCc;

	@SerializedName(value = "CpfSegundoTitularCc")
	private String cpfSegundoTitularCc;

	@SerializedName(value = "NomeAssunto")
	private String nomeAssunto;

	@SerializedName(value = "SinistroAdministrativo")
	private String sinistroAdministrativo;

	@SerializedName(value = "IdCliente")
	private BigDecimal idCliente;

	@SerializedName(value = "TipoAcao")
	private String tipoAcao;

	@SerializedName(value = "JuridicoSeguradora")
	private String juridicoSeguradora;

	@SerializedName(value = "PastaMigrada")
	private String pastaMigrada;

	@SerializedName(value = "Objeto")
	private String objeto;

	@SerializedName(value = "ObjetoId")
	private String objetoId;

	@SerializedName(value = "IdContrato")
	private BigDecimal idContrato;

	@SerializedName(value = "IdCidade")
	private BigDecimal idCidade;

	@SerializedName(value = "NumeroContrato")
	private String numerContrato;
	
	

    @SerializedName(value = "DataHoraEncerramento")
    private String dataHoraEncerramento;

	@SerializedName(value = "Compromissos")
	private List<Compromisso> compromissos;

	@SerializedName(value = "TimeSheets")
	private List<TimeSheets> timeSheets;

	@SerializedName(value = "Recursos")
	private List<Recursos> recursos;

	@SerializedName(value = "Pedidos")
	private List<Pedidos> pedidos;

	@SerializedName(value = "PAs")
	private List<Pas> pas;

	public BigDecimal getIdProcesso() {
		return idProcesso;
	}

	public String getStatus() {
		return status;
	}

	public String getNomeOrigem() {
		return nomeOrigem;
	}

	public String getNumJurisdicao() {
		return numJurisdicao;
	}

	public String getInstancia() {
		return instancia;
	}

	public String getNomeEscritorio() {
		return nomeEscritorio;
	}

	public String getNumeroContratoCliente() {
		return numeroContratoCliente;
	}

	public String getNomeEstado() {
		return nomeEstado;
	}

	public String getNomeJuizado() {
		return nomeJuizado;
	}

	public String getNumeroProcessoAntigo() {
		return numeroProcessoAntigo;
	}

	public String getNomeAdvResponsavel() {
		return nomeAdvResponsavel;
	}

	public String getNomeNatureza() {
		return nomeNatureza;
	}

	public String getNomeCidade() {
		return nomeCidade;
	}

	public String getProtocoloAtual() {
		return protocoloAtual;
	}

	public String isFormularioDefesaPendente() {
		if (isFormularioDefesaPendente) {
			return "1";
		}
		return "0";
	}

	public String getNomeGrupoCliente() {
		return nomeGrupoCliente;
	}

	public String getCpfCnpjGrupoCliente() {
		return cpfCnpjGrupoCliente;
	}

	public String getNomeClientePrincipal() {
		return nomeClientePrincipal;
	}

	public String getNpc() {
		return npc;
	}

	public String getNomeAdversoPrincipal() {
		return nomeAdversoPrincipal;
	}

	public String getCpfCnpjClientePrincipal() {
		return cpfCnpjClientePrincipal;
	}

	public BigDecimal getIdCidade() {
		return idCidade;
	}

	public Timestamp getDataCadEscritorioSistemaSeguradora() {
		Timestamp timeStampDate = stringToTimestamp(dataCadEscritorioSistemaSeguradora);
		return timeStampDate;
	}

	public String getNomeSistemaCliente() {
		return nomeSistemaCliente;
	}

	public Timestamp getDataDistribuicao() {
		Timestamp timeStampDate = stringToTimestamp(dataDistribuicao);
		return timeStampDate;
	}

	public Timestamp getDataHoraCadastro() {
		Timestamp timeStampDate = stringToTimestamp(dataHoraCadastro);
		return timeStampDate;
	}

	public Timestamp getDataCitacao() {
		Timestamp timeStampDate = stringToTimestamp(dataCitacao);
		return timeStampDate;
	}

	public BigDecimal getValorCausa() {
		return valorCausa;
	}

	public BigDecimal getValorPedido() {
		return valorPedido;
	}

	public Timestamp getDataAjuizamento() {
		Timestamp timeStampDate = stringToTimestamp(dataAjuizamento);
		return timeStampDate;
	}

	public String getPastaCPPRO() {
		return pastaCPPRO;
	}

	public String getNomeSubGrupoSeguradora() {
		return nomeSubGrupoSeguradora;
	}

	public BigDecimal getValorRiscoCalculoHonorarios() {
		return valorRiscoCalculoHonorarios;
	}

	public String getNomeSegmentoSeguradora() {
		return nomeSegmentoSeguradora;
	}

	public String isClienteAssistenciaJudiciaria() {
		if (isClienteAssistenciaJudiciaria) {
			return "1";
		}
		return "0";
	}

	public String getNumeroProcessoRPVPrecatorio() {
		return numeroProcessoRPVPrecatorio;
	}

	public Timestamp getDataExpedicaoRPVPrecatorio() {
		Timestamp timeStampDate = stringToTimestamp(dataExpedicaoRPVPrecatorio);
		return timeStampDate;
	}

	public BigDecimal getValorFixadoCliente() {
		return valorFixadoCliente;
	}

	public String getApolice() {
		return apolice;
	}

	public String getRamo() {
		return ramo;
	}

	public String getProduto() {
		return produto;
	}

	public String getNomeTipoContratacao() {
		return nomeTipoContratacao;
	}

	public String getNomeCentrocusto() {
		return nomeCentrocusto;
	}

	public String getSinistroJudicial() {
		return sinistroJudicial;
	}

	public String getStatusAcordo() {
		return statusAcordo;
	}

	public String getTipoAcao() {
		return tipoAcao;
	}

	public String getJuridicoSeguradora() {
		return juridicoSeguradora;
	}

	public String getAcordadoComAutor() {
		if (isAcordadoComAutor) {
			return "1";
		}
		return "0";
	}

	public BigDecimal getValorAcordado() {
		return valorAcordado;
	}

	public Timestamp getDataPagamentoAcordo() {
		Timestamp timeStampDate = stringToTimestamp(dataPagamentoAcordo);
		return timeStampDate;
	}

	public Timestamp getDataAssinaturaMinuta() {
		Timestamp timeStampDate = stringToTimestamp(dataAssinaturaMinuta);
		return timeStampDate;
	}

	public String getSegundoTitularCc() {
		return segundoTitularCc;
	}

	public String getCpfSegundoTitularCc() {
		return cpfSegundoTitularCc;
	}

	public String getNomeAssunto() {
		return nomeAssunto;
	}

	public String getSinistroAdministrativo() {
		return sinistroAdministrativo;
	}

	public BigDecimal getIdCliente() {
		return idCliente;
	}

	public boolean isAcordadoComAutor() {
		return isAcordadoComAutor;
	}

	public String getPastaMigrada() {
		return pastaMigrada;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

	public String getObjetoId() {
		return objetoId;
	}

	public void setObjetoId(String objetoId) {
		this.objetoId = objetoId;
	}

	public BigDecimal getIdContrato() {
		return idContrato != null ? idContrato : BigDecimal.ZERO;
	}

	public void setIdContrato(BigDecimal idContrato) {
		this.idContrato = idContrato;
	}
	
	public Timestamp getDataHoraEncerramento() {
		Timestamp timeStampDate = stringToTimestamp(dataHoraEncerramento);
		return timeStampDate;
	}

	public List<Compromisso> getCompromissos() {
		return compromissos;
	}

	public List<TimeSheets> getTimeSheets() {
		return timeSheets;
	}

	public List<Recursos> getRecursos() {
		return recursos;
	}

	public List<Pedidos> getPedidos() {
		return pedidos;
	}

	public List<Pas> getPas() {
		return pas;
	}

	public String getNumerContrato() {
		return numerContrato;
	}

	public void setNumerContrato(String numerContrato) {
		this.numerContrato = numerContrato;
	}

	@Override
	public String toString() {
		return "Processo [getIdProcesso()=" + getIdProcesso() + ", getStatus()=" + getStatus() + ", getNomeOrigem()="
				+ getNomeOrigem() + ", getNumJurisdicao()=" + getNumJurisdicao() + ", getInstancia()=" + getInstancia()
				+ ", getNomeEscritorio()=" + getNomeEscritorio() + ", getNumeroContratoCliente()="
				+ getNumeroContratoCliente() + ", getNomeEstado()=" + getNomeEstado() + ", getNomeJuizado()="
				+ getNomeJuizado() + ", getNumeroProcessoAntigo()=" + getNumeroProcessoAntigo()
				+ ", getNomeAdvResponsavel()=" + getNomeAdvResponsavel() + ", getNomeNatureza()=" + getNomeNatureza()
				+ ", getNomeCidade()=" + getNomeCidade() + ", getProtocoloAtual()=" + getProtocoloAtual()
				+ ", isFormularioDefesaPendente()=" + isFormularioDefesaPendente() + ", getNomeGrupoCliente()="
				+ getNomeGrupoCliente() + ", getCpfCnpjGrupoCliente()=" + getCpfCnpjGrupoCliente()
				+ ", getNomeClientePrincipal()=" + getNomeClientePrincipal() + ", getNpc()=" + getNpc()
				+ ", getNomeAdversoPrincipal()=" + getNomeAdversoPrincipal() + ", getCpfCnpjClientePrincipal()="
				+ getCpfCnpjClientePrincipal() + ", getDataCadEscritorioSistemaSeguradora()="
				+ getDataCadEscritorioSistemaSeguradora() + ", getNomeSistemaCliente()=" + getNomeSistemaCliente()
				+ ", getDataDistribuicao()=" + getDataDistribuicao() + ", getDataHoraCadastro()="
				+ getDataHoraCadastro() + ", getDataCitacao()=" + getDataCitacao() + ", getValorCausa()="
				+ getValorCausa() + ", getValorPedido()=" + getValorPedido() + ", getDataAjuizamento()="
				+ getDataAjuizamento() + ", getPastaCPPRO()=" + getPastaCPPRO() + ", getNomeSubGrupoSeguradora()="
				+ getNomeSubGrupoSeguradora() + ", getValorRiscoCalculoHonorarios()=" + getValorRiscoCalculoHonorarios()
				+ ", getNomeSegmentoSeguradora()=" + getNomeSegmentoSeguradora() + ", isClienteAssistenciaJudiciaria()="
				+ isClienteAssistenciaJudiciaria() + ", getNumeroProcessoRPVPrecatorio()="
				+ getNumeroProcessoRPVPrecatorio() + ", getDataExpedicaoRPVPrecatorio()="
				+ getDataExpedicaoRPVPrecatorio() + ", getValorFixadoCliente()=" + getValorFixadoCliente()
				+ ", getApolice()=" + getApolice() + ", getRamo()=" + getRamo() + ", getProduto()=" + getProduto()
				+ ", getNomeTipoContratacao()=" + getNomeTipoContratacao() + ", getNomeCentrocusto()="
				+ getNomeCentrocusto() + ", getSinistroJudicial()=" + getSinistroJudicial() + ", getStatusAcordo()="
				+ getStatusAcordo() + ", getAcordadoComAutor()=" + getAcordadoComAutor() + ", getValorAcordado()="
				+ getValorAcordado() + ", getDataPagamentoAcordo()=" + getDataPagamentoAcordo()
				+ ", getDataAssinaturaMinuta()=" + getDataAssinaturaMinuta() + ", getSegundoTitularCc()="
				+ getSegundoTitularCc() + ", getNomeAssunto()=" + getNomeAssunto() + ", getSinistroAdministrativo()="
				+ getSinistroAdministrativo() + ", getIdCliente()=" + getIdCliente() + ", getTipoAcao()="
				+ getTipoAcao() + ", getCompromissos()=" + getCompromissos() + ", getTimeSheets()=" + getTimeSheets()
				+ ", getRecursos()=" + getRecursos() + ", getPedidos()=" + getPedidos() + ", getPas()=" + getPas()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
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
