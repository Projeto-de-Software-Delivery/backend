package br.insper.delivery.pedido.dto;

import java.math.BigDecimal;

import br.insper.delivery.pedido.domain.ItemPedido;

public record ItemPedidoResponse(Long produtoId, Integer quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {

	public static ItemPedidoResponse from(ItemPedido item) {
		return new ItemPedidoResponse(item.getProdutoId(), item.getQuantidade(), item.getPrecoUnitario(),
				item.getSubtotal());
	}
}
