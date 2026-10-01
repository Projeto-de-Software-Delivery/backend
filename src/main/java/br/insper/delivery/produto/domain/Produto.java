package br.insper.delivery.produto.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um produto do catálogo de uma loja.
 */
@Entity
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long lojaId;

	private String nome;

	private String categoria;

	private BigDecimal preco;

	private Integer estoque;

	private String foto;

	protected Produto() {
	}

	public Produto(Long lojaId, String nome, String categoria, BigDecimal preco, Integer estoque, String foto) {
		this.lojaId = lojaId;
		this.nome = nome;
		this.categoria = categoria;
		this.preco = preco;
		this.estoque = estoque;
		this.foto = foto;
	}

	public Long getId() {
		return id;
	}

	public Long getLojaId() {
		return lojaId;
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

	public Integer getEstoque() {
		return estoque;
	}

	public String getFoto() {
		return foto;
	}

	/** A loja dona do produto não muda. */
	public void atualizar(String nome, String categoria, BigDecimal preco, Integer estoque, String foto) {
		this.nome = nome;
		this.categoria = categoria;
		this.preco = preco;
		this.estoque = estoque;
		this.foto = foto;
	}

	/** @throws IllegalStateException se o estoque disponível for menor que a quantidade. */
	public void baixarEstoque(Integer quantidade) {
		if (this.estoque < quantidade) {
			throw new IllegalStateException("Estoque insuficiente para o produto " + this.nome);
		}
		this.estoque -= quantidade;
	}

}
