package br.insper.delivery.cliente.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private Long criarCliente() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/clientes")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Ana\",\"email\":\"ana@email.com\",\"telefone\":\"11999999999\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	@Test
	void criarClienteComDadosValidosRetorna201() throws Exception {
		mockMvc.perform(post("/clientes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Ana\",\"email\":\"ana@email.com\",\"telefone\":\"11999999999\"}"))
				.andExpect(status().isCreated());
	}

	@Test
	void criarClienteComEmailInvalidoRetorna400() throws Exception {
		mockMvc.perform(post("/clientes")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Ana\",\"email\":\"invalido\",\"telefone\":\"11999999999\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listarTodosRetorna200() throws Exception {
		criarCliente();

		mockMvc.perform(get("/clientes")).andExpect(status().isOk());
	}

	@Test
	void buscarClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/clientes/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarClienteExistenteRetorna200() throws Exception {
		Long id = criarCliente();

		mockMvc.perform(get("/clientes/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("ana@email.com"));
	}

	@Test
	void atualizarClienteExistenteRetorna200() throws Exception {
		Long id = criarCliente();

		mockMvc.perform(put("/clientes/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Ana Souza\",\"email\":\"ana.souza@email.com\",\"telefone\":\"11988888888\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Ana Souza"));
	}

	@Test
	void atualizarClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(put("/clientes/999999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Ana Souza\",\"email\":\"ana.souza@email.com\",\"telefone\":\"11988888888\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deletarClienteExistenteRetorna204() throws Exception {
		Long id = criarCliente();

		mockMvc.perform(delete("/clientes/" + id)).andExpect(status().isNoContent());
	}

	@Test
	void deletarClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(delete("/clientes/999999")).andExpect(status().isNotFound());
	}
}
