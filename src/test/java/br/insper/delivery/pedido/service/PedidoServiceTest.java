package br.insper.delivery.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.domain.PedidoStatus;
import br.insper.delivery.pedido.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private ClienteService clienteService;

	@InjectMocks
	private PedidoService pedidoService;

	@Test
	void buscarPorIdDeveRetornarPedidoComStatusRecebido() {
		Pedido pedido = new Pedido(1L, 2L, new BigDecimal("59.90"));
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

		Pedido resultado = pedidoService.buscarPorId(1L);

		assertThat(resultado.getStatus()).isEqualTo(PedidoStatus.RECEBIDO);
		assertThat(resultado.getValorTotal()).isEqualByComparingTo("59.90");
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> pedidoService.buscarPorId(1L));
	}

	@Test
	void listarPorClienteDeveRetornarPedidosDoRepositorio() {
		Cliente cliente = new Cliente("Ana", "ana@email.com", "11999999999");
		when(clienteService.buscarPorId(1L)).thenReturn(cliente);
		Pedido pedido = new Pedido(1L, 2L, new BigDecimal("59.90"));
		when(pedidoRepository.findByClienteId(1L)).thenReturn(List.of(pedido));

		List<Pedido> resultado = pedidoService.listarPorCliente(1L);

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
