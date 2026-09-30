package br.insper.delivery.pedido.event;

import org.springframework.context.ApplicationEvent;

public class PedidoCriadoEvent extends ApplicationEvent {

	private final PedidoCriadoEvento evento;

	public PedidoCriadoEvent(Object source, PedidoCriadoEvento evento) {
		super(source);
		this.evento = evento;
	}

	public PedidoCriadoEvento getEvento() {
		return evento;
	}
}
