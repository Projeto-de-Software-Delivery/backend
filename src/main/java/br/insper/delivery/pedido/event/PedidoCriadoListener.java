package br.insper.delivery.pedido.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Publica o evento pedido.criado. Como ainda não há um broker de mensageria configurado, a
 * publicação é simulada via log estruturado com o payload exato do tópico.
 */
@Component
public class PedidoCriadoListener {

	private static final Logger log = LoggerFactory.getLogger(PedidoCriadoListener.class);

	private final ObjectMapper objectMapper;

	public PedidoCriadoListener(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@EventListener
	public void aoCriarPedido(PedidoCriadoEvent event) {
		try {
			log.info("Publicando no topico pedido.criado: {}", objectMapper.writeValueAsString(event.getEvento()));
		} catch (JacksonException e) {
			log.error("Falha ao serializar evento pedido.criado", e);
		}
	}
}
