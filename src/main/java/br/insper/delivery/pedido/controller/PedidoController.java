package br.insper.delivery.pedido.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.pedido.dto.CriarPedidoRequest;
import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.dto.PedidoStatusResponse;
import br.insper.delivery.pedido.service.PedidoService;
import jakarta.validation.Valid;

/**
 * Controlador de pedidos.
 */
@RestController
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	/**
	 * Cria um novo pedido para um cliente, com status AGUARDANDO_VALIDACAO.
	 *
	 * @param clienteId ID do cliente que está fazendo o pedido.
	 * @param request   Loja, itens e endereço de entrega do pedido.
	 * @return ResponseEntity com o pedido criado.
	 */
	@PostMapping("/clientes/{clienteId}/pedidos")
	public ResponseEntity<PedidoResponse> criar(@PathVariable Long clienteId,
			@Valid @RequestBody CriarPedidoRequest request) {
		PedidoResponse response = pedidoService.criar(clienteId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Busca um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return ResponseEntity com o pedido encontrado.
	 */
	@GetMapping("/pedidos/{id}")
	public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(pedidoService.buscarPorId(id));
	}

	/**
	 * Busca o status de um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return ResponseEntity com o status do pedido.
	 */
	@GetMapping("/pedidos/{id}/status")
	public ResponseEntity<PedidoStatusResponse> buscarStatus(@PathVariable Long id) {
		return ResponseEntity.ok(new PedidoStatusResponse(id, pedidoService.buscarStatus(id)));
	}

	/**
	 * Lista todos os pedidos de um cliente.
	 *
	 * @param clienteId ID do cliente.
	 * @return ResponseEntity com a lista de pedidos do cliente.
	 */
	@GetMapping("/clientes/{clienteId}/pedidos")
	public ResponseEntity<List<PedidoResponse>> listarPorCliente(@PathVariable Long clienteId) {
		return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
	}
}
