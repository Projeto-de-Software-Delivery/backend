package br.insper.delivery.loja.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.domain.EstoqueLoja;
import br.insper.delivery.loja.domain.Loja;
import br.insper.delivery.loja.dto.EstoqueLojaRequest;
import br.insper.delivery.loja.repository.EstoqueLojaRepository;
import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.service.ProdutoService;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class EstoqueLojaServiceTest {

	@Mock
	private EstoqueLojaRepository estoqueLojaRepository;

	@Mock
	private LojaService lojaService;

	@Mock
	private ProdutoService produtoService;

	@InjectMocks
	private EstoqueLojaService estoqueLojaService;

	private static final Loja LOJA = new Loja("Padaria", "12345678000199", "Rua A, 1");
	private static final Produto PRODUTO = new Produto("Bolo", "Sobremesas", new BigDecimal("15.90"), "foto.png");

	// ──────────────────────────────────────────────
	// adicionarOuAtualizar
	// ──────────────────────────────────────────────

	@Test
	void adicionarOuAtualizarDeveCriarNovoRegistroQuandoProdutoNaoExisteNoEstoque() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		when(produtoService.buscarPorId(5L)).thenReturn(PRODUTO);
		when(estoqueLojaRepository.findByLojaIdAndProdutoId(1L, 5L)).thenReturn(Optional.empty());
		EstoqueLoja salvo = new EstoqueLoja(1L, 5L, 10);
		when(estoqueLojaRepository.save(any(EstoqueLoja.class))).thenReturn(salvo);

		EstoqueLoja resultado = estoqueLojaService.adicionarOuAtualizar(1L, new EstoqueLojaRequest(5L, 10));

		assertThat(resultado.getLojaId()).isEqualTo(1L);
		assertThat(resultado.getProdutoId()).isEqualTo(5L);
		assertThat(resultado.getQuantidade()).isEqualTo(10);
		verify(estoqueLojaRepository).save(any(EstoqueLoja.class));
	}

	@Test
	void adicionarOuAtualizarDeveAtualizarQuantidadeQuandoProdutoJaExisteNoEstoque() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		when(produtoService.buscarPorId(5L)).thenReturn(PRODUTO);
		EstoqueLoja existente = new EstoqueLoja(1L, 5L, 3);
		when(estoqueLojaRepository.findByLojaIdAndProdutoId(1L, 5L)).thenReturn(Optional.of(existente));
		when(estoqueLojaRepository.save(existente)).thenReturn(existente);

		EstoqueLoja resultado = estoqueLojaService.adicionarOuAtualizar(1L, new EstoqueLojaRequest(5L, 20));

		assertThat(resultado.getQuantidade()).isEqualTo(20);
		verify(estoqueLojaRepository).save(existente);
	}

	@Test
	void adicionarOuAtualizarDeveLancarQuandoLojaNaoEncontrada() {
		when(lojaService.buscarPorId(1L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));

		Assertions.assertThrows(ResponseStatusException.class,
				() -> estoqueLojaService.adicionarOuAtualizar(1L, new EstoqueLojaRequest(5L, 10)));
	}

	@Test
	void adicionarOuAtualizarDeveLancarQuandoProdutoNaoEncontrado() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		when(produtoService.buscarPorId(5L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

		Assertions.assertThrows(ResponseStatusException.class,
				() -> estoqueLojaService.adicionarOuAtualizar(1L, new EstoqueLojaRequest(5L, 10)));
	}

	// ──────────────────────────────────────────────
	// listarPorLoja
	// ──────────────────────────────────────────────

	@Test
	void listarPorLojaDeveRetornarItensDoRepositorio() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		EstoqueLoja item = new EstoqueLoja(1L, 5L, 10);
		when(estoqueLojaRepository.findByLojaId(1L)).thenReturn(List.of(item));

		List<EstoqueLoja> resultado = estoqueLojaService.listarPorLoja(1L);

		assertThat(resultado).hasSize(1);
		assertThat(resultado.get(0).getProdutoId()).isEqualTo(5L);
	}

	@Test
	void listarPorLojaDeveRetornarListaVaziaQuandoSemEstoque() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		when(estoqueLojaRepository.findByLojaId(1L)).thenReturn(List.of());

		List<EstoqueLoja> resultado = estoqueLojaService.listarPorLoja(1L);

		assertThat(resultado).isEmpty();
	}

	@Test
	void listarPorLojaDeveLancarQuandoLojaNaoEncontrada() {
		when(lojaService.buscarPorId(1L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));

		Assertions.assertThrows(ResponseStatusException.class,
				() -> estoqueLojaService.listarPorLoja(1L));
	}

	// ──────────────────────────────────────────────
	// remover
	// ──────────────────────────────────────────────

	@Test
	void removerDeveExcluirRegistroExistente() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		EstoqueLoja item = new EstoqueLoja(1L, 5L, 10);
		when(estoqueLojaRepository.findByLojaIdAndProdutoId(1L, 5L)).thenReturn(Optional.of(item));

		estoqueLojaService.remover(1L, 5L);

		verify(estoqueLojaRepository).delete(item);
	}

	@Test
	void removerDeveLancarQuandoLojaNaoEncontrada() {
		when(lojaService.buscarPorId(1L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));

		Assertions.assertThrows(ResponseStatusException.class,
				() -> estoqueLojaService.remover(1L, 5L));
	}

	@Test
	void removerDeveLancarQuandoProdutoNaoEstaNoEstoqueDaLoja() {
		when(lojaService.buscarPorId(1L)).thenReturn(LOJA);
		when(estoqueLojaRepository.findByLojaIdAndProdutoId(1L, 5L)).thenReturn(Optional.empty());

		ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
				() -> estoqueLojaService.remover(1L, 5L));

		assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// ──────────────────────────────────────────────
	// buscarPorLojaEProduto
	// ──────────────────────────────────────────────

	@Test
	void buscarPorLojaEProdutoDeveRetornarRegistroExistente() {
		EstoqueLoja item = new EstoqueLoja(1L, 5L, 8);
		when(estoqueLojaRepository.findByLojaIdAndProdutoId(1L, 5L)).thenReturn(Optional.of(item));

		EstoqueLoja resultado = estoqueLojaService.buscarPorLojaEProduto(1L, 5L);

		assertThat(resultado.getQuantidade()).isEqualTo(8);
	}

	@Test
	void buscarPorLojaEProdutoDeveLancarUnprocessableEntityQuandoNaoEncontrado() {
		when(estoqueLojaRepository.findByLojaIdAndProdutoId(1L, 5L)).thenReturn(Optional.empty());

		ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
				() -> estoqueLojaService.buscarPorLojaEProduto(1L, 5L));

		assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	// ──────────────────────────────────────────────
	// temEstoqueSuficiente (comportamento da entidade)
	// ──────────────────────────────────────────────

	@Test
	void temEstoqueSuficienteDeveRetornarTrueQuandoQuantidadeIgualAoEstoque() {
		EstoqueLoja item = new EstoqueLoja(1L, 5L, 5);
		assertThat(item.temEstoqueSuficiente(5)).isTrue();
	}

	@Test
	void temEstoqueSuficienteDeveRetornarTrueQuandoQuantidadeMenorQueEstoque() {
		EstoqueLoja item = new EstoqueLoja(1L, 5L, 10);
		assertThat(item.temEstoqueSuficiente(3)).isTrue();
	}

	@Test
	void temEstoqueSuficienteDeveRetornarFalseQuandoQuantidadeMaiorQueEstoque() {
		EstoqueLoja item = new EstoqueLoja(1L, 5L, 2);
		assertThat(item.temEstoqueSuficiente(5)).isFalse();
	}
}
