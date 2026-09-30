package br.insper.delivery.pedido.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import br.insper.delivery.config.RabbitMQConfig;

/**
 * Publica o evento pedido.criado no RabbitMQ (exchange "pedidos", routing key
 * "pedido.criado"), consumido pelo serviço de notificações.
 */
@Component
public class PedidoCriadoListener {

	private static final Logger log = LoggerFactory.getLogger(PedidoCriadoListener.class);

	private final ObjectMapper objectMapper;
	private final AmqpTemplate amqpTemplate;

	public PedidoCriadoListener(ObjectMapper objectMapper, AmqpTemplate amqpTemplate) {
		this.objectMapper = objectMapper;
		this.amqpTemplate = amqpTemplate;
	}

	@EventListener
	public void aoCriarPedido(PedidoCriadoEvent event) {
		PedidoCriadoEvento evento = event.getEvento();
		try {
			String payload = objectMapper.writeValueAsString(evento);
			amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PEDIDOS, evento.eventType(), payload);
			log.info("Publicado no topico {}: {}", evento.eventType(), payload);
		} catch (JacksonException e) {
			log.error("Falha ao serializar evento pedido.criado", e);
		} catch (AmqpException e) {
			// Publicacao e best-effort: o pedido ja foi persistido, nao falha a requisicao
			// por causa de uma indisponibilidade momentanea do broker.
			log.error("Falha ao publicar evento pedido.criado no RabbitMQ", e);
		}
	}
}
