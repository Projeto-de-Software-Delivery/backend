package br.insper.delivery.pedido.event;

import org.springframework.context.ApplicationEvent;

public class EntregaAceitaEvent extends ApplicationEvent {

	private final EntregaAceitaEvento evento;

	public EntregaAceitaEvent(Object source, EntregaAceitaEvento evento) {
		super(source);
		this.evento = evento;
	}

	public EntregaAceitaEvento getEvento() {
		return evento;
	}
}
