package br.insper.delivery.pedido.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Dados informados pela loja ao aceitar um pedido pendente: de onde o entregador deve retirar,
 * o valor do frete e o tempo estimado de preparo.
 */
public record AceitarPedidoRequest(
		@NotNull @Valid EnderecoEntregaRequest enderecoRetirada,
		@NotNull @DecimalMin(value = "0.0") BigDecimal valorFrete,
		@NotNull @Min(0) Integer tempoPreparoMin) {
}
