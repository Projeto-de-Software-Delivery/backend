package br.insper.delivery.loja.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.loja.dto.EstoqueLojaRequest;
import br.insper.delivery.loja.dto.EstoqueLojaResponse;
import br.insper.delivery.loja.service.EstoqueLojaService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar o estoque de produtos por loja.
 */
@RestController
@RequestMapping("/lojas/{lojaId}/estoque")
public class EstoqueLojaController {

	private final EstoqueLojaService estoqueLojaService;

	public EstoqueLojaController(EstoqueLojaService estoqueLojaService) {
		this.estoqueLojaService = estoqueLojaService;
	}

	/**
	 * Adiciona ou atualiza o estoque de um produto na loja.
	 * Se o produto já existir no estoque, a quantidade é sobrescrita.
	 *
	 * @param lojaId  ID da loja.
	 * @param request Produto e quantidade.
	 * @return ResponseEntity com o registro de estoque salvo.
	 */
	@PutMapping
	public ResponseEntity<EstoqueLojaResponse> adicionarOuAtualizar(
			@PathVariable Long lojaId,
			@Valid @RequestBody EstoqueLojaRequest request) {
		EstoqueLojaResponse response = EstoqueLojaResponse.from(
				estoqueLojaService.adicionarOuAtualizar(lojaId, request));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Lista todos os produtos em estoque de uma loja.
	 *
	 * @param lojaId ID da loja.
	 * @return ResponseEntity com a lista de estoque.
	 */
	@GetMapping
	public ResponseEntity<List<EstoqueLojaResponse>> listar(@PathVariable Long lojaId) {
		List<EstoqueLojaResponse> response = estoqueLojaService.listarPorLoja(lojaId)
				.stream()
				.map(EstoqueLojaResponse::from)
				.toList();
		return ResponseEntity.ok(response);
	}

	/**
	 * Remove um produto do estoque de uma loja.
	 *
	 * @param lojaId    ID da loja.
	 * @param produtoId ID do produto a remover.
	 * @return ResponseEntity sem conteúdo.
	 */
	@DeleteMapping("/{produtoId}")
	public ResponseEntity<Void> remover(
			@PathVariable Long lojaId,
			@PathVariable Long produtoId) {
		estoqueLojaService.remover(lojaId, produtoId);
		return ResponseEntity.noContent().build();
	}
}
