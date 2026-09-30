package br.insper.delivery.endereco.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.endereco.domain.Endereco;
import br.insper.delivery.endereco.dto.EnderecoRequest;
import br.insper.delivery.endereco.event.EnderecoCriadoEvent;
import br.insper.delivery.endereco.repository.EnderecoRepository;

/**
 * Serviço para gerenciar endereços de clientes.
 */
@Service
public class EnderecoService {

	private final EnderecoRepository enderecoRepository;
	private final ClienteService clienteService;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe EnderecoService.
	 *
	 * @param enderecoRepository Repositório de endereços.
	 * @param clienteService     Serviço de clientes, usado para validar o dono do endereço.
	 * @param eventPublisher     Publicador de eventos.
	 */
	public EnderecoService(EnderecoRepository enderecoRepository, ClienteService clienteService,
			ApplicationEventPublisher eventPublisher) {
		this.enderecoRepository = enderecoRepository;
		this.clienteService = clienteService;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria um novo endereço para um cliente.
	 *
	 * @param clienteId ID do cliente dono do endereço.
	 * @param request   Dados do endereço a ser criado.
	 * @return Endereço criado.
	 * @throws ResponseStatusException Se o cliente não for encontrado.
	 */
	public Endereco criar(Long clienteId, EnderecoRequest request) {
		clienteService.buscarPorId(clienteId);
		Endereco endereco = new Endereco(clienteId, request.cep(), request.logradouro(), request.numero(),
				request.complemento(), request.bairro(), request.cidade(), request.estado());
		Endereco salvo = enderecoRepository.save(endereco);
		eventPublisher.publishEvent(new EnderecoCriadoEvent(this, salvo));
		return salvo;
	}

	/**
	 * Lista todos os endereços de um cliente.
	 *
	 * @param clienteId ID do cliente.
	 * @return Lista de endereços do cliente.
	 * @throws ResponseStatusException Se o cliente não for encontrado.
	 */
	public List<Endereco> listarPorCliente(Long clienteId) {
		clienteService.buscarPorId(clienteId);
		return enderecoRepository.findByClienteId(clienteId);
	}

	/**
	 * Busca um endereço de um cliente pelo seu ID.
	 *
	 * @param clienteId ID do cliente dono do endereço.
	 * @param id        ID do endereço a ser buscado.
	 * @return Endereço encontrado.
	 * @throws ResponseStatusException Se o cliente ou o endereço não forem encontrados.
	 */
	public Endereco buscarPorId(Long clienteId, Long id) {
		clienteService.buscarPorId(clienteId);
		Endereco endereco = enderecoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));
		if (!endereco.getClienteId().equals(clienteId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado");
		}
		return endereco;
	}

	/**
	 * Atualiza os dados de um endereço existente.
	 *
	 * @param clienteId ID do cliente dono do endereço.
	 * @param id        ID do endereço a ser atualizado.
	 * @param request   Novos dados do endereço.
	 * @return Endereço atualizado.
	 * @throws ResponseStatusException Se o cliente ou o endereço não forem encontrados.
	 */
	public Endereco atualizar(Long clienteId, Long id, EnderecoRequest request) {
		Endereco endereco = buscarPorId(clienteId, id);
		endereco.atualizar(request.cep(), request.logradouro(), request.numero(), request.complemento(),
				request.bairro(), request.cidade(), request.estado());
		return enderecoRepository.save(endereco);
	}

	/**
	 * Remove um endereço de um cliente.
	 *
	 * @param clienteId ID do cliente dono do endereço.
	 * @param id        ID do endereço a ser removido.
	 * @throws ResponseStatusException Se o cliente ou o endereço não forem encontrados.
	 */
	public void deletar(Long clienteId, Long id) {
		Endereco endereco = buscarPorId(clienteId, id);
		enderecoRepository.delete(endereco);
	}
}
