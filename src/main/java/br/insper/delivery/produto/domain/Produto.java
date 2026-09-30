package br.insper.delivery.produto.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um produto do catálogo.
 */
@Entity
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String categoria;

	private BigDecimal preco;

	private String foto;

	protected Produto() {
	}

	/**
	 * Construtor da classe Produto.
	 *
	 * @param nome      Nome do produto.
	 * @param categoria Categoria do produto no catálogo.
	 * @param preco     Preço do produto.
	 * @param foto      URL da foto do produto.
	 */
	public Produto(String nome, String categoria, BigDecimal preco, String foto) {
		this.nome = nome;
		this.categoria = categoria;
		this.preco = preco;
		this.foto = foto;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCategoria() {
		return categoria;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public String getFoto() {
		return foto;
	}

	/**
	 * Atualiza os dados do produto.
	 *
	 * @param nome      Nome do produto.
	 * @param categoria Categoria do produto no catálogo.
	 * @param preco     Preço do produto.
	 * @param foto      URL da foto do produto.
	 */
	public void atualizar(String nome, String categoria, BigDecimal preco, String foto) {
		this.nome = nome;
		this.categoria = categoria;
		this.preco = preco;
		this.foto = foto;
	}

}
