package br.insper.delivery.entregador.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.entregador.domain.Entregador;
import br.insper.delivery.entregador.domain.StatusDisponibilidade;
import br.insper.delivery.entregador.dto.EntregadorRequest;
import br.insper.delivery.entregador.event.EntregadorCriadoEvent;
import br.insper.delivery.entregador.event.EntregadorStatusAlteradoEvent;
import br.insper.delivery.entregador.repository.EntregadorRepository;

@ExtendWith(MockitoExtension.class)
class EntregadorServiceTest {

	@Mock
	private EntregadorRepository entregadorRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private EntregadorService entregadorService;

	@Test
	void criarDevePersistirComStatusDisponivelEPublicarEvento() {
		EntregadorRequest request = new EntregadorRequest("Joao", "12345678901", "11999999999", "ABC1234");
		Entregador salvo = new Entregador("Joao", "12345678901", "11999999999", "ABC1234");
		when(entregadorRepository.save(any(Entregador.class))).thenReturn(salvo);

		Entregador resultado = entregadorService.criar(request);

		assertThat(resultado.getCpf()).isEqualTo("12345678901");
		assertThat(resultado.getStatus()).isEqualTo(StatusDisponibilidade.DISPONIVEL);
		verify(eventPublisher).publishEvent(any(EntregadorCriadoEvent.class));
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(entregadorRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> entregadorService.buscarPorId(1L));
	}

	@Test
	void listarTodosDeveRetornarEntregadoresDoRepositorio() {
		Entregador entregador = new Entregador("Joao", "12345678901", "11999999999", "ABC1234");
		when(entregadorRepository.findAll()).thenReturn(List.of(entregador));

		List<Entregador> resultado = entregadorService.listarTodos();

		assertThat(resultado).hasSize(1);
	}

	@Test
	void atualizarDeveAlterarDadosCadastraisDoEntregadorExistente() {
		Entregador entregador = new Entregador("Joao", "12345678901", "11999999999", "ABC1234");
		EntregadorRequest request = new EntregadorRequest("Joao Silva", "10987654321", "11988888888", "XYZ9876");
		when(entregadorRepository.findById(1L)).thenReturn(Optional.of(entregador));
		when(entregadorRepository.save(any(Entregador.class))).thenReturn(entregador);

		Entregador resultado = entregadorService.atualizar(1L, request);

		assertThat(resultado.getNome()).isEqualTo("Joao Silva");
		assertThat(resultado.getPlaca()).isEqualTo("XYZ9876");
	}

	@Test
	void atualizarDeveLancarQuandoNaoEncontrado() {
		EntregadorRequest request = new EntregadorRequest("Joao Silva", "10987654321", "11988888888", "XYZ9876");
		when(entregadorRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> entregadorService.atualizar(1L, request));
	}

	@Test
	void atualizarStatusDeveAlterarStatusEPublicarEvento() {
		Entregador entregador = new Entregador("Joao", "12345678901", "11999999999", "ABC1234");
		when(entregadorRepository.findById(1L)).thenReturn(Optional.of(entregador));
		when(entregadorRepository.save(any(Entregador.class))).thenReturn(entregador);

		Entregador resultado = entregadorService.atualizarStatus(1L, StatusDisponibilidade.INDISPONIVEL);

		assertThat(resultado.getStatus()).isEqualTo(StatusDisponibilidade.INDISPONIVEL);
		verify(eventPublisher).publishEvent(any(EntregadorStatusAlteradoEvent.class));
	}

	@Test
	void atualizarStatusDeveLancarQuandoNaoEncontrado() {
		when(entregadorRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> entregadorService.atualizarStatus(1L, StatusDisponibilidade.INDISPONIVEL));
	}

	@Test
	void deletarDeveRemoverEntregadorExistente() {
		Entregador entregador = new Entregador("Joao", "12345678901", "11999999999", "ABC1234");
		when(entregadorRepository.findById(1L)).thenReturn(Optional.of(entregador));

		entregadorService.deletar(1L);

		verify(entregadorRepository).delete(entregador);
	}

	@Test
	void deletarDeveLancarQuandoNaoEncontrado() {
		when(entregadorRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> entregadorService.deletar(1L));
	}
}
