package br.insper.delivery.loja.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LojaCriadaListener {

	private static final Logger log = LoggerFactory.getLogger(LojaCriadaListener.class);

	@EventListener
	public void aoCriarLoja(LojaCriadaEvent evento) {
		log.info("Loja criada: id={}, cnpj={}", evento.getLoja().getId(), evento.getLoja().getCnpj());
	}
}
