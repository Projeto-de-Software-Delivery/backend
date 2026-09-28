package br.insper.delivery.loja.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.loja.domain.Loja;

public class LojaCriadaEvent extends ApplicationEvent {

	private final Loja loja;

	public LojaCriadaEvent(Object source, Loja loja) {
		super(source);
		this.loja = loja;
	}

	public Loja getLoja() {
		return loja;
	}
}
