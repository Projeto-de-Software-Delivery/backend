package br.insper.delivery.loja.controller;

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

import br.insper.delivery.loja.dto.LojaRequest;
import br.insper.delivery.loja.dto.LojaResponse;
import br.insper.delivery.loja.service.LojaService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar lojas.
 */
@RestController
@RequestMapping("/lojas")
public class LojaController {

	private final LojaService lojaService;

	public LojaController(LojaService lojaService) {
		this.lojaService = lojaService;
	}

	/**
	 * Cria uma nova loja.
	 *
	 * @param request Dados da loja a ser criada.
	 * @return ResponseEntity com a loja criada.
	 */
	@PostMapping
	public ResponseEntity<LojaResponse> criar(@Valid @RequestBody LojaRequest request) {
		LojaResponse response = LojaResponse.from(lojaService.criar(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Lista todas as lojas.
	 *
	 * @return ResponseEntity com a lista de lojas.
	 */
	@GetMapping
	public ResponseEntity<List<LojaResponse>> listarTodas() {
		List<LojaResponse> response = lojaService.listarTodas().stream().map(LojaResponse::from).toList();
		return ResponseEntity.ok(response);
	}

	/**
	 * Busca uma loja pelo seu ID.
	 *
	 * @param id ID da loja a ser buscada.
	 * @return ResponseEntity com a loja encontrada.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<LojaResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(LojaResponse.from(lojaService.buscarPorId(id)));
	}

	/**
	 * Atualiza os dados de uma loja existente.
	 *
	 * @param id      ID da loja a ser atualizada.
	 * @param request Novos dados da loja.
	 * @return ResponseEntity com a loja atualizada.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<LojaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody LojaRequest request) {
		return ResponseEntity.ok(LojaResponse.from(lojaService.atualizar(id, request)));
	}

	/**
	 * Remove uma loja.
	 *
	 * @param id ID da loja a ser removida.
	 * @return ResponseEntity sem conteúdo.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		lojaService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
