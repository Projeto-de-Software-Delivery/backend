package br.insper.delivery.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnderecoDados(
		@NotBlank String rua,
		@NotNull Double lat,
		@NotNull Double lng) {
}
