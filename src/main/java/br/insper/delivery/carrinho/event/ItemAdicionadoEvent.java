package br.insper.delivery.carrinho.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.carrinho.domain.ItemCarrinho;

public class ItemAdicionadoEvent extends ApplicationEvent {

	private final ItemCarrinho item;

	public ItemAdicionadoEvent(Object source, ItemCarrinho item) {
		super(source);
		this.item = item;
	}

	public ItemCarrinho getItem() {
		return item;
	}
}
