package br.insper.delivery.loja.dto;

import br.insper.delivery.loja.domain.EstoqueLoja;

public record EstoqueLojaResponse(Long id, Long lojaId, Long produtoId, Integer quantidade) {

	public static EstoqueLojaResponse from(EstoqueLoja estoque) {
		return new EstoqueLojaResponse(
				estoque.getId(),
				estoque.getLojaId(),
				estoque.getProdutoId(),
				estoque.getQuantidade());
	}
}
