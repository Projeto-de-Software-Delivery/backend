package br.insper.delivery.pedido.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.loja.service.LojaService;
import br.insper.delivery.pedido.domain.ItemPedido;
import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.domain.PedidoStatus;
import br.insper.delivery.pedido.dto.AceitarPedidoRequest;
import br.insper.delivery.pedido.dto.CriarPedidoRequest;
import br.insper.delivery.pedido.dto.EnderecoEntregaRequest;
import br.insper.delivery.pedido.dto.EnderecoEntregaResponse;
import br.insper.delivery.pedido.dto.EntregaAceitaDados;
import br.insper.delivery.pedido.dto.ItemPedidoResponse;
import br.insper.delivery.pedido.dto.PedidoEntregueDados;
import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.dto.PedidoRetiradoDados;
import br.insper.delivery.pedido.dto.PedidoValidadoDados;
import br.insper.delivery.pedido.event.PedidoCriadoEvent;
import br.insper.delivery.pedido.event.PedidoCriadoEvento;
import br.insper.delivery.pedido.event.PedidoValidadoEvent;
import br.insper.delivery.pedido.event.PedidoValidadoEvento;
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

	/** Status inicial AGUARDANDO_VALIDACAO; publica pedido.criado. */
	public PedidoResponse criar(Long clienteId, CriarPedidoRequest request) {
		clienteService.buscarPorId(clienteId);
		lojaService.buscarPorId(request.lojaId());

		record ItemResolvido(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
		}

		List<ItemResolvido> resolvidos = request.itens().stream()
				.map(itemRequest -> {
					Produto produto = produtoService.buscarPorId(itemRequest.produtoId());
					if (!produto.getLojaId().equals(request.lojaId())) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Produto " + produto.getId() + " não pertence à loja informada");
					}
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

	public PedidoResponse buscarPorId(Long id) {
		Pedido pedido = buscarEntidade(id);
		return paraResponse(pedido, itemPedidoRepository.findByPedidoId(pedido.getId()));
	}

	public PedidoStatus buscarStatus(Long id) {
		return buscarEntidade(id).getStatus();
	}

	public PedidoResponse aplicarPedidoValidado(PedidoValidadoDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.AGUARDANDO_VALIDACAO, PedidoStatus.VALIDADO);
	}

	public PedidoResponse aplicarEntregaAceita(EntregaAceitaDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.VALIDADO, PedidoStatus.ENTREGA_ACEITA);
	}

	public PedidoResponse aplicarPedidoRetirado(PedidoRetiradoDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.ENTREGA_ACEITA, PedidoStatus.EM_ENTREGA);
	}

	public PedidoResponse aplicarPedidoEntregue(PedidoEntregueDados dados) {
		return aplicarTransicao(dados.pedidoId(), PedidoStatus.EM_ENTREGA, PedidoStatus.ENTREGUE);
	}

	public List<PedidoResponse> listarPorCliente(Long clienteId) {
		clienteService.buscarPorId(clienteId);
		return pedidoRepository.findByClienteId(clienteId).stream()
				.map(pedido -> paraResponse(pedido, itemPedidoRepository.findByPedidoId(pedido.getId())))
				.toList();
	}

	public List<PedidoResponse> listarPendentesPorLoja(Long lojaId) {
		lojaService.buscarPorId(lojaId);
		return pedidoRepository.findByLojaIdAndStatus(lojaId, PedidoStatus.AGUARDANDO_VALIDACAO).stream()
				.map(pedido -> paraResponse(pedido, itemPedidoRepository.findByPedidoId(pedido.getId())))
				.toList();
	}

	/**
	 * Tudo-ou-nada: se a baixa de estoque falhar, nada é persistido (nem o estoque, nem a
	 * transição pra VALIDADO).
	 */
	@Transactional
	public PedidoResponse aceitar(Long lojaId, Long pedidoId, AceitarPedidoRequest request) {
		lojaService.buscarPorId(lojaId);
		Pedido pedido = buscarEntidadeDaLoja(lojaId, pedidoId);
		if (pedido.getStatus() != PedidoStatus.AGUARDANDO_VALIDACAO) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Pedido está em " + pedido.getStatus() + ", esperado AGUARDANDO_VALIDACAO para aceitar");
		}

		List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedido.getId());
		Map<Long, Integer> quantidadesPorProduto = itens.stream()
				.collect(Collectors.toMap(ItemPedido::getProdutoId, ItemPedido::getQuantidade, Integer::sum));
		produtoService.baixarEstoque(quantidadesPorProduto);

		pedido.transicionar(PedidoStatus.AGUARDANDO_VALIDACAO, PedidoStatus.VALIDADO);
		Pedido salvo = pedidoRepository.save(pedido);
		log.info("Pedido {} aceito pela loja {} e transicionou para VALIDADO", salvo.getId(), lojaId);

		EnderecoEntregaRequest enderecoEntrega = new EnderecoEntregaRequest(salvo.getEnderecoRua(),
				salvo.getEnderecoLat(), salvo.getEnderecoLng());
		PedidoValidadoDados dados = new PedidoValidadoDados(String.valueOf(salvo.getId()), String.valueOf(lojaId),
				request.enderecoRetirada(), enderecoEntrega, request.valorFrete(), request.tempoPreparoMin());
		eventPublisher.publishEvent(new PedidoValidadoEvent(this, PedidoValidadoEvento.de(dados)));

		return paraResponse(salvo, itens);
	}

	/** Nenhum estoque é alterado — ele só é baixado na aceitação, nunca antes. */
	public PedidoResponse recusar(Long lojaId, Long pedidoId) {
		lojaService.buscarPorId(lojaId);
		Pedido pedido = buscarEntidadeDaLoja(lojaId, pedidoId);
		try {
			pedido.transicionar(PedidoStatus.AGUARDANDO_VALIDACAO, PedidoStatus.CANCELADO);
		} catch (IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		Pedido salvo = pedidoRepository.save(pedido);
		log.info("Pedido {} recusado pela loja {} e transicionou para CANCELADO", salvo.getId(), lojaId);
		return paraResponse(salvo, itemPedidoRepository.findByPedidoId(salvo.getId()));
	}

	private Pedido buscarEntidadeDaLoja(Long lojaId, Long pedidoId) {
		Pedido pedido = buscarEntidade(pedidoId);
		if (!pedido.getLojaId().equals(lojaId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado para esta loja");
		}
		return pedido;
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
				pedido.getTotal(), enderecoEntrega, pedido.getStatus(), pedido.getDataCriacao(), pedido.getPin());
	}
}
