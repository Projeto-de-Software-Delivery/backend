package br.insper.delivery.produto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.domain.Loja;
import br.insper.delivery.loja.service.LojaService;
import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.dto.ProdutoRequest;
import br.insper.delivery.produto.event.ProdutoCriadoEvent;
import br.insper.delivery.produto.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private LojaService lojaService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ProdutoService produtoService;

	private static final Loja LOJA = new Loja("Padaria", "12345678000199", "Rua A, 1");

	@Test
	void criarDevePersistirEPublicarEvento() {
		when(lojaService.buscarPorId(7L)).thenReturn(LOJA);
		ProdutoRequest request = new ProdutoRequest(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
				10, "http://exemplo.com/foto.png");
		Produto salvo = new Produto(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		when(produtoRepository.save(any(Produto.class))).thenReturn(salvo);

		Produto resultado = produtoService.criar(request);

		assertThat(resultado.getNome()).isEqualTo("Bolo de chocolate");
		assertThat(resultado.getLojaId()).isEqualTo(7L);
		assertThat(resultado.getEstoque()).isEqualTo(10);
		verify(eventPublisher).publishEvent(any(ProdutoCriadoEvent.class));
	}

	@Test
	void criarDeveLancarQuandoLojaNaoEncontrada() {
		when(lojaService.buscarPorId(7L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));
		ProdutoRequest request = new ProdutoRequest(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
				10, "http://exemplo.com/foto.png");

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> produtoService.criar(request));
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> produtoService.buscarPorId(1L));
	}

	@Test
	void listarTodosDeveRetornarProdutosDoRepositorio() {
		Produto produto = new Produto(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		when(produtoRepository.findAll()).thenReturn(List.of(produto));

		List<Produto> resultado = produtoService.listarTodos();

		assertThat(resultado).hasSize(1);
	}

	@Test
	void listarPorLojaDeveRetornarProdutosDaLoja() {
		when(lojaService.buscarPorId(7L)).thenReturn(LOJA);
		Produto produto = new Produto(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		when(produtoRepository.findByLojaId(7L)).thenReturn(List.of(produto));

		List<Produto> resultado = produtoService.listarPorLoja(7L);

		assertThat(resultado).hasSize(1);
	}

	@Test
	void atualizarDeveAlterarDadosDoProdutoExistente() {
		Produto produto = new Produto(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		ProdutoRequest request = new ProdutoRequest(7L, "Bolo de cenoura", "Sobremesas", new BigDecimal("19.90"), 5,
				"http://exemplo.com/nova-foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
		when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

		Produto resultado = produtoService.atualizar(1L, request);

		assertThat(resultado.getNome()).isEqualTo("Bolo de cenoura");
		assertThat(resultado.getPreco()).isEqualTo(new BigDecimal("19.90"));
		assertThat(resultado.getEstoque()).isEqualTo(5);
	}

	@Test
	void atualizarDeveLancarQuandoNaoEncontrado() {
		ProdutoRequest request = new ProdutoRequest(7L, "Bolo de cenoura", "Sobremesas", new BigDecimal("19.90"), 5,
				"http://exemplo.com/nova-foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> produtoService.atualizar(1L, request));
	}

	@Test
	void atualizarDeveLancarQuandoTentaMudarDeLoja() {
		Produto produto = new Produto(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		ProdutoRequest request = new ProdutoRequest(9L, "Bolo de cenoura", "Sobremesas", new BigDecimal("19.90"), 5,
				"http://exemplo.com/nova-foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

		ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(
				ResponseStatusException.class, () -> produtoService.atualizar(1L, request));

		assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void deletarDeveRemoverProdutoExistente() {
		Produto produto = new Produto(7L, "Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

		produtoService.deletar(1L);

		verify(produtoRepository).delete(produto);
	}

	@Test
	void deletarDeveLancarQuandoNaoEncontrado() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> produtoService.deletar(1L));
	}

	@Test
	void baixarEstoqueDeveDecrementarQuandoHaEstoqueSuficiente() {
		Produto produto = new Produto(7L, "Bolo", "Sobremesas", new BigDecimal("25.90"), 10,
				"http://exemplo.com/foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
		when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

		produtoService.baixarEstoque(Map.of(1L, 3));

		assertThat(produto.getEstoque()).isEqualTo(7);
		verify(produtoRepository).save(produto);
	}

	@Test
	void baixarEstoqueDeveLancarConflitoQuandoEstoqueInsuficiente() {
		Produto produto = new Produto(7L, "Bolo", "Sobremesas", new BigDecimal("25.90"), 2,
				"http://exemplo.com/foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

		ResponseStatusException exception = org.junit.jupiter.api.Assertions.assertThrows(
				ResponseStatusException.class, () -> produtoService.baixarEstoque(Map.of(1L, 3)));

		assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(produto.getEstoque()).isEqualTo(2);
	}
}
