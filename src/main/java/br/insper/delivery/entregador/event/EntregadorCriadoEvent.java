package br.insper.delivery.entregador.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.entregador.domain.Entregador;

public class EntregadorCriadoEvent extends ApplicationEvent {

	private final Entregador entregador;

	public EntregadorCriadoEvent(Object source, Entregador entregador) {
		super(source);
		this.entregador = entregador;
	}

	public Entregador getEntregador() {
		return entregador;
	}
}
