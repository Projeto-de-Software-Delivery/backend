package br.insper.delivery.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara o exchange dos eventos de pedido, consumido pelo serviço de notificações
 * (exchange "pedidos", tipo topic, routing key = eventType do payload — ex.: "pedido.criado").
 */
@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE_PEDIDOS = "pedidos";

	@Bean
	public TopicExchange pedidosExchange() {
		return new TopicExchange(EXCHANGE_PEDIDOS, true, false);
	}
}
