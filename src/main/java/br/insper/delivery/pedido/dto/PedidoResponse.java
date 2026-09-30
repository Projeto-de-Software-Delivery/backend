package br.insper.delivery.pedido.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.domain.PedidoStatus;

public record PedidoResponse(Long id, Long clienteId, Long lojaId, BigDecimal valorTotal, PedidoStatus status,
		Instant dataCriacao) {

	public static PedidoResponse from(Pedido pedido) {
		return new PedidoResponse(pedido.getId(), pedido.getClienteId(), pedido.getLojaId(), pedido.getValorTotal(),
				pedido.getStatus(), pedido.getDataCriacao());
	}
}
