package br.insper.delivery.endereco.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa o endereço de um cliente.
 */
@Entity
public class Endereco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long clienteId;

	private String cep;

	private String logradouro;

	private String numero;

	private String complemento;

	private String bairro;

	private String cidade;

	private String estado;

	protected Endereco() {
	}

	/**
	 * Construtor da classe Endereco.
	 *
	 * @param clienteId   ID do cliente dono do endereço.
	 * @param cep         CEP do endereço.
	 * @param logradouro  Logradouro do endereço.
	 * @param numero      Número do endereço.
	 * @param complemento Complemento do endereço.
	 * @param bairro      Bairro do endereço.
	 * @param cidade      Cidade do endereço.
	 * @param estado      Estado (UF) do endereço.
	 */
	public Endereco(Long clienteId, String cep, String logradouro, String numero, String complemento, String bairro,
			String cidade, String estado) {
		this.clienteId = clienteId;
		this.cep = cep;
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.bairro = bairro;
		this.cidade = cidade;
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public String getCep() {
		return cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public String getEstado() {
		return estado;
	}

	/**
	 * Atualiza os dados do endereço.
	 *
	 * @param cep         CEP do endereço.
	 * @param logradouro  Logradouro do endereço.
	 * @param numero      Número do endereço.
	 * @param complemento Complemento do endereço.
	 * @param bairro      Bairro do endereço.
	 * @param cidade      Cidade do endereço.
	 * @param estado      Estado (UF) do endereço.
	 */
	public void atualizar(String cep, String logradouro, String numero, String complemento, String bairro,
			String cidade, String estado) {
		this.cep = cep;
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.bairro = bairro;
		this.cidade = cidade;
		this.estado = estado;
	}

}
