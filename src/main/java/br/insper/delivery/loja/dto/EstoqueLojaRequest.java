package br.insper.delivery.loja.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EstoqueLojaRequest(
		@NotNull Long produtoId,
		@NotNull @Min(0) Integer quantidade) {
}
