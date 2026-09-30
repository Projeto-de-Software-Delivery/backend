package br.insper.delivery.pedido.event;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PedidoCriadoListenerTest {

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private PedidoCriadoListener listener;

	private PedidoCriadoEvent eventoDeTeste() {
		PedidoCriadoDados dados = new PedidoCriadoDados("1", "2", "3", List.of(), BigDecimal.TEN, null);
		PedidoCriadoEvento evento = new PedidoCriadoEvento(UUID.randomUUID().toString(), "pedido.criado", 1,
				Instant.now(), dados);
		return new PedidoCriadoEvent(this, evento);
	}

	@Test
	void aoCriarPedidoNaoDeveLancarQuandoSerializacaoFalha() {
		when(objectMapper.writeValueAsString(any())).thenThrow(mock(JacksonException.class));

		assertThatCode(() -> listener.aoCriarPedido(eventoDeTeste())).doesNotThrowAnyException();
	}
}
