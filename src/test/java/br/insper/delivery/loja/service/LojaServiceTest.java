package br.insper.delivery.loja.service;

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

	@Test
	void listarTodasDeveRetornarLojasDoRepositorio() {
		Loja loja = new Loja("Padaria", "12345678000199", "Rua A, 1");
		when(lojaRepository.findAll()).thenReturn(List.of(loja));

		List<Loja> resultado = lojaService.listarTodas();

		assertThat(resultado).hasSize(1);
	}

	@Test
	void atualizarDeveAlterarDadosDaLojaExistente() {
		Loja loja = new Loja("Padaria", "12345678000199", "Rua A, 1");
		LojaRequest request = new LojaRequest("Padaria Nova", "98765432000188", "Rua B, 2");
		when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));
		when(lojaRepository.save(any(Loja.class))).thenReturn(loja);

		Loja resultado = lojaService.atualizar(1L, request);

		assertThat(resultado.getNome()).isEqualTo("Padaria Nova");
		assertThat(resultado.getCnpj()).isEqualTo("98765432000188");
	}

	@Test
	void atualizarDeveLancarQuandoNaoEncontrado() {
		LojaRequest request = new LojaRequest("Padaria Nova", "98765432000188", "Rua B, 2");
		when(lojaRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> lojaService.atualizar(1L, request));
	}

	@Test
	void deletarDeveRemoverLojaExistente() {
		Loja loja = new Loja("Padaria", "12345678000199", "Rua A, 1");
		when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));

		lojaService.deletar(1L);

		verify(lojaRepository).delete(loja);
	}

	@Test
	void deletarDeveLancarQuandoNaoEncontrado() {
		when(lojaRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> lojaService.deletar(1L));
	}
}
