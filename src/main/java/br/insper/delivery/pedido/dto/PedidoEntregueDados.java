package br.insper.delivery.pedido.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload do tópico pedido.entregue: PIN validado e entrega finalizada.
 */
public record PedidoEntregueDados(
		@NotBlank String pedidoId,
		@NotBlank String entregaId,
		@NotBlank String entregadorId,
		@NotNull Instant entregueEm,
		@NotNull Boolean pinValidado) {
}
