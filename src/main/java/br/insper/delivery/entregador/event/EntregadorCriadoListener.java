package br.insper.delivery.entregador.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EntregadorCriadoListener {

	private static final Logger log = LoggerFactory.getLogger(EntregadorCriadoListener.class);

	@EventListener
	public void aoCriarEntregador(EntregadorCriadoEvent evento) {
		log.info("Entregador criado: id={}, cpf={}", evento.getEntregador().getId(), evento.getEntregador().getCpf());
	}
}
