package br.insper.delivery.loja.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.domain.Loja;
import br.insper.delivery.loja.dto.LojaRequest;
import br.insper.delivery.loja.event.LojaCriadaEvent;
import br.insper.delivery.loja.repository.LojaRepository;

@ExtendWith(MockitoExtension.class)
class LojaServiceTest {

	@Mock
	private LojaRepository lojaRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private LojaService lojaService;

	@Test
	void criarDevePersistirEPublicarEvento() {
		LojaRequest request = new LojaRequest("Padaria", "12345678000199", "Rua A, 1");
		Loja salva = new Loja("Padaria", "12345678000199", "Rua A, 1");
		when(lojaRepository.save(any(Loja.class))).thenReturn(salva);

		Loja resultado = lojaService.criar(request);

		assertThat(resultado.getCnpj()).isEqualTo("12345678000199");
		verify(eventPublisher).publishEvent(any(LojaCriadaEvent.class));
	}

	@Test
	void buscarPorIdDeveLancarQuandoNaoEncontrado() {
		when(lojaRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> lojaService.buscarPorId(1L));
	}
}
