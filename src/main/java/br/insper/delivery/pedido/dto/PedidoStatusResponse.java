package br.insper.delivery.pedido.dto;

import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.domain.PedidoStatus;

public record PedidoStatusResponse(Long id, PedidoStatus status) {

	public static PedidoStatusResponse from(Pedido pedido) {
		return new PedidoStatusResponse(pedido.getId(), pedido.getStatus());
	}
}
