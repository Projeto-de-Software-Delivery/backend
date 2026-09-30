package br.insper.delivery.carrinho.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarrinhoResponse(Long clienteId, List<ItemCarrinhoResponse> itens, BigDecimal total) {
}
