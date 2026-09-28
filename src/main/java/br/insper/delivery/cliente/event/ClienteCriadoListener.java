package br.insper.delivery.cliente.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClienteCriadoListener {

	private static final Logger log = LoggerFactory.getLogger(ClienteCriadoListener.class);

	@EventListener
	public void aoCriarCliente(ClienteCriadoEvent evento) {
		log.info("Cliente criado: id={}, email={}", evento.getCliente().getId(), evento.getCliente().getEmail());
	}
}
