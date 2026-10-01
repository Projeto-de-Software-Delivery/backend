package br.insper.delivery.produto.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.produto.dto.ProdutoRequest;
import br.insper.delivery.produto.dto.ProdutoResponse;
import br.insper.delivery.produto.service.ProdutoService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar produtos do catálogo.
 */
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@PostMapping
	public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
		ProdutoResponse response = ProdutoResponse.from(produtoService.criar(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/** Com lojaId informado, lista só os produtos dessa loja. */
	@GetMapping
	public ResponseEntity<List<ProdutoResponse>> listarTodos(@RequestParam(required = false) Long lojaId) {
		List<ProdutoResponse> response = (lojaId != null ? produtoService.listarPorLoja(lojaId)
				: produtoService.listarTodos()).stream().map(ProdutoResponse::from).toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(ProdutoResponse.from(produtoService.buscarPorId(id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
			@Valid @RequestBody ProdutoRequest request) {
		return ResponseEntity.ok(ProdutoResponse.from(produtoService.atualizar(id, request)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		produtoService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
