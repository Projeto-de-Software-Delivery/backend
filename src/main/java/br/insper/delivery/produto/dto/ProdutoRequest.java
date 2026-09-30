package br.insper.delivery.produto.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequest(
		@NotBlank String nome,
		@NotBlank String categoria,
		@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal preco,
		@NotBlank String foto) {
}
