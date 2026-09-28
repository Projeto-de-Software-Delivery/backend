package br.insper.delivery.cliente.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.dto.ClienteRequest;
import br.insper.delivery.cliente.event.ClienteCriadoEvent;
import br.insper.delivery.cliente.repository.ClienteRepository;

@Service
public class ClienteService {

	private final ClienteRepository clienteRepository;
	private final ApplicationEventPublisher eventPublisher;

	public ClienteService(ClienteRepository clienteRepository, ApplicationEventPublisher eventPublisher) {
		this.clienteRepository = clienteRepository;
		this.eventPublisher = eventPublisher;
	}

	public Cliente criar(ClienteRequest request) {
		Cliente cliente = new Cliente(request.nome(), request.email(), request.telefone());
		Cliente salvo = clienteRepository.save(cliente);
		eventPublisher.publishEvent(new ClienteCriadoEvent(this, salvo));
		return salvo;
	}

	public Cliente buscarPorId(Long id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}
}
