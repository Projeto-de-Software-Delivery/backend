package br.insper.delivery.cliente.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.cliente.dto.ClienteRequest;
import br.insper.delivery.cliente.dto.ClienteResponse;
import br.insper.delivery.cliente.service.ClienteService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar clientes.
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	/**
	 * Cria um novo cliente.
	 *
	 * @param request Dados do cliente a ser criado.
	 * @return ResponseEntity com o cliente criado.
	 */
	@PostMapping
	public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
		ClienteResponse response = ClienteResponse.from(clienteService.criar(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Busca um cliente pelo seu ID.
	 *
	 * @param id ID do cliente a ser buscado.
	 * @return ResponseEntity com o cliente encontrado.
	 */

	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(ClienteResponse.from(clienteService.buscarPorId(id)));
	}
}
