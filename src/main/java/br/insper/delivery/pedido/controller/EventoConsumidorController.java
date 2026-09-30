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
 * Consome os eventos dos demais tópicos da arquitetura (payloads.md) e aplica as transições
 * correspondentes na máquina de estados do pedido. Como ainda não há um broker de mensageria
 * configurado, os eventos são recebidos via HTTP nesses endpoints.
 */
@RestController
@RequestMapping("/eventos")
public class EventoConsumidorController {

	private final PedidoService pedidoService;

	public EventoConsumidorController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	/**
	 * Consome o evento pedido.validado (loja aceitou e baixou o estoque).
	 *
	 * @param evento Envelope do evento pedido.validado.
	 * @return ResponseEntity com o pedido atualizado.
	 */
	@PostMapping("/pedido-validado")
	public ResponseEntity<PedidoResponse> pedidoValidado(
			@Valid @RequestBody EventoRecebido<PedidoValidadoDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarPedidoValidado(evento.data()));
	}

	/**
	 * Consome o evento entrega.aceita (um entregador pegou a corrida).
	 *
	 * @param evento Envelope do evento entrega.aceita.
	 * @return ResponseEntity com o pedido atualizado.
	 */
	@PostMapping("/entrega-aceita")
	public ResponseEntity<PedidoResponse> entregaAceita(
			@Valid @RequestBody EventoRecebido<EntregaAceitaDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarEntregaAceita(evento.data()));
	}

	/**
	 * Consome o evento pedido.retirado (entregador saiu da loja com o pedido).
	 *
	 * @param evento Envelope do evento pedido.retirado.
	 * @return ResponseEntity com o pedido atualizado.
	 */
	@PostMapping("/pedido-retirado")
	public ResponseEntity<PedidoResponse> pedidoRetirado(
			@Valid @RequestBody EventoRecebido<PedidoRetiradoDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarPedidoRetirado(evento.data()));
	}

	/**
	 * Consome o evento pedido.entregue (PIN validado e entrega finalizada).
	 *
	 * @param evento Envelope do evento pedido.entregue.
	 * @return ResponseEntity com o pedido atualizado.
	 */
	@PostMapping("/pedido-entregue")
	public ResponseEntity<PedidoResponse> pedidoEntregue(
			@Valid @RequestBody EventoRecebido<PedidoEntregueDados> evento) {
		return ResponseEntity.ok(pedidoService.aplicarPedidoEntregue(evento.data()));
	}
}
