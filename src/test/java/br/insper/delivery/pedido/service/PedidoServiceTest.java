package br.insper.delivery.pedido.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.loja.domain.Loja;
import br.insper.delivery.loja.service.LojaService;
import br.insper.delivery.pedido.domain.ItemPedido;
import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.domain.PedidoStatus;
import br.insper.delivery.pedido.dto.CriarPedidoRequest;
import br.insper.delivery.pedido.dto.EnderecoEntregaRequest;
import br.insper.delivery.pedido.dto.ItemPedidoRequest;
import br.insper.delivery.pedido.dto.PedidoResponse;
import br.insper.delivery.pedido.event.PedidoCriadoEvent;
import br.insper.delivery.pedido.repository.ItemPedidoRepository;
import br.insper.delivery.pedido.repository.PedidoRepository;
import br.insper.delivery.produto.domain.Produto;
import br.insper.delivery.produto.service.ProdutoService;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private ItemPedidoRepository itemPedidoRepository;

	@Mock
	private ClienteService clienteService;

	@Mock
	private LojaService lojaService;

	@Mock
	private ProdutoService produtoService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private PedidoService pedidoService;

	private static final Cliente CLIENTE = new Cliente("Ana", "ana@email.com", "11999999999");
	private static final Loja LOJA = new Loja("Padaria", "12345678000199", "Rua A, 1");

	private CriarPedidoRequest requestPadrao() {
		return new CriarPedidoRequest(2L, List.of(new ItemPedidoRequest(9L, 2)),
				new EnderecoEntregaRequest("Rua B, 2", -23.5, -46.6));
	}

	@Test
	void criarDevePersistirComStatusAguardandoValidacaoEPublicarEvento() {
		when(clienteService.buscarPorId(1L)).thenReturn(CLIENTE);
		when(lojaService.buscarPorId(2L)).thenReturn(LOJA);
		Produto produto = new Produto("Bolo", "Sobremesas", new BigDecimal("15.90"), "foto.png");
		when(produtoService.buscarPorId(9L)).thenReturn(produto);
		Pedido salvo = new Pedido(1L, 2L, new BigDecimal("31.80"), "Rua B, 2", -23.5, -46.6);
		when(pedidoRepository.save(any(Pedido.class))).thenReturn(salvo);
		ItemPedido itemSalvo = new ItemPedido(salvo.getId(), 9L, 2, new BigDecimal("15.90"));
		when(itemPedidoRepository.save(any(ItemPedido.class))).thenReturn(itemSalvo);

		PedidoResponse response = pedidoService.criar(1L, requestPadrao());

		assertThat(response.status()).isEqualTo(PedidoStatus.AGUARDANDO_VALIDACAO);
		assertThat(response.total()).isEqualByComparingTo("31.80");
		assertThat(response.itens()).hasSize(1);
		assertThat(response.enderecoEntrega().rua()).isEqualTo("Rua B, 2");
		verify(eventPublisher).publishEvent(any(PedidoCriadoEvent.class));
	}

	@Test
	void criarDeveLancarQuandoClienteNaoEncontrado() {
		when(clienteService.buscarPorId(1L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> pedidoService.criar(1L, requestPadrao()));
	}

	@Test
	void criarDeveLancarQuandoLojaNaoEncontrada() {
		when(clienteService.buscarPorId(1L)).thenReturn(CLIENTE);
		when(lojaService.buscarPorId(2L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> pedidoService.criar(1L, requestPadrao()));
	}

	@Test
	void criarDeveLancarQuandoProdutoNaoEncontrado() {
		when(clienteService.buscarPorId(1L)).thenReturn(CLIENTE);
		when(lojaService.buscarPorId(2L)).thenReturn(LOJA);
		when(produtoService.buscarPorId(9L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> pedidoService.criar(1L, requestPadrao()));
	}

	@Test
	void buscarPorIdDeveRetornarPedidoComItens() {
		Pedido pedido = new Pedido(1L, 2L, new BigDecimal("31.80"), "Rua B, 2", -23.5, -46.6);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
		ItemPedido item = new ItemPedido(1L, 9L, 2, new BigDecimal("15.90"));
		when(itemPedidoRepository.findByPedidoId(pedido.getId())).thenReturn(List.of(item));

		PedidoResponse resultado = pedidoService.buscarPorId(1L);

		assertThat(resultado.status()).isEqualTo(PedidoStatus.AGUARDANDO_VALIDACAO);
		assertThat(resultado.itens()).hasSize(1);
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> pedidoService.buscarPorId(1L));
	}

	@Test
	void buscarStatusDeveRetornarStatusDoPedido() {
		Pedido pedido = new Pedido(1L, 2L, new BigDecimal("31.80"), "Rua B, 2", -23.5, -46.6);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

		PedidoStatus status = pedidoService.buscarStatus(1L);

		assertThat(status).isEqualTo(PedidoStatus.AGUARDANDO_VALIDACAO);
	}

	@Test
	void listarPorClienteDeveRetornarPedidosDoRepositorio() {
		when(clienteService.buscarPorId(1L)).thenReturn(CLIENTE);
		Pedido pedido = new Pedido(1L, 2L, new BigDecimal("31.80"), "Rua B, 2", -23.5, -46.6);
		when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));
		when(itemPedidoRepository.findByPedidoId(pedido.getId())).thenReturn(List.of());

		List<PedidoResponse> resultado = pedidoService.listarPorCliente(1L);

		assertThat(resultado).hasSize(1);
	}

	@Test
	void listarPorClienteDeveLancarQuandoClienteNaoEncontrado() {
		when(clienteService.buscarPorId(1L))
				.thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> pedidoService.listarPorCliente(1L));
	}
}
