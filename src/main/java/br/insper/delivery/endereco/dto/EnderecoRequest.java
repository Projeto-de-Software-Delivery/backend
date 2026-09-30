package br.insper.delivery.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequest(
		@NotBlank @Pattern(regexp = "\\d{8}") String cep,
		@NotBlank String logradouro,
		@NotBlank String numero,
		String complemento,
		@NotBlank String bairro,
		@NotBlank String cidade,
		@NotBlank @Pattern(regexp = "[A-Za-z]{2}") String estado) {
}
