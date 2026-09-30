package br.insper.delivery.pedido.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import br.insper.delivery.pedido.dto.EntregaAceitaDados;
import br.insper.delivery.pedido.dto.ItemPedidoResponse;
import br.insper.delivery.pedido.dto.PedidoEntregueDados;
import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.dto.PedidoRetiradoDados;
import br.insper.delivery.pedido.dto.PedidoValidadoDados;
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

	private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

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
	 * Consome o evento pedido.validado e aplica a transição AGUARDANDO_VALIDACAO -> VALIDADO.
	 *
	 * @param dados Payload do evento pedido.validado.
	 * @return Pedido atualizado.
	 * @throws ResponseStatusException Se o pedido não for encontrado ou não estiver no status esperado.
	 */
	public PedidoResponse aplicarPedidoValidado(PedidoValidadoDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.AGUARDANDO_VALIDACAO, PedidoStatus.VALIDADO);
	}

	/**
	 * Consome o evento entrega.aceita e aplica a transição VALIDADO -> ENTREGA_ACEITA.
	 *
	 * @param dados Payload do evento entrega.aceita.
	 * @return Pedido atualizado.
	 * @throws ResponseStatusException Se o pedido não for encontrado ou não estiver no status esperado.
	 */
	public PedidoResponse aplicarEntregaAceita(EntregaAceitaDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.VALIDADO, PedidoStatus.ENTREGA_ACEITA);
	}

	/**
	 * Consome o evento pedido.retirado e aplica a transição ENTREGA_ACEITA -> EM_ENTREGA.
	 *
	 * @param dados Payload do evento pedido.retirado.
	 * @return Pedido atualizado.
	 * @throws ResponseStatusException Se o pedido não for encontrado ou não estiver no status esperado.
	 */
	public PedidoResponse aplicarPedidoRetirado(PedidoRetiradoDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.ENTREGA_ACEITA, PedidoStatus.EM_ENTREGA);
	}

	/**
	 * Consome o evento pedido.entregue e aplica a transição EM_ENTREGA -> ENTREGUE.
	 *
	 * @param dados Payload do evento pedido.entregue.
	 * @return Pedido atualizado.
	 * @throws ResponseStatusException Se o pedido não for encontrado ou não estiver no status esperado.
	 */
	public PedidoResponse aplicarPedidoEntregue(PedidoEntregueDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.EM_ENTREGA, PedidoStatus.ENTREGUE);
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

	/**
	 * Lista os pedidos de uma loja que ainda aguardam validação (painel da loja).
	 *
	 * @param lojaId ID da loja.
	 * @return Lista de pedidos pendentes da loja.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public List<PedidoResponse> listarPendentesPorLoja(Long lojaId) {
		lojaService.buscarPorId(lojaId);
		return pedidoRepository.findByLojaIdAndStatus(lojaId, PedidoStatus.AGUARDANDO_VALIDACAO).stream()
				.map(pedido -> paraResponse(pedido, itemPedidoRepository.findByPedidoId(pedido.getId())))
				.toList();
	}

	private Pedido buscarEntidade(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
	}

	private PedidoResponse aplicarTransicao(String pedidoIdTexto, PedidoStatus statusEsperado,
			PedidoStatus novoStatus) {
		Pedido pedido = buscarEntidade(parseId(pedidoIdTexto));
		try {
			pedido.transicionar(statusEsperado, novoStatus);
		} catch (IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		Pedido salvo = pedidoRepository.save(pedido);
		log.info("Pedido {} transicionou de {} para {}", salvo.getId(), statusEsperado, novoStatus);
		return paraResponse(salvo, itemPedidoRepository.findByPedidoId(salvo.getId()));
	}

	private Long parseId(String texto) {
		try {
			return Long.valueOf(texto);
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pedidoId inválido: " + texto);
		}
	}

	private PedidoResponse paraResponse(Pedido pedido, List<ItemPedido> itens) {
		List<ItemPedidoResponse> itensResponse = itens.stream().map(ItemPedidoResponse::from).toList();
		EnderecoEntregaResponse enderecoEntrega = new EnderecoEntregaResponse(pedido.getEnderecoRua(),
				pedido.getEnderecoLat(), pedido.getEnderecoLng());
		return new PedidoResponse(pedido.getId(), pedido.getClienteId(), pedido.getLojaId(), itensResponse,
				pedido.getTotal(), enderecoEntrega, pedido.getStatus(), pedido.getDataCriacao());
	}
}
