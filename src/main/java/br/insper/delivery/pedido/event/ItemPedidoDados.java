package br.insper.delivery.pedido.event;

import java.math.BigDecimal;

public record ItemPedidoDados(String produtoId, Integer quantidade, BigDecimal precoUnitario) {
}
