package br.insper.delivery.pedido.event;

import java.time.Instant;
import java.util.UUID;

import br.insper.delivery.pedido.dto.PedidoValidadoDados;

/**
 * Envelope do evento publicado no tópico pedido.validado, conforme o contrato definido em
 * payloads.md.
 */
public record PedidoValidadoEvento(String eventId, String eventType, int version, Instant occurredAt,
		PedidoValidadoDados data) {

	public static PedidoValidadoEvento de(PedidoValidadoDados dados) {
		return new PedidoValidadoEvento(UUID.randomUUID().toString(), "pedido.validado", 1, Instant.now(), dados);
	}
}
