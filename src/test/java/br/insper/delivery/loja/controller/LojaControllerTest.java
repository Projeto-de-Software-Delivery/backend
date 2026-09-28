package br.insper.delivery.loja.controller;

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
class LojaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void criarLojaComDadosValidosRetorna201() throws Exception {
		mockMvc.perform(post("/lojas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Padaria\",\"cnpj\":\"12345678000199\",\"endereco\":\"Rua A, 1\"}"))
				.andExpect(status().isCreated());
	}

	@Test
	void criarLojaComCnpjInvalidoRetorna400() throws Exception {
		mockMvc.perform(post("/lojas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Padaria\",\"cnpj\":\"123\",\"endereco\":\"Rua A, 1\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void buscarLojaInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/lojas/999")).andExpect(status().isNotFound());
	}
}
