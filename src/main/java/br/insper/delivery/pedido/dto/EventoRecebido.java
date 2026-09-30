package br.insper.delivery.pedido.dto;

import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Envelope de um evento recebido de outro serviço, no formato descrito em payloads.md.
 *
 * @param <T> Tipo do payload em {@code data}.
 */
public record EventoRecebido<T>(
		@NotBlank String eventId,
		@NotBlank String eventType,
		@NotNull Integer version,
		@NotNull Instant occurredAt,
		@NotNull @Valid T data) {
}
