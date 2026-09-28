package br.insper.delivery.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LojaRequest(
		@NotBlank String nome,
		@NotBlank @Pattern(regexp = "\\d{14}") String cnpj,
		@NotBlank String endereco) {
}
