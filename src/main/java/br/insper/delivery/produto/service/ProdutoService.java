package br.insper.delivery.produto.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.dto.ProdutoRequest;
import br.insper.delivery.produto.event.ProdutoCriadoEvent;
import br.insper.delivery.produto.repository.ProdutoRepository;

/**
 * Serviço para gerenciar produtos do catálogo.
 */
@Service
public class ProdutoService {

	private final ProdutoRepository produtoRepository;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe ProdutoService.
	 *
	 * @param produtoRepository Repositório de produtos.
	 * @param eventPublisher    Publicador de eventos.
	 */
	public ProdutoService(ProdutoRepository produtoRepository, ApplicationEventPublisher eventPublisher) {
		this.produtoRepository = produtoRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria um novo produto.
	 *
	 * @param request Dados do produto a ser criado.
	 * @return Produto criado.
	 */
	public Produto criar(ProdutoRequest request) {
		Produto produto = new Produto(request.nome(), request.categoria(), request.preco(), request.foto());
		Produto salvo = produtoRepository.save(produto);
		eventPublisher.publishEvent(new ProdutoCriadoEvent(this, salvo));
		return salvo;
	}

	/**
	 * Lista todos os produtos do catálogo.
	 *
	 * @return Lista de produtos.
	 */
	public List<Produto> listarTodos() {
		return produtoRepository.findAll();
	}

	/**
	 * Busca um produto pelo seu ID.
	 *
	 * @param id ID do produto a ser buscado.
	 * @return Produto encontrado.
	 * @throws ResponseStatusException Se o produto não for encontrado.
	 */
	public Produto buscarPorId(Long id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
	}

	/**
	 * Atualiza os dados de um produto existente.
	 *
	 * @param id      ID do produto a ser atualizado.
	 * @param request Novos dados do produto.
	 * @return Produto atualizado.
	 * @throws ResponseStatusException Se o produto não for encontrado.
	 */
	public Produto atualizar(Long id, ProdutoRequest request) {
		Produto produto = buscarPorId(id);
		produto.atualizar(request.nome(), request.categoria(), request.preco(), request.foto());
		return produtoRepository.save(produto);
	}

	/**
	 * Remove um produto do catálogo.
	 *
	 * @param id ID do produto a ser removido.
	 * @throws ResponseStatusException Se o produto não for encontrado.
	 */
	public void deletar(Long id) {
		Produto produto = buscarPorId(id);
		produtoRepository.delete(produto);
	}
}
