package br.insper.delivery.entregador.dto;

import br.insper.delivery.entregador.domain.StatusDisponibilidade;
import jakarta.validation.constraints.NotNull;

public record StatusDisponibilidadeRequest(@NotNull StatusDisponibilidade status) {
}
