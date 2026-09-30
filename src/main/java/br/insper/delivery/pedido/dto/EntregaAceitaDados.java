package br.insper.delivery.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload do tópico entrega.aceita: um entregador pegou a corrida.
 */
public record EntregaAceitaDados(
		@NotBlank String pedidoId,
		@NotBlank String entregaId,
		@NotBlank String entregadorId,
		@NotBlank String nomeEntregador,
		@NotBlank String veiculo,
		@NotNull Integer etaRetiradaMin) {
}
