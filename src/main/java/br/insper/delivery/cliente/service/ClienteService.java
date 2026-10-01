package br.insper.delivery.cliente.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.dto.ClienteRequest;
import br.insper.delivery.cliente.repository.ClienteRepository;

/**
 * Serviço para gerenciar clientes.
 */
@Service
public class ClienteService {

	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	public Cliente criar(ClienteRequest request) {
		Cliente cliente = new Cliente(request.nome(), request.email(), request.telefone());
		Cliente salvo = clienteRepository.save(cliente);
		log.info("Cliente criado: id={}, email={}", salvo.getId(), salvo.getEmail());
		return salvo;
	}

	public List<Cliente> listarTodos() {
		return clienteRepository.findAll();
	}

	/**
	 * @throws ResponseStatusException 404 se o cliente não existir.
	 */
	public Cliente buscarPorId(Long id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}

	public Cliente atualizar(Long id, ClienteRequest request) {
		Cliente cliente = buscarPorId(id);
		cliente.atualizar(request.nome(), request.email(), request.telefone());
		return clienteRepository.save(cliente);
	}

	public void deletar(Long id) {
		Cliente cliente = buscarPorId(id);
		clienteRepository.delete(cliente);
	}
}
