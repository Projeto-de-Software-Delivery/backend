package br.insper.delivery.pedido.event;

import java.math.BigDecimal;
import java.util.List;

public record PedidoCriadoDados(String pedidoId, String clienteId, String lojaId, List<ItemPedidoDados> itens,
		BigDecimal total, EnderecoEntregaDados enderecoEntrega) {
}
