package br.com.sankhya.jaco.integracao.dao;



import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class Parceiro {

	//@SerializedName(value = "IdCliente", alternate="IdPessoa")
	@SerializedName(value = "IdCliente")
	private BigDecimal idPessoa;
	
	@SerializedName(value = "IsOk")
	private boolean isOk;
	
	@SerializedName(value = "NomePessoa")
	private String nomePessoa;
	
	@SerializedName(value = "CpfCnpj")
	private String cpfCnpj;
	
	@SerializedName(value = "TipoPessoa")
	private String tipoPessoa;

	@SerializedName(value = "Cep")
	private String cep;
	
	@SerializedName(value = "Complemento")
	private String complemento;
	
	@SerializedName(value = "Logradouro")
	private String logradouro;
	
	@SerializedName(value = "Cidade")
	private String cidade;
	
	@SerializedName(value = "Bairro")
	private String bairro;
	
	@SerializedName(value = "NomeEstado")
	private String nomeEstado;
	
	@SerializedName(value = "NumeroContratoCliente")
	private String numeroContratoCliente;
	

	public BigDecimal getIdPessoa() {
		return idPessoa;
	}

	public String isOk() {
		if (isOk) {
			return "1"; 
		} 
		return "0";
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public String getCep() {
		return cep;
	}

	public String getComplemento() {
		return complemento;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public String getCidade() {
		return cidade;
	}

	public String getBairro() {
		return bairro;
	}

	public String getNomeEstado() {
		return nomeEstado;
	}

	public String getNumeroContratoCliente() {
		return numeroContratoCliente;
	}


}
		


