package br.insper.delivery.pedido.dto;

import br.insper.delivery.pedido.domain.PedidoStatus;

public record PedidoStatusResponse(Long id, PedidoStatus status) {
}
