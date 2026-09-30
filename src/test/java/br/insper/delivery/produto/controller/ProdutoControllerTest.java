package br.insper.delivery.produto.controller;

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
class ProdutoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private Long criarProduto() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/produtos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\",\"preco\":25.90,"
								+ "\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	@Test
	void criarProdutoComDadosValidosRetorna201() throws Exception {
		mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\",\"preco\":25.90,"
						+ "\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nome").value("Bolo de chocolate"));
	}

	@Test
	void criarProdutoComPrecoInvalidoRetorna400() throws Exception {
		mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\",\"preco\":-1,"
						+ "\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listarTodosRetorna200() throws Exception {
		criarProduto();

		mockMvc.perform(get("/produtos")).andExpect(status().isOk());
	}

	@Test
	void buscarProdutoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/produtos/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarProdutoExistenteRetorna200() throws Exception {
		Long id = criarProduto();

		mockMvc.perform(get("/produtos/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoria").value("Sobremesas"));
	}

	@Test
	void atualizarProdutoExistenteRetorna200() throws Exception {
		Long id = criarProduto();

		mockMvc.perform(put("/produtos/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Bolo de cenoura\",\"categoria\":\"Sobremesas\",\"preco\":19.90,"
						+ "\"foto\":\"http://exemplo.com/nova-foto.png\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Bolo de cenoura"));
	}

	@Test
	void atualizarProdutoInexistenteRetorna404() throws Exception {
		mockMvc.perform(put("/produtos/999999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Bolo de cenoura\",\"categoria\":\"Sobremesas\",\"preco\":19.90,"
						+ "\"foto\":\"http://exemplo.com/nova-foto.png\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deletarProdutoExistenteRetorna204() throws Exception {
		Long id = criarProduto();

		mockMvc.perform(delete("/produtos/" + id)).andExpect(status().isNoContent());
	}

	@Test
	void deletarProdutoInexistenteRetorna404() throws Exception {
		mockMvc.perform(delete("/produtos/999999")).andExpect(status().isNotFound());
	}
}
