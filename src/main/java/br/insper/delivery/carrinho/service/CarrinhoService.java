package br.insper.delivery.carrinho.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.carrinho.domain.ItemCarrinho;
import br.insper.delivery.carrinho.dto.CarrinhoResponse;
import br.insper.delivery.carrinho.dto.ItemCarrinhoRequest;
import br.insper.delivery.carrinho.dto.ItemCarrinhoResponse;
import br.insper.delivery.carrinho.event.ItemAdicionadoEvent;
import br.insper.delivery.carrinho.event.ItemRemovidoEvent;
import br.insper.delivery.carrinho.repository.ItemCarrinhoRepository;
import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.service.ProdutoService;

/**
 * Serviço para gerenciar o carrinho de compras de um cliente.
 */
@Service
public class CarrinhoService {

	private final ItemCarrinhoRepository itemCarrinhoRepository;
	private final ClienteService clienteService;
	private final ProdutoService produtoService;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe CarrinhoService.
	 *
	 * @param itemCarrinhoRepository Repositório de itens de carrinho.
	 * @param clienteService         Serviço de clientes, usado para validar o dono do carrinho.
	 * @param produtoService         Serviço de produtos, usado para validar e precificar os itens.
	 * @param eventPublisher         Publicador de eventos.
	 */
	public CarrinhoService(ItemCarrinhoRepository itemCarrinhoRepository, ClienteService clienteService,
			ProdutoService produtoService, ApplicationEventPublisher eventPublisher) {
		this.itemCarrinhoRepository = itemCarrinhoRepository;
		this.clienteService = clienteService;
		this.produtoService = produtoService;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Adiciona um item ao carrinho do cliente. Se o produto já estiver no carrinho, a quantidade é
	 * somada à existente e o preço unitário é atualizado para o preço atual do produto.
	 *
	 * @param clienteId ID do cliente dono do carrinho.
	 * @param request   Produto e quantidade a serem adicionados.
	 * @return Carrinho atualizado, com itens e total.
	 * @throws ResponseStatusException Se o cliente ou o produto não forem encontrados.
	 */
	public CarrinhoResponse adicionarItem(Long clienteId, ItemCarrinhoRequest request) {
		clienteService.buscarPorId(clienteId);
		Produto produto = produtoService.buscarPorId(request.produtoId());

		ItemCarrinho item = itemCarrinhoRepository.findByClienteIdAndProdutoId(clienteId, request.produtoId())
				.map(existente -> {
					existente.adicionarQuantidade(request.quantidade(), produto.getPreco());
					return existente;
				})
				.orElseGet(() -> new ItemCarrinho(clienteId, request.produtoId(), request.quantidade(),
						produto.getPreco()));

		ItemCarrinho salvo = itemCarrinhoRepository.save(item);
		eventPublisher.publishEvent(new ItemAdicionadoEvent(this, salvo));
		return montarCarrinho(clienteId);
	}

	/**
	 * Remove um item do carrinho do cliente.
	 *
	 * @param clienteId ID do cliente dono do carrinho.
	 * @param itemId    ID do item a ser removido.
	 * @return Carrinho atualizado, com itens e total.
	 * @throws ResponseStatusException Se o cliente ou o item não forem encontrados.
	 */
	public CarrinhoResponse removerItem(Long clienteId, Long itemId) {
		clienteService.buscarPorId(clienteId);
		ItemCarrinho item = buscarItem(clienteId, itemId);

		itemCarrinhoRepository.delete(item);
		eventPublisher.publishEvent(new ItemRemovidoEvent(this, item));
		return montarCarrinho(clienteId);
	}

	/**
	 * Busca o carrinho do cliente, com os itens e o total calculado.
	 *
	 * @param clienteId ID do cliente dono do carrinho.
	 * @return Carrinho do cliente.
	 * @throws ResponseStatusException Se o cliente não for encontrado.
	 */
	public CarrinhoResponse buscarCarrinho(Long clienteId) {
		clienteService.buscarPorId(clienteId);
		return montarCarrinho(clienteId);
	}

	private ItemCarrinho buscarItem(Long clienteId, Long itemId) {
		ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
		if (!item.getClienteId().equals(clienteId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado");
		}
		return item;
	}

	private CarrinhoResponse montarCarrinho(Long clienteId) {
		List<ItemCarrinho> itens = itemCarrinhoRepository.findByClienteId(clienteId);
		List<ItemCarrinhoResponse> itensResponse = itens.stream().map(ItemCarrinhoResponse::from).toList();
		BigDecimal total = itens.stream().map(ItemCarrinho::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
		return new CarrinhoResponse(clienteId, itensResponse, total);
	}
}
