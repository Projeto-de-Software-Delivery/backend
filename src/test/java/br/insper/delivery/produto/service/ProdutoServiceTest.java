package br.insper.delivery.produto.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.dto.ProdutoRequest;
import br.insper.delivery.produto.event.ProdutoCriadoEvent;
import br.insper.delivery.produto.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ProdutoService produtoService;

	@Test
	void criarDevePersistirEPublicarEvento() {
		ProdutoRequest request = new ProdutoRequest("Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
				"http://exemplo.com/foto.png");
		Produto salvo = new Produto("Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
				"http://exemplo.com/foto.png");
		when(produtoRepository.save(any(Produto.class))).thenReturn(salvo);

		Produto resultado = produtoService.criar(request);

		assertThat(resultado.getNome()).isEqualTo("Bolo de chocolate");
		verify(eventPublisher).publishEvent(any(ProdutoCriadoEvent.class));
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> produtoService.buscarPorId(1L));
	}

	@Test
	void listarTodosDeveRetornarProdutosDoRepositorio() {
		Produto produto = new Produto("Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
				"http://exemplo.com/foto.png");
		when(produtoRepository.findAll()).thenReturn(List.of(produto));

		List<Produto> resultado = produtoService.listarTodos();

		assertThat(resultado).hasSize(1);
	}

	@Test
	void atualizarDeveAlterarDadosDoProdutoExistente() {
		Produto produto = new Produto("Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
				"http://exemplo.com/foto.png");
		ProdutoRequest request = new ProdutoRequest("Bolo de cenoura", "Sobremesas", new BigDecimal("19.90"),
				"http://exemplo.com/nova-foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
		when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

		Produto resultado = produtoService.atualizar(1L, request);

		assertThat(resultado.getNome()).isEqualTo("Bolo de cenoura");
		assertThat(resultado.getPreco()).isEqualTo(new BigDecimal("19.90"));
	}

	@Test
	void atualizarDeveLancarQuandoNaoEncontrado() {
		ProdutoRequest request = new ProdutoRequest("Bolo de cenoura", "Sobremesas", new BigDecimal("19.90"),
				"http://exemplo.com/nova-foto.png");
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> produtoService.atualizar(1L, request));
	}

	@Test
	void deletarDeveRemoverProdutoExistente() {
		Produto produto = new Produto("Bolo de chocolate", "Sobremesas", new BigDecimal("25.90"),
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
}
