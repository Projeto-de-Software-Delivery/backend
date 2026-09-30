package br.insper.delivery.produto.dto;

import java.math.BigDecimal;

import br.insper.delivery.produto.domain.Produto;

public record ProdutoResponse(Long id, Long lojaId, String nome, String categoria, BigDecimal preco, Integer estoque,
		String foto) {

	public static ProdutoResponse from(Produto produto) {
		return new ProdutoResponse(produto.getId(), produto.getLojaId(), produto.getNome(), produto.getCategoria(),
				produto.getPreco(), produto.getEstoque(), produto.getFoto());
	}
}
