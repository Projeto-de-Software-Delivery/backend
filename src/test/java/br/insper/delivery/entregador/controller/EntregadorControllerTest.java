package br.insper.delivery.entregador.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EntregadorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private Long criarEntregador() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/entregadores")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Joao\",\"cpf\":\"12345678901\",\"telefone\":\"11999999999\","
								+ "\"placa\":\"ABC1234\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	@Test
	void criarEntregadorComDadosValidosRetorna201() throws Exception {
		mockMvc.perform(post("/entregadores")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Joao\",\"cpf\":\"12345678901\",\"telefone\":\"11999999999\","
						+ "\"placa\":\"ABC1234\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("DISPONIVEL"));
	}

	@Test
	void criarEntregadorComCpfInvalidoRetorna400() throws Exception {
		mockMvc.perform(post("/entregadores")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Joao\",\"cpf\":\"123\",\"telefone\":\"11999999999\",\"placa\":\"ABC1234\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listarTodosRetorna200() throws Exception {
		criarEntregador();

		mockMvc.perform(get("/entregadores")).andExpect(status().isOk());
	}

	@Test
	void buscarEntregadorInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/entregadores/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarEntregadorExistenteRetorna200() throws Exception {
		Long id = criarEntregador();

		mockMvc.perform(get("/entregadores/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.placa").value("ABC1234"));
	}

	@Test
	void atualizarEntregadorExistenteRetorna200() throws Exception {
		Long id = criarEntregador();

		mockMvc.perform(put("/entregadores/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Joao Silva\",\"cpf\":\"10987654321\",\"telefone\":\"11988888888\","
						+ "\"placa\":\"XYZ9876\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Joao Silva"));
	}

	@Test
	void atualizarEntregadorInexistenteRetorna404() throws Exception {
		mockMvc.perform(put("/entregadores/999999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Joao Silva\",\"cpf\":\"10987654321\",\"telefone\":\"11988888888\","
						+ "\"placa\":\"XYZ9876\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void atualizarStatusDeveAlterarDisponibilidade() throws Exception {
		Long id = criarEntregador();

		mockMvc.perform(patch("/entregadores/" + id + "/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"status\":\"INDISPONIVEL\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("INDISPONIVEL"));
	}

	@Test
	void atualizarStatusComValorInvalidoRetorna400() throws Exception {
		Long id = criarEntregador();

		mockMvc.perform(patch("/entregadores/" + id + "/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"status\":\"EM_FERIAS\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void atualizarStatusDeEntregadorInexistenteRetorna404() throws Exception {
		mockMvc.perform(patch("/entregadores/999999/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"status\":\"INDISPONIVEL\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deletarEntregadorExistenteRetorna204() throws Exception {
		Long id = criarEntregador();

		mockMvc.perform(delete("/entregadores/" + id)).andExpect(status().isNoContent());
	}

	@Test
	void deletarEntregadorInexistenteRetorna404() throws Exception {
		mockMvc.perform(delete("/entregadores/999999")).andExpect(status().isNotFound());
	}
}
