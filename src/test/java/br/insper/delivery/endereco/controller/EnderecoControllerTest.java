package br.insper.delivery.endereco.controller;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EnderecoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private static final String ENDERECO_JSON = "{\"cep\":\"01001000\",\"logradouro\":\"Praça da Sé\","
			+ "\"numero\":\"1\",\"complemento\":\"Lado ímpar\",\"bairro\":\"Sé\",\"cidade\":\"São Paulo\","
			+ "\"estado\":\"SP\"}";

	private Long extrairId(MvcResult result) throws Exception {
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	private Long criarCliente() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/clientes")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Ana\",\"email\":\"endereco.teste@email.com\","
								+ "\"telefone\":\"11999999999\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	private Long criarEndereco(Long clienteId) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/clientes/" + clienteId + "/enderecos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(ENDERECO_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	@Test
	void criarEnderecoComDadosValidosRetorna201() throws Exception {
		Long clienteId = criarCliente();

		mockMvc.perform(post("/clientes/" + clienteId + "/enderecos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ENDERECO_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cidade").value("São Paulo"));
	}

	@Test
	void criarEnderecoParaClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(post("/clientes/999999/enderecos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(ENDERECO_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void criarEnderecoComCepInvalidoRetorna400() throws Exception {
		Long clienteId = criarCliente();

		mockMvc.perform(post("/clientes/" + clienteId + "/enderecos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cep\":\"123\",\"logradouro\":\"Praça da Sé\",\"numero\":\"1\","
						+ "\"bairro\":\"Sé\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listarEnderecosDeClienteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		criarEndereco(clienteId);

		mockMvc.perform(get("/clientes/" + clienteId + "/enderecos")).andExpect(status().isOk());
	}

	@Test
	void buscarEnderecoInexistenteRetorna404() throws Exception {
		Long clienteId = criarCliente();

		mockMvc.perform(get("/clientes/" + clienteId + "/enderecos/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarEnderecoExistenteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Long enderecoId = criarEndereco(clienteId);

		mockMvc.perform(get("/clientes/" + clienteId + "/enderecos/" + enderecoId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.bairro").value("Sé"));
	}

	@Test
	void atualizarEnderecoExistenteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Long enderecoId = criarEndereco(clienteId);

		mockMvc.perform(put("/clientes/" + clienteId + "/enderecos/" + enderecoId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cep\":\"04538132\",\"logradouro\":\"Av. Faria Lima\",\"numero\":\"1500\","
						+ "\"bairro\":\"Itaim Bibi\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.logradouro").value("Av. Faria Lima"));
	}

	@Test
	void deletarEnderecoExistenteRetorna204() throws Exception {
		Long clienteId = criarCliente();
		Long enderecoId = criarEndereco(clienteId);

		mockMvc.perform(delete("/clientes/" + clienteId + "/enderecos/" + enderecoId))
				.andExpect(status().isNoContent());
	}

	@Test
	void deletarEnderecoInexistenteRetorna404() throws Exception {
		Long clienteId = criarCliente();

		mockMvc.perform(delete("/clientes/" + clienteId + "/enderecos/999999")).andExpect(status().isNotFound());
	}
}
