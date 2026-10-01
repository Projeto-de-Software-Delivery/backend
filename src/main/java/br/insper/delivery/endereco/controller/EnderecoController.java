package br.insper.delivery.endereco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.endereco.dto.EnderecoRequest;
import br.insper.delivery.endereco.dto.EnderecoResponse;
import br.insper.delivery.endereco.service.EnderecoService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar endereços de clientes.
 */
@RestController
@RequestMapping("/clientes/{clienteId}/enderecos")
public class EnderecoController {

	private final EnderecoService enderecoService;

	public EnderecoController(EnderecoService enderecoService) {
		this.enderecoService = enderecoService;
	}

	@PostMapping
	public ResponseEntity<EnderecoResponse> criar(@PathVariable Long clienteId,
			@Valid @RequestBody EnderecoRequest request) {
		EnderecoResponse response = EnderecoResponse.from(enderecoService.criar(clienteId, request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<EnderecoResponse>> listarPorCliente(@PathVariable Long clienteId) {
		List<EnderecoResponse> response = enderecoService.listarPorCliente(clienteId).stream()
				.map(EnderecoResponse::from).toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EnderecoResponse> buscarPorId(@PathVariable Long clienteId, @PathVariable Long id) {
		return ResponseEntity.ok(EnderecoResponse.from(enderecoService.buscarPorId(clienteId, id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EnderecoResponse> atualizar(@PathVariable Long clienteId, @PathVariable Long id,
			@Valid @RequestBody EnderecoRequest request) {
		return ResponseEntity.ok(EnderecoResponse.from(enderecoService.atualizar(clienteId, id, request)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long clienteId, @PathVariable Long id) {
		enderecoService.deletar(clienteId, id);
		return ResponseEntity.noContent().build();
	}
}
