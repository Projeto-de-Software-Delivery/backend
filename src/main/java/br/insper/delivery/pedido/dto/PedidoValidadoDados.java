package br.insper.delivery.pedido.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload do tópico pedido.validado: a loja aceitou o pedido e baixou o estoque.
 */
public record PedidoValidadoDados(
		@NotBlank String pedidoId,
		@NotBlank String lojaId,
		@NotNull @Valid EnderecoDados enderecoRetirada,
		@NotNull @Valid EnderecoDados enderecoEntrega,
		@NotNull BigDecimal valorFrete,
		@NotNull Integer tempoPreparoMin) {
}
