package br.insper.delivery.pedido.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import br.insper.delivery.pedido.domain.Pedido;
import br.insper.delivery.pedido.repository.PedidoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PedidoRepository pedidoRepository;

	private Long criarCliente() throws Exception {
		MvcResult result = mockMvc
				.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/clientes")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Ana\",\"email\":\"pedido.teste@email.com\","
								+ "\"telefone\":\"11999999999\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	@Test
	void buscarPedidoExistenteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Pedido pedido = pedidoRepository.save(new Pedido(clienteId, 1L, new BigDecimal("59.90")));

		mockMvc.perform(get("/pedidos/" + pedido.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.clienteId").value(clienteId))
				.andExpect(jsonPath("$.status").value("RECEBIDO"))
				.andExpect(jsonPath("$.valorTotal").value(59.90));
	}

	@Test
	void buscarPedidoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/pedidos/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarStatusDePedidoExistenteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Pedido pedido = pedidoRepository.save(new Pedido(clienteId, 1L, new BigDecimal("59.90")));

		mockMvc.perform(get("/pedidos/" + pedido.getId() + "/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("RECEBIDO"));
	}

	@Test
	void buscarStatusDePedidoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/pedidos/999999/status")).andExpect(status().isNotFound());
	}

	@Test
	void listarPedidosDeClienteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		pedidoRepository.save(new Pedido(clienteId, 1L, new BigDecimal("59.90")));
		pedidoRepository.save(new Pedido(clienteId, 2L, new BigDecimal("19.90")));

		mockMvc.perform(get("/clientes/" + clienteId + "/pedidos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void listarPedidosDeClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/clientes/999999/pedidos")).andExpect(status().isNotFound());
	}
}
