package br.insper.delivery.gateway.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import br.insper.delivery.gateway.config.RouteProperties;
import br.insper.delivery.gateway.event.RequisicaoRoteadaEvent;

@RestController
public class GatewayController {

	private final RestClient restClient;
	private final RouteProperties routeProperties;
	private final ApplicationEventPublisher eventPublisher;

	public GatewayController(RouteProperties routeProperties, ApplicationEventPublisher eventPublisher) {
		this.restClient = RestClient.create();
		this.routeProperties = routeProperties;
		this.eventPublisher = eventPublisher;
	}

	@RequestMapping("/clientes/**")
	public ResponseEntity<String> rotearParaCliente(HttpMethod method, jakarta.servlet.http.HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return rotear(routeProperties.clienteServiceUrl(), "cliente-service", method, request, body);
	}

	@RequestMapping("/lojas/**")
	public ResponseEntity<String> rotearParaLoja(HttpMethod method, jakarta.servlet.http.HttpServletRequest request,
			@RequestBody(required = false) String body) {
		return rotear(routeProperties.lojaServiceUrl(), "loja-service", method, request, body);
	}

	private ResponseEntity<String> rotear(String baseUrl, String servicoDestino, HttpMethod method,
			jakarta.servlet.http.HttpServletRequest request, String body) {
		String path = request.getRequestURI();
		eventPublisher.publishEvent(new RequisicaoRoteadaEvent(this, servicoDestino, path));

		return restClient.method(method)
				.uri(baseUrl + path)
				.body(body == null ? "" : body)
				.retrieve()
				.toEntity(String.class);
	}
}
