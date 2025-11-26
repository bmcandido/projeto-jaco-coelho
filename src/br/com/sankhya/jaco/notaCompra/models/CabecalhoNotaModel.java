package br.com.sankhya.jaco.notaCompra.models;

import java.math.BigDecimal;
import java.util.Objects;

import com.sankhya.util.BigDecimalUtil;

public class CabecalhoNotaModel {

    // Campos obrigatórios (final)
    private final BigDecimal codEmp;
    private final BigDecimal codCenCus;
    private final BigDecimal codParc;
    private final BigDecimal codNat;
    private final BigDecimal codProj;
    private final BigDecimal codTipOper;
    private final String tipMov;
    private final BigDecimal tipoNegociacao;
    private final BigDecimal numeroRequisicao;

    // Campo opcional (final)
    private final String observacao;
    private final BigDecimal numNota;
    private final String serie;
    private final String chaveNfe;

    // Construtor privado para uso com Builder
    private CabecalhoNotaModel(Builder builder) {
        this.codEmp = builder.codEmp;
        this.codCenCus = builder.codCenCus;
        this.codParc = builder.codParc;
        this.codNat = builder.codNat;
        this.codProj = builder.codProj;
        this.codTipOper = builder.codTipOper;
        this.tipMov = builder.tipMov;
        this.tipoNegociacao = builder.tipoNegociacao;
        this.observacao = builder.observacao;
        this.numNota = builder.numNota;
        this.serie = builder.serie;
        this.chaveNfe = builder.chaveNfe;
        this.numeroRequisicao = builder.numeroRequisicao;
    }

    // Builder class
    public static class Builder {
        // Campos obrigatórios
        private BigDecimal codEmp;
        private BigDecimal codCenCus;
        private BigDecimal codParc;
        private BigDecimal codNat;
        private BigDecimal codProj;
        private BigDecimal codTipOper;
        private String tipMov;
        private BigDecimal tipoNegociacao;
        private BigDecimal numeroRequisicao;

        // Campo opcional
        private String observacao = "";
        private BigDecimal numNota = null;
        private String serie = null;
        private String chaveNfe = "";

        public Builder codEmp(BigDecimal codEmp) {
            this.codEmp = validarNaoNulo(codEmp, "codEmp");
            return this;
        }

        public Builder codCenCus(BigDecimal codCenCus) {
            this.codCenCus = validarNaoNulo(codCenCus, "codCenCus");
            return this;
        }

        public Builder codParc(BigDecimal codParc) {
            this.codParc = validarNaoNulo(codParc, "codparc");
            return this;
        }

        public Builder codNat(BigDecimal codNat) {
            this.codNat = validarNaoNulo(codNat, "codnat");
            return this;
        }

        public Builder codProj(BigDecimal codProj) {
            this.codProj = validarNaoNulo(codProj, "codProj");
            return this;
        }

        public Builder codTipOper(BigDecimal codTipOper) {
            this.codTipOper = validarNaoNulo(codTipOper, "codTipOper");
            return this;
        }

        public Builder tipMov(String tipMov) {
            this.tipMov = validarNaoNulo(tipMov, "tipMov");
            return this;
        }

        public Builder tipoNegociacao(BigDecimal tipoNegociacao) {
            this.tipoNegociacao = validarNaoNulo(tipoNegociacao, "tipoNegociacao");
            return this;
        }

        public Builder observacao(String observacao) {
            this.observacao = observacao != null ? observacao : "";
            return this;
        }

        public Builder numNota(BigDecimal numNota) {
            this.numNota = numNota != null ? numNota : BigDecimal.ZERO;
            return this;
        }

        public Builder serie(String serie) {
            this.serie = serie != null ? serie : "";
            return this;
        }

        public Builder chaveNfe(String chaveNfe) {
            this.chaveNfe = chaveNfe != null ? chaveNfe : "";
            return this;
        }

        public Builder numeroRequisicao(BigDecimal numeroRequisicao) {
            this.numeroRequisicao = numeroRequisicao != null ? numeroRequisicao : BigDecimal.ZERO;
            return this;
        }

        public CabecalhoNotaModel build() {
            validarCamposObrigatorios();
            return new CabecalhoNotaModel(this);
        }

        private void validarCamposObrigatorios() {
            validarNaoNulo(codEmp, "codEmp");
            validarNaoNulo(codCenCus, "codCenCus");
            validarNaoNulo(codParc, "codparc");
            validarNaoNulo(codNat, "codnat");
            validarNaoNulo(codProj, "codProj");
            validarNaoNulo(codTipOper, "codTipOper");
            validarNaoNulo(tipMov, "tipMov");
            validarNaoNulo(tipoNegociacao, "tipoNegociacao");
        }

        private BigDecimal validarNaoNulo(BigDecimal valor, String nomeCampo) {
            return Objects.requireNonNull(valor, nomeCampo + " não pode ser nulo");
        }

        private String validarNaoNulo(String valor, String nomeCampo) {
            return Objects.requireNonNull(valor, nomeCampo + " não pode ser nulo");
        }
    }

    // Getters
    public BigDecimal getCodEmp() {
        return codEmp;
    }

    public BigDecimal getCodCenCus() {
        return codCenCus != null ? codCenCus : BigDecimalUtil.ZERO_VALUE;
    }

    public BigDecimal getCodParc() {
        return codParc;
    }

    public BigDecimal getCodNat() {
        return codNat != null ? codNat : BigDecimalUtil.ZERO_VALUE;
    }

    public BigDecimal getCodProj() {
        return codProj != null ? codProj : BigDecimalUtil.ZERO_VALUE;
    }

    public BigDecimal getCodTipOper() {
        return codTipOper;
    }

    public String getTipMov() {
        return tipMov;
    }

    public BigDecimal getTipoNegociacao() {
        return tipoNegociacao;
    }

    public String getObservacao() {
        return observacao != null ? observacao : "";
    }

    public BigDecimal getNumNota() {
        return numNota != null ? numNota : BigDecimalUtil.ZERO_VALUE;
    }

    public String getSerie() {
        return serie != null ? serie : "";
    }

    public String getchaveNfe() {
        return chaveNfe != null ? chaveNfe : "";
    }

    public BigDecimal getnumeroRequisicao() {
        return numeroRequisicao != null ? numeroRequisicao : BigDecimalUtil.ZERO_VALUE;
    }

    @Override
    public String toString() {
        return "CabecalhoNotaModel{" + "codEmp=" + codEmp + ", codCenCus=" + codCenCus + ", codparc=" + codParc
                + ", codnat=" + codNat + ", codProj=" + codProj + ", codTipOper=" + codTipOper + ", tipMov='" + tipMov
                + '\'' + ", tipoNegociacao=" + tipoNegociacao + ", observacao='" + observacao + '\'' + '}';
    }
}