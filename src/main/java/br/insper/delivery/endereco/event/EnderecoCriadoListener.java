package br.insper.delivery.endereco.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EnderecoCriadoListener {

	private static final Logger log = LoggerFactory.getLogger(EnderecoCriadoListener.class);

	@EventListener
	public void aoCriarEndereco(EnderecoCriadoEvent evento) {
		log.info("Endereco criado: id={}, clienteId={}", evento.getEndereco().getId(),
				evento.getEndereco().getClienteId());
	}
}
