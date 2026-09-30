package br.insper.delivery.carrinho.dto;

import java.math.BigDecimal;

import br.insper.delivery.carrinho.domain.ItemCarrinho;

public record ItemCarrinhoResponse(Long id, Long produtoId, Integer quantidade, BigDecimal precoUnitario,
		BigDecimal subtotal) {

	public static ItemCarrinhoResponse from(ItemCarrinho item) {
		return new ItemCarrinhoResponse(item.getId(), item.getProdutoId(), item.getQuantidade(),
				item.getPrecoUnitario(), item.getSubtotal());
	}
}
