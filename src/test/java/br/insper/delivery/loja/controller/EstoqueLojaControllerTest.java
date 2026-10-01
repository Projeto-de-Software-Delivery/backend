package br.insper.delivery.loja.controller;

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
class EstoqueLojaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	// ──────────────────────────────────────────────
	// Helpers para criar loja e produto via API
	// ──────────────────────────────────────────────

	private Long criarLoja() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/lojas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Padaria\",\"cnpj\":\"12345678000199\",\"endereco\":\"Rua A, 1\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	private Long criarProduto() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/produtos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Bolo\",\"categoria\":\"Sobremesas\",\"preco\":15.90,\"foto\":\"foto.png\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	private Long extrairId(MvcResult result) throws Exception {
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	// ──────────────────────────────────────────────
	// PUT /lojas/{lojaId}/estoque
	// ──────────────────────────────────────────────

	@Test
	void adicionarEstoqueEmLojaExistenteRetorna200() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":10}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lojaId").value(lojaId))
				.andExpect(jsonPath("$.produtoId").value(produtoId))
				.andExpect(jsonPath("$.quantidade").value(10));
	}

	@Test
	void atualizarEstoqueExistenteSubstitiuiQuantidade() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":5}"))
				.andExpect(status().isOk());

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":99}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantidade").value(99));
	}

	@Test
	void adicionarEstoqueEmLojaInexistenteRetorna404() throws Exception {
		Long produtoId = criarProduto();

		mockMvc.perform(put("/lojas/999999/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":10}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void adicionarEstoqueComProdutoInexistenteRetorna404() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":999999,\"quantidade\":10}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void adicionarEstoqueComQuantidadeNegativaRetorna400() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":-1}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void adicionarEstoqueSemProdutoIdRetorna400() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"quantidade\":10}"))
				.andExpect(status().isBadRequest());
	}

	// ──────────────────────────────────────────────
	// GET /lojas/{lojaId}/estoque
	// ──────────────────────────────────────────────

	@Test
	void listarEstoqueDeLojaExistenteRetorna200() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":7}"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/lojas/" + lojaId + "/estoque"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].produtoId").value(produtoId))
				.andExpect(jsonPath("$[0].quantidade").value(7));
	}

	@Test
	void listarEstoqueDeLojaVaziaRetornaListaVazia() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(get("/lojas/" + lojaId + "/estoque"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void listarEstoqueDeLojaInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/lojas/999999/estoque"))
				.andExpect(status().isNotFound());
	}

	// ──────────────────────────────────────────────
	// DELETE /lojas/{lojaId}/estoque/{produtoId}
	// ──────────────────────────────────────────────

	@Test
	void removerProdutoDoEstoqueRetorna204() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(put("/lojas/" + lojaId + "/estoque")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"produtoId\":" + produtoId + ",\"quantidade\":5}"))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/lojas/" + lojaId + "/estoque/" + produtoId))
				.andExpect(status().isNoContent());
	}

	@Test
	void removerProdutoInexistenteDoEstoqueRetorna404() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(delete("/lojas/" + lojaId + "/estoque/999999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void removerProdutoDeLojaInexistenteRetorna404() throws Exception {
		Long produtoId = criarProduto();

		mockMvc.perform(delete("/lojas/999999/estoque/" + produtoId))
				.andExpect(status().isNotFound());
	}
}
