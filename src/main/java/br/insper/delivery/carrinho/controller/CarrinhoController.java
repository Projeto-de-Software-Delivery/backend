package br.insper.delivery.carrinho.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.carrinho.dto.CarrinhoResponse;
import br.insper.delivery.carrinho.dto.ItemCarrinhoRequest;
import br.insper.delivery.carrinho.service.CarrinhoService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar o carrinho de compras de um cliente.
 */
@RestController
@RequestMapping("/clientes/{clienteId}/carrinho")
public class CarrinhoController {

	private final CarrinhoService carrinhoService;

	public CarrinhoController(CarrinhoService carrinhoService) {
		this.carrinhoService = carrinhoService;
	}

	/**
	 * Busca o carrinho do cliente, com os itens e o total calculado.
	 *
	 * @param clienteId ID do cliente dono do carrinho.
	 * @return ResponseEntity com o carrinho do cliente.
	 */
	@GetMapping
	public ResponseEntity<CarrinhoResponse> buscarCarrinho(@PathVariable Long clienteId) {
		return ResponseEntity.ok(carrinhoService.buscarCarrinho(clienteId));
	}

	/**
	 * Adiciona um item ao carrinho do cliente.
	 *
	 * @param clienteId ID do cliente dono do carrinho.
	 * @param request   Produto e quantidade a serem adicionados.
	 * @return ResponseEntity com o carrinho atualizado.
	 */
	@PostMapping("/itens")
	public ResponseEntity<CarrinhoResponse> adicionarItem(@PathVariable Long clienteId,
			@Valid @RequestBody ItemCarrinhoRequest request) {
		CarrinhoResponse response = carrinhoService.adicionarItem(clienteId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Remove um item do carrinho do cliente.
	 *
	 * @param clienteId ID do cliente dono do carrinho.
	 * @param itemId    ID do item a ser removido.
	 * @return ResponseEntity com o carrinho atualizado.
	 */
	@DeleteMapping("/itens/{itemId}")
	public ResponseEntity<CarrinhoResponse> removerItem(@PathVariable Long clienteId, @PathVariable Long itemId) {
		return ResponseEntity.ok(carrinhoService.removerItem(clienteId, itemId));
	}
}
