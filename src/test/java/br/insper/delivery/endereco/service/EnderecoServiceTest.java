package br.insper.delivery.endereco.service;

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

import br.insper.delivery.cliente.domain.Cliente;
import br.insper.delivery.cliente.repository.ClienteRepository;
import br.insper.delivery.cliente.service.ClienteService;
import br.insper.delivery.endereco.domain.Endereco;
import br.insper.delivery.endereco.dto.EnderecoRequest;
import br.insper.delivery.endereco.event.EnderecoCriadoEvent;
import br.insper.delivery.endereco.repository.EnderecoRepository;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

	@Mock
	private EnderecoRepository enderecoRepository;

	@Mock
	private ClienteRepository clienteRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	private ClienteService clienteService;

	private EnderecoService enderecoService;

	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		clienteService = new ClienteService(clienteRepository, eventPublisher);
		enderecoService = new EnderecoService(enderecoRepository, clienteService, eventPublisher);
	}

	private static final Cliente CLIENTE = new Cliente("Ana", "ana@email.com", "11999999999");

	private EnderecoRequest requestPadrao() {
		return new EnderecoRequest("01001000", "Praça da Sé", "1", "Lado ímpar", "Sé", "São Paulo", "SP");
	}

	@Test
	void criarDevePersistirEPublicarEventoQuandoClienteExiste() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(CLIENTE));
		Endereco salvo = new Endereco(1L, "01001000", "Praça da Sé", "1", "Lado ímpar", "Sé", "São Paulo", "SP");
		when(enderecoRepository.save(any(Endereco.class))).thenReturn(salvo);

		Endereco resultado = enderecoService.criar(1L, requestPadrao());

		assertThat(resultado.getCidade()).isEqualTo("São Paulo");
		verify(eventPublisher).publishEvent(any(EnderecoCriadoEvent.class));
	}

	@Test
	void criarDeveLancarQuandoClienteNaoEncontrado() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> enderecoService.criar(1L, requestPadrao()));
	}

	@Test
	void listarPorClienteDeveRetornarEnderecosDoRepositorio() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(CLIENTE));
		Endereco endereco = new Endereco(1L, "01001000", "Praça da Sé", "1", "Lado ímpar", "Sé", "São Paulo", "SP");
		when(enderecoRepository.findByClienteId(1L)).thenReturn(List.of(endereco));

		List<Endereco> resultado = enderecoService.listarPorCliente(1L);

		assertThat(resultado).hasSize(1);
	}

	@Test
	void buscarPorIdDeveLancarQuandoEnderecoNaoEncontrado() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(CLIENTE));
		when(enderecoRepository.findById(2L)).thenReturn(Optional.empty());

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> enderecoService.buscarPorId(1L, 2L));
	}

	@Test
	void buscarPorIdDeveLancarQuandoEnderecoPertenceAOutroCliente() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(CLIENTE));
		Endereco deOutroCliente = new Endereco(2L, "01001000", "Praça da Sé", "1", "Lado ímpar", "Sé", "São Paulo",
				"SP");
		when(enderecoRepository.findById(5L)).thenReturn(Optional.of(deOutroCliente));

		org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class,
				() -> enderecoService.buscarPorId(1L, 5L));
	}

	@Test
	void atualizarDeveAlterarDadosDoEnderecoExistente() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(CLIENTE));
		Endereco endereco = new Endereco(1L, "01001000", "Praça da Sé", "1", "Lado ímpar", "Sé", "São Paulo", "SP");
		when(enderecoRepository.findById(5L)).thenReturn(Optional.of(endereco));
		when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);
		EnderecoRequest request = new EnderecoRequest("04538132", "Av. Faria Lima", "1500", null, "Itaim Bibi",
				"São Paulo", "SP");

		Endereco resultado = enderecoService.atualizar(1L, 5L, request);

		assertThat(resultado.getLogradouro()).isEqualTo("Av. Faria Lima");
		assertThat(resultado.getCep()).isEqualTo("04538132");
	}

	@Test
	void deletarDeveRemoverEnderecoExistente() {
		when(clienteRepository.findById(1L)).thenReturn(Optional.of(CLIENTE));
		Endereco endereco = new Endereco(1L, "01001000", "Praça da Sé", "1", "Lado ímpar", "Sé", "São Paulo", "SP");
		when(enderecoRepository.findById(5L)).thenReturn(Optional.of(endereco));

		enderecoService.deletar(1L, 5L);

		verify(enderecoRepository).delete(endereco);
	}
}
