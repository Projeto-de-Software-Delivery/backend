package br.insper.delivery.cliente.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.cliente.domain.Cliente;

/**
 * Evento lançado quando um cliente é criado.
 */
public class ClienteCriadoEvent extends ApplicationEvent {

	private final Cliente cliente;

	/**
	 * Construtor do evento ClienteCriadoEvent.
	 *
	 * @param source  Objeto que gerou o evento.
	 * @param cliente Cliente criado.
	 */
	public ClienteCriadoEvent(Object source, Cliente cliente) {
		super(source);
		this.cliente = cliente;
	}

	/**
	 * Retorna o cliente criado.
	 *
	 * @return O cliente criado.
	 */
	public Cliente getCliente() {
		return cliente;
	}
}
