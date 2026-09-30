package br.insper.delivery.entregador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EntregadorRequest(
		@NotBlank String nome,
		@NotBlank @Pattern(regexp = "\\d{11}") String cpf,
		@NotBlank String telefone,
		@NotBlank String placa) {
}
