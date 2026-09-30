package br.insper.delivery.entregador.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.delivery.entregador.dto.EntregadorRequest;
import br.insper.delivery.entregador.dto.EntregadorResponse;
import br.insper.delivery.entregador.dto.StatusDisponibilidadeRequest;
import br.insper.delivery.entregador.service.EntregadorService;
import jakarta.validation.Valid;

/**
 * Controlador para gerenciar entregadores.
 */
@RestController
@RequestMapping("/entregadores")
public class EntregadorController {

	private final EntregadorService entregadorService;

	public EntregadorController(EntregadorService entregadorService) {
		this.entregadorService = entregadorService;
	}

	/**
	 * Cria um novo entregador.
	 *
	 * @param request Dados do entregador a ser criado.
	 * @return ResponseEntity com o entregador criado.
	 */
	@PostMapping
	public ResponseEntity<EntregadorResponse> criar(@Valid @RequestBody EntregadorRequest request) {
		EntregadorResponse response = EntregadorResponse.from(entregadorService.criar(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Lista todos os entregadores.
	 *
	 * @return ResponseEntity com a lista de entregadores.
	 */
	@GetMapping
	public ResponseEntity<List<EntregadorResponse>> listarTodos() {
		List<EntregadorResponse> response = entregadorService.listarTodos().stream().map(EntregadorResponse::from)
				.toList();
		return ResponseEntity.ok(response);
	}

	/**
	 * Busca um entregador pelo seu ID.
	 *
	 * @param id ID do entregador a ser buscado.
	 * @return ResponseEntity com o entregador encontrado.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<EntregadorResponse> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(EntregadorResponse.from(entregadorService.buscarPorId(id)));
	}

	/**
	 * Atualiza os dados cadastrais de um entregador existente.
	 *
	 * @param id      ID do entregador a ser atualizado.
	 * @param request Novos dados do entregador.
	 * @return ResponseEntity com o entregador atualizado.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<EntregadorResponse> atualizar(@PathVariable Long id,
			@Valid @RequestBody EntregadorRequest request) {
		return ResponseEntity.ok(EntregadorResponse.from(entregadorService.atualizar(id, request)));
	}

	/**
	 * Atualiza o status de disponibilidade de um entregador.
	 *
	 * @param id      ID do entregador a ser atualizado.
	 * @param request Novo status de disponibilidade.
	 * @return ResponseEntity com o entregador atualizado.
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<EntregadorResponse> atualizarStatus(@PathVariable Long id,
			@Valid @RequestBody StatusDisponibilidadeRequest request) {
		return ResponseEntity.ok(EntregadorResponse.from(entregadorService.atualizarStatus(id, request.status())));
	}

	/**
	 * Remove um entregador.
	 *
	 * @param id ID do entregador a ser removido.
	 * @return ResponseEntity sem conteúdo.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		entregadorService.deletar(id);
		return ResponseEntity.noContent().build();
	}
}
