package br.insper.delivery.pedido.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.repository.PedidoRepository;

/**
 * Serviço de consulta de pedidos.
 */
@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final ClienteService clienteService;

	/**
	 * Construtor da classe PedidoService.
	 *
	 * @param pedidoRepository Repositório de pedidos.
	 * @param clienteService   Serviço de clientes, usado para validar o dono dos pedidos.
	 */
	public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService) {
		this.pedidoRepository = pedidoRepository;
		this.clienteService = clienteService;
	}

	/**
	 * Busca um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return Pedido encontrado.
	 * @throws ResponseStatusException Se o pedido não for encontrado.
	 */
	public Pedido buscarPorId(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
	}

	/**
	 * Lista todos os pedidos de um cliente.
	 *
	 * @param clienteId ID do cliente.
	 * @return Lista de pedidos do cliente.
	 * @throws ResponseStatusException Se o cliente não for encontrado.
	 */
	public List<Pedido> listarPorCliente(Long clienteId) {
		clienteService.buscarPorId(clienteId);
		return pedidoRepository.findByClienteId(clienteId);
	}
}
