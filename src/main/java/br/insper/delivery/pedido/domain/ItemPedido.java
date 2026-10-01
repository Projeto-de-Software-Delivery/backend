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

	public BigDecimal getSubtotal() {
		return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
	}

}
