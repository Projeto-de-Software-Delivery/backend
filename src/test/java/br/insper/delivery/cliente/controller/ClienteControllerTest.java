package br.insper.delivery.cliente.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

	@Autowired
	private MockMvc mockMvc;

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
	void buscarClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/clientes/999")).andExpect(status().isNotFound());
	}
}
