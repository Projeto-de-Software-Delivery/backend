package br.insper.delivery.pedido.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.dto.PedidoStatusResponse;
import br.insper.delivery.pedido.service.PedidoService;

/**
 * Controlador de consulta de pedidos.
 */
@RestController
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	/**
	 * Busca um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return ResponseEntity com o pedido encontrado.
	 */
	@GetMapping("/pedidos/{id}")
	public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(PedidoResponse.from(pedidoService.buscarPorId(id)));
	}

	/**
	 * Busca o status de um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return ResponseEntity com o status do pedido.
	 */
	@GetMapping("/pedidos/{id}/status")
	public ResponseEntity<PedidoStatusResponse> buscarStatus(@PathVariable Long id) {
		return ResponseEntity.ok(PedidoStatusResponse.from(pedidoService.buscarPorId(id)));
	}

	/**
	 * Lista todos os pedidos de um cliente.
	 *
	 * @param clienteId ID do cliente.
	 * @return ResponseEntity com a lista de pedidos do cliente.
	 */
	@GetMapping("/clientes/{clienteId}/pedidos")
	public ResponseEntity<List<PedidoResponse>> listarPorCliente(@PathVariable Long clienteId) {
		List<PedidoResponse> response = pedidoService.listarPorCliente(clienteId).stream().map(PedidoResponse::from)
				.toList();
		return ResponseEntity.ok(response);
	}
}
