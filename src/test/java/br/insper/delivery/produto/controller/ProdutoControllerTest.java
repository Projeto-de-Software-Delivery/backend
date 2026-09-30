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

	private Long extrairId(MvcResult result) throws Exception {
		String body = result.getResponse().getContentAsString();
		Matcher matcher = Pattern.compile("\"id\":(\\d+)").matcher(body);
		matcher.find();
		return Long.valueOf(matcher.group(1));
	}

	private Long criarLoja() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/lojas")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Padaria\",\"cnpj\":\"12345678000199\",\"endereco\":\"Rua A, 1\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	private Long criarProduto(Long lojaId) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/produtos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"lojaId\":" + lojaId + ",\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\","
								+ "\"preco\":25.90,\"estoque\":10,\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	@Test
	void criarProdutoComDadosValidosRetorna201() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + lojaId + ",\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":25.90,\"estoque\":10,\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nome").value("Bolo de chocolate"))
				.andExpect(jsonPath("$.lojaId").value(lojaId))
				.andExpect(jsonPath("$.estoque").value(10));
	}

	@Test
	void criarProdutoComLojaInexistenteRetorna404() throws Exception {
		mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":999999,\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":25.90,\"estoque\":10,\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void criarProdutoComPrecoInvalidoRetorna400() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + lojaId + ",\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":-1,\"estoque\":10,\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void criarProdutoComEstoqueNegativoRetorna400() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(post("/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + lojaId + ",\"nome\":\"Bolo de chocolate\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":25.90,\"estoque\":-1,\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listarTodosRetorna200() throws Exception {
		Long lojaId = criarLoja();
		criarProduto(lojaId);

		mockMvc.perform(get("/produtos")).andExpect(status().isOk());
	}

	@Test
	void listarPorLojaRetornaApenasProdutosDaLoja() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto(lojaId);
		Long outraLojaId = criarLoja();
		criarProduto(outraLojaId);

		mockMvc.perform(get("/produtos").param("lojaId", String.valueOf(lojaId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(produtoId));
	}

	@Test
	void buscarProdutoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/produtos/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarProdutoExistenteRetorna200() throws Exception {
		Long lojaId = criarLoja();
		Long id = criarProduto(lojaId);

		mockMvc.perform(get("/produtos/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoria").value("Sobremesas"));
	}

	@Test
	void atualizarProdutoExistenteRetorna200() throws Exception {
		Long lojaId = criarLoja();
		Long id = criarProduto(lojaId);

		mockMvc.perform(put("/produtos/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + lojaId + ",\"nome\":\"Bolo de cenoura\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":19.90,\"estoque\":5,\"foto\":\"http://exemplo.com/nova-foto.png\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Bolo de cenoura"))
				.andExpect(jsonPath("$.estoque").value(5));
	}

	@Test
	void atualizarProdutoTentandoMudarDeLojaRetorna400() throws Exception {
		Long lojaId = criarLoja();
		Long outraLojaId = criarLoja();
		Long id = criarProduto(lojaId);

		mockMvc.perform(put("/produtos/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + outraLojaId + ",\"nome\":\"Bolo de cenoura\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":19.90,\"estoque\":5,\"foto\":\"http://exemplo.com/nova-foto.png\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void atualizarProdutoInexistenteRetorna404() throws Exception {
		Long lojaId = criarLoja();

		mockMvc.perform(put("/produtos/999999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + lojaId + ",\"nome\":\"Bolo de cenoura\",\"categoria\":\"Sobremesas\","
						+ "\"preco\":19.90,\"estoque\":5,\"foto\":\"http://exemplo.com/nova-foto.png\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deletarProdutoExistenteRetorna204() throws Exception {
		Long lojaId = criarLoja();
		Long id = criarProduto(lojaId);

		mockMvc.perform(delete("/produtos/" + id)).andExpect(status().isNoContent());
	}

	@Test
	void deletarProdutoInexistenteRetorna404() throws Exception {
		mockMvc.perform(delete("/produtos/999999")).andExpect(status().isNotFound());
	}
}
