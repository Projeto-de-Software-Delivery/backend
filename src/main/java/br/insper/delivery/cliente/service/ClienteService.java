package br.insper.delivery.cliente.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.dto.ClienteRequest;
import br.insper.delivery.cliente.event.ClienteCriadoEvent;
import br.insper.delivery.cliente.repository.ClienteRepository;

@Service
public class ClienteService {

	private final ClienteRepository clienteRepository;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe ClienteService.
	 *
	 * @param clienteRepository Repositório de clientes.
	 *
	 * @param eventPublisher    Publicador de eventos.
	 */
	public ClienteService(ClienteRepository clienteRepository, ApplicationEventPublisher eventPublisher) {
		this.clienteRepository = clienteRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria um novo cliente.
	 *
	 * @param request Dados do cliente a ser criado.
	 * @return O cliente criado.
	 */
	public Cliente criar(ClienteRequest request) {
		Cliente cliente = new Cliente(request.nome(), request.email(), request.telefone());
		Cliente salvo = clienteRepository.save(cliente);
		eventPublisher.publishEvent(new ClienteCriadoEvent(this, salvo));
		return salvo;
	}

	/**
	 * Busca um cliente pelo seu ID.
	 *
	 * @param id ID do cliente a ser buscado.
	 * @return O cliente encontrado.
	 * @throws ResponseStatusException Se o cliente não for encontrado.
	 */
	public Cliente buscarPorId(Long id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}
}
