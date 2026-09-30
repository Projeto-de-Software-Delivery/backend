package br.insper.delivery.entregador.dto;

import br.insper.delivery.entregador.domain.Entregador;
import br.insper.delivery.entregador.domain.StatusDisponibilidade;

public record EntregadorResponse(Long id, String nome, String cpf, String telefone, String placa,
		StatusDisponibilidade status) {

	public static EntregadorResponse from(Entregador entregador) {
		return new EntregadorResponse(entregador.getId(), entregador.getNome(), entregador.getCpf(),
				entregador.getTelefone(), entregador.getPlaca(), entregador.getStatus());
	}
}
