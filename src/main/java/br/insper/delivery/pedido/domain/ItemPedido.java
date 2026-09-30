package br.insper.delivery.pedido.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um item de um pedido.
 */
@Entity
public class ItemPedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long pedidoId;

	private Long produtoId;

	private Integer quantidade;

	private BigDecimal precoUnitario;

	protected ItemPedido() {
	}

	/**
	 * Construtor da classe ItemPedido.
	 *
	 * @param pedidoId      ID do pedido dono do item.
	 * @param produtoId     ID do produto pedido.
	 * @param quantidade    Quantidade do produto.
	 * @param precoUnitario Preço unitário do produto no momento do pedido.
	 */
	public ItemPedido(Long pedidoId, Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
		this.pedidoId = pedidoId;
		this.produtoId = produtoId;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
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

}
