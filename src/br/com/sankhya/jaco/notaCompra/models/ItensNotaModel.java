package br.com.sankhya.jaco.notaCompra.models;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


public class ItensNotaModel {

    // Campos obrigatórios (final)
    private final BigDecimal vlrunit;
    private final BigDecimal qtdNegociada;
    private final BigDecimal codProd;
    private final BigDecimal codLocal;
    private final String controle;
    private final String codVolume;


    // Campos opcionais (final)
    private final BigDecimal percentualDesconto;
    private final BigDecimal vlrDesconto;
    private final BigDecimal codLocalDestino;
    private final BigDecimal sequencia;
    private final BigDecimal codemp;
    private final BigDecimal vlrTotal;

    // Construtor privado para uso com Builder
    private ItensNotaModel(Builder builder) {
        this.vlrunit = builder.vlrunit;
        this.qtdNegociada = builder.qtdNegociada;
        this.codProd = builder.codProd;
        this.codLocal = builder.codLocal;
        this.controle = builder.controle;
        this.codVolume = builder.codVolume;
        this.percentualDesconto = builder.percentualDesconto;
        this.vlrDesconto = builder.vlrDesconto;
        this.codLocalDestino = builder.codLocalDestino;
        this.sequencia = builder.sequencia;
        this.codemp = builder.codemp;
        this.vlrTotal = builder.vlrTotal;
    }

    // Builder class
    public static class Builder {
        // Campos obrigatórios
        private BigDecimal vlrunit;
        private BigDecimal qtdNegociada;
        private BigDecimal codProd;
        private BigDecimal codLocal;
        private String controle;
        private String codVolume;

        // Campos opcionais com valores padrão
        private BigDecimal percentualDesconto = BigDecimal.ZERO;
        private BigDecimal vlrDesconto = BigDecimal.ZERO;
        private BigDecimal codLocalDestino = null;
        private BigDecimal sequencia = null;
        private BigDecimal codemp = null;
        private BigDecimal vlrTotal = null;

        public Builder vlrunit(BigDecimal vlrunit) {
            this.vlrunit = validarPositivo(vlrunit, "vlrunit");
            return this;
        }

        public Builder qtdNegociada(BigDecimal qtdNegociada) {
            this.qtdNegociada = validarPositivo(qtdNegociada, "qtdNegociada");
            return this;
        }

        public Builder codProd(BigDecimal codProd) {
            this.codProd = validarNaoNulo(codProd, "codProd");
            return this;
        }

        public Builder codLocal(BigDecimal codLocal) {
            this.codLocal = validarNaoNulo(codLocal, "codLocal");
            return this;
        }

        public Builder controle(String controle) {
            this.controle = controle != null ? controle : "";
            return this;
        }

        public Builder codVolume(String codVolume) {
            this.codVolume = validarNaoNulo(codVolume, "codVolume");
            return this;
        }

        public Builder percentualDesconto(BigDecimal percentualDesconto) {
            this.percentualDesconto = validarNaoNegativo(percentualDesconto, "percentualDesconto");
            return this;
        }

        public Builder vlrDesconto(BigDecimal vlrDesconto) {
            this.vlrDesconto = validarNaoNegativo(vlrDesconto, "vlrDesconto");
            return this;
        }

        public Builder codLocalDestino(BigDecimal codLocalDestino) {
            this.codLocalDestino = codLocalDestino;
            return this;
        }

        public Builder sequencia(BigDecimal sequencia) {
            this.sequencia = sequencia;
            return this;
        }

        public Builder codemp(BigDecimal codemp) {
            this.codemp = codemp;
            return this;
        }

        public Builder vlrTotal(BigDecimal vlrTotal) {
            this.vlrTotal = vlrTotal;
            return this;
        }

        public ItensNotaModel build() {
            validarCamposObrigatorios();
            return new ItensNotaModel(this);
        }

        private void validarCamposObrigatorios() {
            validarNaoNulo(vlrunit, "vlrunit");
            validarNaoNulo(qtdNegociada, "qtdNegociada");
            validarNaoNulo(codProd, "codProd");
            validarNaoNulo(codLocal, "codLocal");
            validarNaoNulo(codVolume, "codVolume");
            // Controle pode ser nulo (já tratado no método)
        }

        private BigDecimal validarNaoNulo(BigDecimal valor, String nomeCampo) {
            return Objects.requireNonNull(valor, nomeCampo + " não pode ser nulo");
        }

        private String validarNaoNulo(String valor, String nomeCampo) {
            return Objects.requireNonNull(valor, nomeCampo + " não pode ser nulo");
        }

        private BigDecimal validarPositivo(BigDecimal valor, String nomeCampo) {
            validarNaoNulo(valor, nomeCampo);
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(nomeCampo + " não pode ser negativo");
            }
            return valor;
        }

        private BigDecimal validarNaoNegativo(BigDecimal valor, String nomeCampo) {
            if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(nomeCampo + " não pode ser negativo");
            }
            return valor != null ? valor : BigDecimal.ZERO;
        }
    }

    // Getters
    public BigDecimal getVlrunit() {
        return vlrunit;
    }

    public BigDecimal getQtdNegociada() {
        return qtdNegociada;
    }

    public BigDecimal getCodProd() {
        return codProd;
    }

    public BigDecimal getCodLocal() {
        return codLocal;
    }

    public String getControle() {
        return controle;
    }

    public String getCodVolume() {
        return codVolume;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public BigDecimal getVlrDesconto() {
        return vlrDesconto;
    }

    public BigDecimal getCodLocalDestino() {
        return codLocalDestino;
    }

    public BigDecimal getSequencia() {
        return sequencia;
    }

    // Métodos utilitários
    public BigDecimal getVlrTotalFormula() {
        return vlrunit.multiply(qtdNegociada).subtract(getVlrDescontoEfetivo());
    }

    public BigDecimal getVlrTotal(){return vlrTotal;}

    public BigDecimal getCodemp() {
        return codemp;
    }

    public BigDecimal getVlrDescontoEfetivo() {
        if (vlrDesconto.compareTo(BigDecimal.ZERO) > 0) {
            return vlrDesconto;
        }
        if (percentualDesconto.compareTo(BigDecimal.ZERO) > 0) {
            return vlrunit.multiply(qtdNegociada)
                         .multiply(percentualDesconto)
                         .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ItensNotaModel{" +
                "vlrunit=" + vlrunit +
                ", qtdNegociada=" + qtdNegociada +
                ", codProd=" + codProd +
                ", codLocal=" + codLocal +
                ", controle='" + controle + '\'' +
                ", codVolume='" + codVolume + '\'' +
                ", percentualDesconto=" + percentualDesconto +
                ", vlrDesconto=" + vlrDesconto +
                ", codLocalDestino=" + codLocalDestino +
                ", sequencia=" + sequencia +
                ", codemp=" + codemp +
                '}';
    }
}