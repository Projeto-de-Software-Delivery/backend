package br.insper.delivery.pedido.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.insper.delivery.pedido.domain.ItemPedido;
import br.insper.delivery.pedido.domain.Pedido;

/**
 * Envelope do evento publicado no tópico pedido.criado, conforme o contrato definido em payloads.md.
 */
public record PedidoCriadoEvento(String eventId, String eventType, int version, Instant occurredAt,
		PedidoCriadoDados data) {

	public static PedidoCriadoEvento de(Pedido pedido, List<ItemPedido> itens) {
		List<ItemPedidoDados> itensDados = itens.stream()
				.map(item -> new ItemPedidoDados(String.valueOf(item.getProdutoId()), item.getQuantidade(),
						item.getPrecoUnitario()))
				.toList();
		EnderecoEntregaDados enderecoEntrega = new EnderecoEntregaDados(pedido.getEnderecoRua(),
				pedido.getEnderecoLat(), pedido.getEnderecoLng());
		PedidoCriadoDados data = new PedidoCriadoDados(String.valueOf(pedido.getId()),
				String.valueOf(pedido.getClienteId()), String.valueOf(pedido.getLojaId()), itensDados,
				pedido.getTotal(), enderecoEntrega);
		return new PedidoCriadoEvento(UUID.randomUUID().toString(), "pedido.criado", 1, Instant.now(), data);
	}
}
