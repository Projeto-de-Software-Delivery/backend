package br.insper.delivery.loja.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa uma loja.
 */
@Entity
public class Loja {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String cnpj;

	private String endereco;

	protected Loja() {
	}

	/**
	 * Construtor da classe Loja.
	 *
	 * @param nome     Nome da loja.
	 * @param cnpj     CNPJ da loja.
	 * @param endereco Endereço da loja.
	 */
	public Loja(String nome, String cnpj, String endereco) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.endereco = endereco;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCnpj() {
		return cnpj;
	}

	public String getEndereco() {
		return endereco;
	}

	/**
	 * Atualiza os dados da loja.
	 *
	 * @param nome     Nome da loja.
	 * @param cnpj     CNPJ da loja.
	 * @param endereco Endereço da loja.
	 */
	public void atualizar(String nome, String cnpj, String endereco) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.endereco = endereco;
	}

}
