package br.insper.delivery.pedido.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class PedidoControllerTest {

	@Autowired
	private MockMvc mockMvc;

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
						.content("{\"nome\":\"Ana\",\"email\":\"pedido.teste@email.com\","
								+ "\"telefone\":\"11999999999\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
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

	private Long criarProduto() throws Exception {
		MvcResult result = mockMvc
				.perform(post("/produtos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"nome\":\"Bolo\",\"categoria\":\"Sobremesas\",\"preco\":15.90,"
								+ "\"foto\":\"http://exemplo.com/foto.png\"}"))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	private String pedidoJson(Long lojaId, Long produtoId, int quantidade) {
		return "{\"lojaId\":" + lojaId + ",\"itens\":[{\"produtoId\":" + produtoId + ",\"quantidade\":" + quantidade
				+ "}],\"enderecoEntrega\":{\"rua\":\"Rua B, 2\",\"lat\":-23.5,\"lng\":-46.6}}";
	}

	private Long criarPedido(Long clienteId, Long lojaId, Long produtoId, int quantidade) throws Exception {
		MvcResult result = mockMvc
				.perform(post("/clientes/" + clienteId + "/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(pedidoJson(lojaId, produtoId, quantidade)))
				.andExpect(status().isCreated())
				.andReturn();
		return extrairId(result);
	}

	@Test
	void criarPedidoComDadosValidosRetorna201ComStatusAguardandoValidacao() throws Exception {
		Long clienteId = criarCliente();
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(post("/clientes/" + clienteId + "/pedidos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pedidoJson(lojaId, produtoId, 2)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("AGUARDANDO_VALIDACAO"))
				.andExpect(jsonPath("$.total").value(31.80))
				.andExpect(jsonPath("$.itens.length()").value(1))
				.andExpect(jsonPath("$.enderecoEntrega.rua").value("Rua B, 2"));
	}

	@Test
	void criarPedidoParaClienteInexistenteRetorna404() throws Exception {
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();

		mockMvc.perform(post("/clientes/999999/pedidos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pedidoJson(lojaId, produtoId, 2)))
				.andExpect(status().isNotFound());
	}

	@Test
	void criarPedidoComLojaInexistenteRetorna404() throws Exception {
		Long clienteId = criarCliente();
		Long produtoId = criarProduto();

		mockMvc.perform(post("/clientes/" + clienteId + "/pedidos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pedidoJson(999999L, produtoId, 2)))
				.andExpect(status().isNotFound());
	}

	@Test
	void criarPedidoComProdutoInexistenteRetorna404() throws Exception {
		Long clienteId = criarCliente();
		Long lojaId = criarLoja();

		mockMvc.perform(post("/clientes/" + clienteId + "/pedidos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pedidoJson(lojaId, 999999L, 2)))
				.andExpect(status().isNotFound());
	}

	@Test
	void criarPedidoSemItensRetorna400() throws Exception {
		Long clienteId = criarCliente();
		Long lojaId = criarLoja();

		mockMvc.perform(post("/clientes/" + clienteId + "/pedidos")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"lojaId\":" + lojaId
						+ ",\"itens\":[],\"enderecoEntrega\":{\"rua\":\"Rua B, 2\",\"lat\":-23.5,\"lng\":-46.6}}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void buscarPedidoExistenteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();
		Long pedidoId = criarPedido(clienteId, lojaId, produtoId, 2);

		mockMvc.perform(get("/pedidos/" + pedidoId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.clienteId").value(clienteId))
				.andExpect(jsonPath("$.status").value("AGUARDANDO_VALIDACAO"));
	}

	@Test
	void buscarPedidoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/pedidos/999999")).andExpect(status().isNotFound());
	}

	@Test
	void buscarStatusDePedidoExistenteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();
		Long pedidoId = criarPedido(clienteId, lojaId, produtoId, 2);

		mockMvc.perform(get("/pedidos/" + pedidoId + "/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("AGUARDANDO_VALIDACAO"));
	}

	@Test
	void buscarStatusDePedidoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/pedidos/999999/status")).andExpect(status().isNotFound());
	}

	@Test
	void listarPedidosDeClienteRetorna200() throws Exception {
		Long clienteId = criarCliente();
		Long lojaId = criarLoja();
		Long produtoId = criarProduto();
		criarPedido(clienteId, lojaId, produtoId, 1);
		criarPedido(clienteId, lojaId, produtoId, 3);

		mockMvc.perform(get("/clientes/" + clienteId + "/pedidos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void listarPedidosDeClienteInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/clientes/999999/pedidos")).andExpect(status().isNotFound());
	}
}
