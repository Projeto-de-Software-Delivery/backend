package br.insper.delivery.pedido.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import br.insper.delivery.pedido.domain.PedidoStatus;

public record PedidoResponse(Long id, Long clienteId, Long lojaId, List<ItemPedidoResponse> itens, BigDecimal total,
		EnderecoEntregaResponse enderecoEntrega, PedidoStatus status, Instant dataCriacao) {
}
