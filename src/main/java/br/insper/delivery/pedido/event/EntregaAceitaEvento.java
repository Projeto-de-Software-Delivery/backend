package br.insper.delivery.pedido.event;

import java.time.Instant;
import java.util.UUID;

import br.insper.delivery.pedido.dto.EntregaAceitaDados;

/**
 * Envelope do evento publicado no tópico entrega.aceita, conforme o contrato definido em
 * payloads.md.
 */
public record EntregaAceitaEvento(String eventId, String eventType, int version, Instant occurredAt,
		EntregaAceitaDados data) {

	public static EntregaAceitaEvento de(EntregaAceitaDados dados) {
		return new EntregaAceitaEvento(UUID.randomUUID().toString(), "entrega.aceita", 1, Instant.now(), dados);
	}
}
