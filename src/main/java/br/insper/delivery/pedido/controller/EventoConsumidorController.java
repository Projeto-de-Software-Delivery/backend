package br.insper.delivery.pedido.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.pedido.dto.EntregaAceitaDados;
import br.insper.delivery.pedido.dto.EventoRecebido;
import br.insper.delivery.pedido.dto.PedidoEntregueDados;
import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.dto.PedidoRetiradoDados;
import br.insper.delivery.pedido.dto.PedidoValidadoDados;
import br.insper.delivery.pedido.service.PedidoService;
import jakarta.validation.Valid;

/**
 * Consome os eventos dos tópicos de payloads.md que ainda não têm um @RabbitListener real
 * (pedido.validado é publicado de verdade pela KAN-34; os outros três dependem do serviço de
 * entregador, que ainda não existe) e aplica as transições correspondentes na máquina de
 * estados do pedido.
 */
@RestController
@RequestMapping("/eventos")
public class EventoConsumidorController {

	private final PedidoService pedidoService;

	public EventoConsumidorController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	/** Loja aceitou e baixou o estoque. */
	@PostMapping("/pedido-validado")
	public ResponseEntity<PedidoResponse> pedidoValidado(
			@Valid @RequestBody EventoRecebido<PedidoValidadoDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarPedidoValidado(evento.data()));
	}

	/** Um entregador pegou a corrida. */
	@PostMapping("/entrega-aceita")
	public ResponseEntity<PedidoResponse> entregaAceita(
			@Valid @RequestBody EventoRecebido<EntregaAceitaDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarEntregaAceita(evento.data()));
	}

	/** Entregador saiu da loja com o pedido. */
	@PostMapping("/pedido-retirado")
	public ResponseEntity<PedidoResponse> pedidoRetirado(
			@Valid @RequestBody EventoRecebido<PedidoRetiradoDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarPedidoRetirado(evento.data()));
	}

	/** PIN validado e entrega finalizada. */
	@PostMapping("/pedido-entregue")
	public ResponseEntity<PedidoResponse> pedidoEntregue(
			@Valid @RequestBody EventoRecebido<PedidoEntregueDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarPedidoEntregue(evento.data()));
	}
}
