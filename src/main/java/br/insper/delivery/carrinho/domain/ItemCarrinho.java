package br.insper.delivery.carrinho.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um item no carrinho de um cliente.
 */
@Entity
public class ItemCarrinho {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long clienteId;

	private Long produtoId;

	private Integer quantidade;

	private BigDecimal precoUnitario;

	protected ItemCarrinho() {
	}

	/**
	 * Construtor da classe ItemCarrinho.
	 *
	 * @param clienteId     ID do cliente dono do carrinho.
	 * @param produtoId     ID do produto adicionado.
	 * @param quantidade    Quantidade do produto.
	 * @param precoUnitario Preço unitário do produto no momento da adição.
	 */
	public ItemCarrinho(Long clienteId, Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
		this.clienteId = clienteId;
		this.produtoId = produtoId;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
	}

	public Long getId() {
		return id;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public BigDecimal getPrecoUnitario() {
		return precoUnitario;
	}

	/**
	 * Calcula o subtotal do item (quantidade x preço unitário).
	 *
	 * @return Subtotal do item.
	 */
	public BigDecimal getSubtotal() {
		return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
	}

	/**
	 * Acrescenta quantidade a um item já existente no carrinho, atualizando o preço unitário para o
	 * preço atual do produto.
	 *
	 * @param quantidade    Quantidade a ser acrescentada.
	 * @param precoUnitario Preço unitário atual do produto.
	 */
	public void adicionarQuantidade(Integer quantidade, BigDecimal precoUnitario) {
		this.quantidade += quantidade;
		this.precoUnitario = precoUnitario;
	}

}
