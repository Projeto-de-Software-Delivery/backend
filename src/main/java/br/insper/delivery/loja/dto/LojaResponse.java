package br.insper.delivery.loja.dto;

import br.insper.delivery.loja.domain.Loja;

public record LojaResponse(Long id, String nome, String cnpj, String endereco) {

	public static LojaResponse from(Loja loja) {
		return new LojaResponse(loja.getId(), loja.getNome(), loja.getCnpj(), loja.getEndereco());
	}
}
