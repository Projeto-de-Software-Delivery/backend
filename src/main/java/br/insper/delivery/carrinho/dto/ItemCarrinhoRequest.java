package br.insper.delivery.carrinho.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemCarrinhoRequest(
		@NotNull Long produtoId,
		@NotNull @Positive Integer quantidade) {
}
