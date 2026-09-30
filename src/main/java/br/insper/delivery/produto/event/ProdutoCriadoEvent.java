package br.insper.delivery.produto.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.produto.domain.Produto;

public class ProdutoCriadoEvent extends ApplicationEvent {

	private final Produto produto;

	public ProdutoCriadoEvent(Object source, Produto produto) {
		super(source);
		this.produto = produto;
	}

	public Produto getProduto() {
		return produto;
	}
}
