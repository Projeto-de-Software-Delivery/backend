package br.insper.delivery.cliente.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.cliente.domain.Cliente;

public class ClienteCriadoEvent extends ApplicationEvent {

	private final Cliente cliente;

	public ClienteCriadoEvent(Object source, Cliente cliente) {
		super(source);
		this.cliente = cliente;
	}

	public Cliente getCliente() {
		return cliente;
	}
}
