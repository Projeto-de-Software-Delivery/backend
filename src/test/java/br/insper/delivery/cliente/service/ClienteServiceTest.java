package br.insper.delivery.cliente.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.dto.ClienteRequest;
import br.insper.delivery.cliente.event.ClienteCriadoEvent;
import br.insper.delivery.cliente.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ClienteService clienteService;

	@Test
	void criarDevePersistirEPublicarEvento() {
		ClienteRequest request = new ClienteRequest("Ana", "ana@email.com", "11999999999");
		Cliente salvo = new Cliente("Ana", "ana@email.com", "11999999999");
		when(clienteRepository.save(any(Cliente.class))).thenReturn(salvo);

		Cliente resultado = clienteService.criar(request);

		assertThat(resultado.getEmail()).isEqualTo("ana@email.com");
		verify(eventPublisher).publishEvent(any(ClienteCriadoEvent.class));
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> clienteService.buscarPorId(1L));
	}
}
