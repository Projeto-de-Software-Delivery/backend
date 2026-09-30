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

	/**
	 * Construtor da classe Produto.
	 *
	 * @param lojaId    ID da loja dona do produto.
	 * @param nome      Nome do produto.
	 * @param categoria Categoria do produto no catálogo.
	 * @param preco     Preço do produto.
	 * @param estoque   Quantidade em estoque do produto.
	 * @param foto      URL da foto do produto.
	 */
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

	/**
	 * Atualiza os dados do produto. A loja dona do produto não muda.
	 *
	 * @param nome      Nome do produto.
	 * @param categoria Categoria do produto no catálogo.
	 * @param preco     Preço do produto.
	 * @param estoque   Quantidade em estoque do produto.
	 * @param foto      URL da foto do produto.
	 */
	public void atualizar(String nome, String categoria, BigDecimal preco, Integer estoque, String foto) {
		this.nome = nome;
		this.categoria = categoria;
		this.preco = preco;
		this.estoque = estoque;
		this.foto = foto;
	}

	/**
	 * Baixa uma quantidade do estoque do produto.
	 *
	 * @param quantidade Quantidade a ser baixada do estoque.
	 * @throws IllegalStateException Se o estoque disponível for menor que a quantidade solicitada.
	 */
	public void baixarEstoque(Integer quantidade) {
		if (this.estoque < quantidade) {
			throw new IllegalStateException("Estoque insuficiente para o produto " + this.nome);
		}
		this.estoque -= quantidade;
	}

}
