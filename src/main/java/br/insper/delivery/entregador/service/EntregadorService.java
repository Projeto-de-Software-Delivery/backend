package br.insper.delivery.entregador.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.entregador.domain.Entregador;
import br.insper.delivery.entregador.domain.StatusDisponibilidade;
import br.insper.delivery.entregador.dto.EntregadorRequest;
import br.insper.delivery.entregador.event.EntregadorCriadoEvent;
import br.insper.delivery.entregador.event.EntregadorStatusAlteradoEvent;
import br.insper.delivery.entregador.repository.EntregadorRepository;

/**
 * Serviço para gerenciar entregadores.
 */
@Service
public class EntregadorService {

	private final EntregadorRepository entregadorRepository;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe EntregadorService.
	 *
	 * @param entregadorRepository Repositório de entregadores.
	 * @param eventPublisher       Publicador de eventos.
	 */
	public EntregadorService(EntregadorRepository entregadorRepository, ApplicationEventPublisher eventPublisher) {
		this.entregadorRepository = entregadorRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria um novo entregador. O entregador é criado com status DISPONIVEL.
	 *
	 * @param request Dados do entregador a ser criado.
	 * @return Entregador criado.
	 */
	public Entregador criar(EntregadorRequest request) {
		Entregador entregador = new Entregador(request.nome(), request.cpf(), request.telefone(), request.placa());
		Entregador salvo = entregadorRepository.save(entregador);
		eventPublisher.publishEvent(new EntregadorCriadoEvent(this, salvo));
		return salvo;
	}

	/**
	 * Lista todos os entregadores.
	 *
	 * @return Lista de entregadores.
	 */
	public List<Entregador> listarTodos() {
		return entregadorRepository.findAll();
	}

	/**
	 * Busca um entregador pelo seu ID.
	 *
	 * @param id ID do entregador a ser buscado.
	 * @return Entregador encontrado.
	 * @throws ResponseStatusException Se o entregador não for encontrado.
	 */
	public Entregador buscarPorId(Long id) {
		return entregadorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entregador não encontrado"));
	}

	/**
	 * Atualiza os dados cadastrais de um entregador existente.
	 *
	 * @param id      ID do entregador a ser atualizado.
	 * @param request Novos dados do entregador.
	 * @return Entregador atualizado.
	 * @throws ResponseStatusException Se o entregador não for encontrado.
	 */
	public Entregador atualizar(Long id, EntregadorRequest request) {
		Entregador entregador = buscarPorId(id);
		entregador.atualizar(request.nome(), request.cpf(), request.telefone(), request.placa());
		return entregadorRepository.save(entregador);
	}

	/**
	 * Atualiza o status de disponibilidade de um entregador.
	 *
	 * @param id     ID do entregador a ser atualizado.
	 * @param status Novo status de disponibilidade.
	 * @return Entregador com o status atualizado.
	 * @throws ResponseStatusException Se o entregador não for encontrado.
	 */
	public Entregador atualizarStatus(Long id, StatusDisponibilidade status) {
		Entregador entregador = buscarPorId(id);
		entregador.atualizarStatus(status);
		Entregador salvo = entregadorRepository.save(entregador);
		eventPublisher.publishEvent(new EntregadorStatusAlteradoEvent(this, salvo));
		return salvo;
	}

	/**
	 * Remove um entregador.
	 *
	 * @param id ID do entregador a ser removido.
	 * @throws ResponseStatusException Se o entregador não for encontrado.
	 */
	public void deletar(Long id) {
		Entregador entregador = buscarPorId(id);
		entregadorRepository.delete(entregador);
	}
}
