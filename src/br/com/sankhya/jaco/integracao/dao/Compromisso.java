package br.com.sankhya.jaco.integracao.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Compromisso {


    @SerializedName(value = "IdCompromisso")
    private BigDecimal idCompromisso;

    @SerializedName(value = "IdSubtipoCompromisso")
    private BigDecimal idSubtipoCompromisso;

    @SerializedName(value = "DataCompromisso")
    private String dataCompromisso;

    @SerializedName(value = "HoraCompromisso")
    private String horaCompromisso;

    @SerializedName(value = "DataHoraConclusao")
    private String dataHoraConclusao;

    @SerializedName(value = "NomeTipoCompromisso")
    private String nomeTipoCompromisso;

    @SerializedName(value = "NomeSubTipoCompromisso")
    private String nomeSubTipoCompromisso;

    @SerializedName(value = "NomeResponsavel")
    private String nomeResponsavel;

    @SerializedName(value = "IsAutomatico")
    private boolean isAutomatico;

    @SerializedName(value = "NomeResponsavelProtocolo")
    private String nomeResponsavelProtocolo;

    @SerializedName(value = "ProtocoloAtual")
    private String protocoloAtual;

    @SerializedName(value = "Status")
    private String status;

    //Novos
    @SerializedName(value = "TipoFatoGerador")
    private String tipoFatoGerador;

    @SerializedName(value = "DataFatoGerador")
    private String dataFatoGerador;

    @SerializedName(value = "AdvogadoCompAudienciaName")
    private String advogadoCompAudienciaName;

    @SerializedName(value = "IsAdvogadoCumpriuOrientacoes")
    private boolean isAdvogadoCumpriuOrientacoes;

    @SerializedName(value = "PrepostoCompAudienciaEnumName")
    private String prepostoCompAudienciaEnumName;

    @SerializedName(value = "IsAudienciaRealizadaOnline")
    private boolean isAudienciaRealizadaOnline;
    
    @SerializedName(value = "FormatoAudiencia")
    private String formatoAudiencia;



    public BigDecimal getIdCompromisso() {
        return idCompromisso;
    }

    public Timestamp getDataCompromisso() {
        Timestamp timeStampDate = stringToTimestamp(dataCompromisso);
        return timeStampDate;
    }


    public Timestamp getDataHoraConclusao() {
        Timestamp timeStampDate = stringToTimestamp(dataHoraConclusao);
        return timeStampDate;
    }

    public String getNomeTipoCompromisso() {
        return nomeTipoCompromisso;
    }

    public String getNomeSubTipoCompromisso() {
        return nomeSubTipoCompromisso;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public String isAutomatico() {
        if (isAutomatico) {
            return "1";
        }
        return "0";
    }

    public String getNomeResponsavelProtocolo() {
        return nomeResponsavelProtocolo;
    }

    public String getProtocoloAtual() {
        return protocoloAtual;
    }

    public String getStatus() {
        return status;
    }

    //Novos
    public BigDecimal getIdSubtipoCompromisso() {
        return idSubtipoCompromisso;
    }

    public String getTipoFatoGerador() {
        return tipoFatoGerador;
    }

    public Timestamp getDataFatoGerador() {

        Timestamp timeStampDate = stringToTimestamp(dataFatoGerador);
        return timeStampDate;

    }

    public String getAdvogadoCompAudienciaName() {
        return advogadoCompAudienciaName;
    }

    public String isAdvogadoCumpriuOrientacoes() {

        if (isAdvogadoCumpriuOrientacoes) {
            return "1";
        }
        return "0";

    }

    public String getPrepostoCompAudienciaEnumName() {
        return prepostoCompAudienciaEnumName;
    }

    public String isAudienciaRealizadaOnline() {

        if (isAudienciaRealizadaOnline) {
            return "1";
        }
        return "0";
    }
    
    


    public String getFormatoAudiencia() {
		return formatoAudiencia;
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

