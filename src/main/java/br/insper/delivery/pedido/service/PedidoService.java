package br.insper.delivery.pedido.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.loja.service.LojaService;
import br.insper.delivery.pedido.domain.ItemPedido;
import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.domain.PedidoStatus;
import br.insper.delivery.pedido.dto.CriarPedidoRequest;
import br.insper.delivery.pedido.dto.EnderecoEntregaResponse;
import br.insper.delivery.pedido.dto.ItemPedidoResponse;
import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.event.PedidoCriadoEvent;
import br.insper.delivery.pedido.event.PedidoCriadoEvento;
import br.insper.delivery.pedido.repository.ItemPedidoRepository;
import br.insper.delivery.pedido.repository.PedidoRepository;
import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.service.ProdutoService;

/**
 * Serviço de pedidos: criação e consulta.
 */
@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final ItemPedidoRepository itemPedidoRepository;
	private final ClienteService clienteService;
	private final LojaService lojaService;
	private final ProdutoService produtoService;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe PedidoService.
	 *
	 * @param pedidoRepository     Repositório de pedidos.
	 * @param itemPedidoRepository Repositório de itens de pedido.
	 * @param clienteService       Serviço de clientes, usado para validar o dono do pedido.
	 * @param lojaService          Serviço de lojas, usado para validar a loja do pedido.
	 * @param produtoService       Serviço de produtos, usado para validar e precificar os itens.
	 * @param eventPublisher       Publicador de eventos.
	 */
	public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
			ClienteService clienteService, LojaService lojaService, ProdutoService produtoService,
			ApplicationEventPublisher eventPublisher) {
		this.pedidoRepository = pedidoRepository;
		this.itemPedidoRepository = itemPedidoRepository;
		this.clienteService = clienteService;
		this.lojaService = lojaService;
		this.produtoService = produtoService;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria um novo pedido com status AGUARDANDO_VALIDACAO e publica o evento pedido.criado.
	 *
	 * @param clienteId ID do cliente que está fazendo o pedido.
	 * @param request   Loja, itens e endereço de entrega do pedido.
	 * @return Pedido criado.
	 * @throws ResponseStatusException Se o cliente, a loja ou algum produto não forem encontrados.
	 */
	public PedidoResponse criar(Long clienteId, CriarPedidoRequest request) {
		clienteService.buscarPorId(clienteId);
		lojaService.buscarPorId(request.lojaId());

		record ItemResolvido(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
		}

		List<ItemResolvido> resolvidos = request.itens().stream()
				.map(itemRequest -> {
					Produto produto = produtoService.buscarPorId(itemRequest.produtoId());
					return new ItemResolvido(itemRequest.produtoId(), itemRequest.quantidade(), produto.getPreco());
				})
				.toList();

		BigDecimal total = resolvidos.stream()
				.map(item -> item.precoUnitario().multiply(BigDecimal.valueOf(item.quantidade())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Pedido pedido = new Pedido(clienteId, request.lojaId(), total, request.enderecoEntrega().rua(),
				request.enderecoEntrega().lat(), request.enderecoEntrega().lng());
		Pedido pedidoSalvo = pedidoRepository.save(pedido);

		List<ItemPedido> itensSalvos = resolvidos.stream()
				.map(item -> itemPedidoRepository
						.save(new ItemPedido(pedidoSalvo.getId(), item.produtoId(), item.quantidade(),
								item.precoUnitario())))
				.toList();

		PedidoCriadoEvento evento = PedidoCriadoEvento.de(pedidoSalvo, itensSalvos);
		eventPublisher.publishEvent(new PedidoCriadoEvent(this, evento));

		return paraResponse(pedidoSalvo, itensSalvos);
	}

	/**
	 * Busca um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return Pedido encontrado.
	 * @throws ResponseStatusException Se o pedido não for encontrado.
	 */
	public PedidoResponse buscarPorId(Long id) {
		Pedido pedido = buscarEntidade(id);
		return paraResponse(pedido, itemPedidoRepository.findByPedidoId(pedido.getId()));
	}

	/**
	 * Busca o status de um pedido pelo seu ID.
	 *
	 * @param id ID do pedido a ser buscado.
	 * @return Status do pedido.
	 * @throws ResponseStatusException Se o pedido não for encontrado.
	 */
	public PedidoStatus buscarStatus(Long id) {
		return buscarEntidade(id).getStatus();
	}

	/**
	 * Lista todos os pedidos de um cliente.
	 *
	 * @param clienteId ID do cliente.
	 * @return Lista de pedidos do cliente.
	 * @throws ResponseStatusException Se o cliente não for encontrado.
	 */
	public List<PedidoResponse> listarPorCliente(Long clienteId) {
		clienteService.buscarPorId(clienteId);
		return pedidoRepository.findByClienteId(clienteId).stream()
				.map(pedido -> paraResponse(pedido, itemPedidoRepository.findByPedidoId(pedido.getId())))
				.toList();
	}

	private Pedido buscarEntidade(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
	}

	private PedidoResponse paraResponse(Pedido pedido, List<ItemPedido> itens) {
		List<ItemPedidoResponse> itensResponse = itens.stream().map(ItemPedidoResponse::from).toList();
		EnderecoEntregaResponse enderecoEntrega = new EnderecoEntregaResponse(pedido.getEnderecoRua(),
				pedido.getEnderecoLat(), pedido.getEnderecoLng());
		return new PedidoResponse(pedido.getId(), pedido.getClienteId(), pedido.getLojaId(), itensResponse,
				pedido.getTotal(), enderecoEntrega, pedido.getStatus(), pedido.getDataCriacao());
	}
}
