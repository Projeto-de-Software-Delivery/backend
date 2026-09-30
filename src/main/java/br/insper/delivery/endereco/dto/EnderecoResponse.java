package br.insper.delivery.endereco.dto;

import br.insper.delivery.endereco.domain.Endereco;

public record EnderecoResponse(Long id, Long clienteId, String cep, String logradouro, String numero,
		String complemento, String bairro, String cidade, String estado) {

	public static EnderecoResponse from(Endereco endereco) {
		return new EnderecoResponse(endereco.getId(), endereco.getClienteId(), endereco.getCep(),
				endereco.getLogradouro(), endereco.getNumero(), endereco.getComplemento(), endereco.getBairro(),
				endereco.getCidade(), endereco.getEstado());
	}
}
