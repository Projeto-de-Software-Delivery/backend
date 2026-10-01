package br.insper.delivery.produto.service;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.service.LojaService;
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
	private final LojaService lojaService;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe ProdutoService.
	 *
	 * @param produtoRepository Repositório de produtos.
	 * @param lojaService       Serviço de lojas, usado para validar a loja dona do produto.
	 * @param eventPublisher    Publicador de eventos.
	 */
	public ProdutoService(ProdutoRepository produtoRepository, LojaService lojaService,
			ApplicationEventPublisher eventPublisher) {
		this.produtoRepository = produtoRepository;
		this.lojaService = lojaService;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria um novo produto para uma loja.
	 *
	 * @param request Dados do produto a ser criado.
	 * @return Produto criado.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public Produto criar(ProdutoRequest request) {
		lojaService.buscarPorId(request.lojaId());
		Produto produto = new Produto(request.lojaId(), request.nome(), request.categoria(), request.preco(),
				request.estoque(), request.foto());
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
	 * Lista os produtos de uma loja.
	 *
	 * @param lojaId ID da loja.
	 * @return Lista de produtos da loja.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public List<Produto> listarPorLoja(Long lojaId) {
		lojaService.buscarPorId(lojaId);
		return produtoRepository.findByLojaId(lojaId);
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
	 * Atualiza os dados de um produto existente. A loja dona do produto não pode ser alterada.
	 *
	 * @param id      ID do produto a ser atualizado.
	 * @param request Novos dados do produto.
	 * @return Produto atualizado.
	 * @throws ResponseStatusException Se o produto não for encontrado ou se a requisição tentar
	 *                                  mover o produto para outra loja.
	 */
	public Produto atualizar(Long id, ProdutoRequest request) {
		Produto produto = buscarPorId(id);
		if (!produto.getLojaId().equals(request.lojaId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Não é possível mover um produto para outra loja");
		}
		produto.atualizar(request.nome(), request.categoria(), request.preco(), request.estoque(), request.foto());
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

	/**
	 * Baixa o estoque dos produtos informados. A operação é tudo-ou-nada: se algum produto não
	 * tiver estoque suficiente, nenhum estoque é alterado.
	 *
	 * @param quantidadesPorProduto Quantidade a baixar por ID de produto.
	 * @throws ResponseStatusException Se algum produto não for encontrado ou não tiver estoque
	 *                                  suficiente.
	 */
	@Transactional
	public void baixarEstoque(Map<Long, Integer> quantidadesPorProduto) {
		record Baixa(Produto produto, Integer quantidade) {
		}

		List<Baixa> baixas = quantidadesPorProduto.entrySet().stream()
				.map(entrada -> new Baixa(buscarPorId(entrada.getKey()), entrada.getValue()))
				.toList();

		for (Baixa baixa : baixas) {
			if (baixa.produto().getEstoque() < baixa.quantidade()) {
				throw new ResponseStatusException(HttpStatus.CONFLICT,
						"Estoque insuficiente para o produto " + baixa.produto().getNome());
			}
		}

		for (Baixa baixa : baixas) {
			baixa.produto().baixarEstoque(baixa.quantidade());
			produtoRepository.save(baixa.produto());
		}
	}
}
