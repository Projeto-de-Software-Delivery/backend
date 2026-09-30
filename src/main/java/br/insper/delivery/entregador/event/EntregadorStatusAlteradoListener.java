package br.insper.delivery.entregador.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EntregadorStatusAlteradoListener {

	private static final Logger log = LoggerFactory.getLogger(EntregadorStatusAlteradoListener.class);

	@EventListener
	public void aoAlterarStatus(EntregadorStatusAlteradoEvent evento) {
		log.info("Status do entregador alterado: id={}, status={}", evento.getEntregador().getId(),
				evento.getEntregador().getStatus());
	}
}
