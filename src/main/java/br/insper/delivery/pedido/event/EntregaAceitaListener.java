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
 * Publica o evento entrega.aceita no RabbitMQ (exchange "pedidos", routing key "entrega.aceita"),
 * consumido pelo serviço de notificações.
 */
@Component
public class EntregaAceitaListener {

	private static final Logger log = LoggerFactory.getLogger(EntregaAceitaListener.class);

	private final ObjectMapper objectMapper;
	private final AmqpTemplate amqpTemplate;

	public EntregaAceitaListener(ObjectMapper objectMapper, AmqpTemplate amqpTemplate) {
		this.objectMapper = objectMapper;
		this.amqpTemplate = amqpTemplate;
	}

	@EventListener
	public void aoAceitarEntrega(EntregaAceitaEvent event) {
		EntregaAceitaEvento evento = event.getEvento();
		try {
			String payload = objectMapper.writeValueAsString(evento);
			amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PEDIDOS, evento.eventType(), payload);
			log.info("Publicado no topico {}: {}", evento.eventType(), payload);
		} catch (JacksonException e) {
			log.error("Falha ao serializar evento entrega.aceita", e);
		} catch (AmqpException e) {
			// Publicacao e best-effort: o entregador ja foi atribuido, nao falha a requisicao por
			// causa de uma indisponibilidade momentanea do broker.
			log.error("Falha ao publicar evento entrega.aceita no RabbitMQ", e);
		}
	}
}
