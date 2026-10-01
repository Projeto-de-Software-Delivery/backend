package br.insper.delivery.loja.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.domain.EstoqueLoja;
import br.insper.delivery.loja.dto.EstoqueLojaRequest;
import br.insper.delivery.loja.repository.EstoqueLojaRepository;
import br.insper.delivery.produto.service.ProdutoService;

/**
 * Serviço para gerenciar o estoque de produtos em cada loja.
 */
@Service
public class EstoqueLojaService {

	private final EstoqueLojaRepository estoqueLojaRepository;
	private final LojaService lojaService;
	private final ProdutoService produtoService;

	/**
	 * Construtor da classe EstoqueLojaService.
	 *
	 * @param estoqueLojaRepository Repositório de estoque.
	 * @param lojaService           Serviço de lojas, usado para validar a loja.
	 * @param produtoService        Serviço de produtos, usado para validar o produto.
	 */
	public EstoqueLojaService(EstoqueLojaRepository estoqueLojaRepository, LojaService lojaService,
			ProdutoService produtoService) {
		this.estoqueLojaRepository = estoqueLojaRepository;
		this.lojaService = lojaService;
		this.produtoService = produtoService;
	}

	/**
	 * Adiciona ou atualiza o estoque de um produto em uma loja.
	 * Se o produto já existir no estoque da loja, a quantidade é atualizada.
	 * Caso contrário, um novo registro de estoque é criado.
	 *
	 * @param lojaId  ID da loja.
	 * @param request Produto e quantidade a adicionar ao estoque.
	 * @return Registro de estoque salvo.
	 * @throws ResponseStatusException Se a loja ou o produto não forem encontrados.
	 */
	public EstoqueLoja adicionarOuAtualizar(Long lojaId, EstoqueLojaRequest request) {
		lojaService.buscarPorId(lojaId);
		produtoService.buscarPorId(request.produtoId());

		EstoqueLoja estoque = estoqueLojaRepository
				.findByLojaIdAndProdutoId(lojaId, request.produtoId())
				.orElse(new EstoqueLoja(lojaId, request.produtoId(), request.quantidade()));

		estoque.atualizarQuantidade(request.quantidade());
		return estoqueLojaRepository.save(estoque);
	}

	/**
	 * Lista todos os itens do estoque de uma loja.
	 *
	 * @param lojaId ID da loja.
	 * @return Lista de registros de estoque da loja.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public List<EstoqueLoja> listarPorLoja(Long lojaId) {
		lojaService.buscarPorId(lojaId);
		return estoqueLojaRepository.findByLojaId(lojaId);
	}

	/**
	 * Remove um item do estoque de uma loja.
	 *
	 * @param lojaId    ID da loja.
	 * @param produtoId ID do produto a remover do estoque.
	 * @throws ResponseStatusException Se a loja não for encontrada ou o produto não
	 *                                 estiver no estoque da loja.
	 */
	public void remover(Long lojaId, Long produtoId) {
		lojaService.buscarPorId(lojaId);
		EstoqueLoja estoque = estoqueLojaRepository
				.findByLojaIdAndProdutoId(lojaId, produtoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Produto não encontrado no estoque da loja"));
		estoqueLojaRepository.delete(estoque);
	}

	/**
	 * Busca o registro de estoque de um produto específico em uma loja.
	 * Método de uso interno (ex.: validação no PedidoService).
	 *
	 * @param lojaId    ID da loja.
	 * @param produtoId ID do produto.
	 * @return Registro de estoque encontrado.
	 * @throws ResponseStatusException Se o produto não estiver no estoque da loja.
	 */
	public EstoqueLoja buscarPorLojaEProduto(Long lojaId, Long produtoId) {
		return estoqueLojaRepository
				.findByLojaIdAndProdutoId(lojaId, produtoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
						"Produto " + produtoId + " não disponível na loja " + lojaId));
	}
}
