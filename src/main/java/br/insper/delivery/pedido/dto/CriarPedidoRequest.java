package br.insper.delivery.pedido.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CriarPedidoRequest(
		@NotNull Long lojaId,
		@NotEmpty @Valid List<ItemPedidoRequest> itens,
		@NotNull @Valid EnderecoEntregaRequest enderecoEntrega) {
}
