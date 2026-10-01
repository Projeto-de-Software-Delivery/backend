package br.insper.delivery.pedido.event;

import org.springframework.context.ApplicationEvent;

public class PedidoValidadoEvent extends ApplicationEvent {

	private final PedidoValidadoEvento evento;

	public PedidoValidadoEvent(Object source, PedidoValidadoEvento evento) {
		super(source);
		this.evento = evento;
	}

	public PedidoValidadoEvento getEvento() {
		return evento;
	}
}
