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

	/**
	 * Cria um novo produto.
	 *
	 * @param request Dados do produto a ser criado.
	 * @return ResponseEntity com o produto criado.
	 */
	@PostMapping
	public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
		ProdutoResponse response = ProdutoResponse.from(produtoService.criar(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**+
	 * Lista todos os produtos do catálogo.
	 *
	 * @return ResponseEntity com a lista de produtos.
	 */
	@GetMapping
	public ResponseEntity<List<ProdutoResponse>> listarTodos() {
		List<ProdutoResponse> response = produtoService.listarTodos().stream().map(ProdutoResponse::from).toList();
		return ResponseEntity.ok(response);
	}

	/**
	 * Busca um produto pelo seu ID.
	 *
	 * @param id ID do produto a ser buscado.
	 * @return ResponseEntity com o produto encontrado.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(ProdutoResponse.from(produtoService.buscarPorId(id)));
	}

	/**
	 * Atualiza os dados de um produto existente.
	 *
	 * @param id      ID do produto a ser atualizado.
	 * @param request Novos dados do produto.
	 * @return ResponseEntity com o produto atualizado.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
			@Valid @RequestBody ProdutoRequest request) {
		return ResponseEntity.ok(ProdutoResponse.from(produtoService.atualizar(id, request)));
	}

	/**
	 * Remove um produto do catálogo.
	 *
	 * @param id ID do produto a ser removido.
	 * @return ResponseEntity sem conteúdo.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		produtoService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
