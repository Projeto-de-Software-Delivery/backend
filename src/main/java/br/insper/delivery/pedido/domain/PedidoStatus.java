package br.insper.delivery.pedido.domain;

/**
 * Status do ciclo de vida de um pedido. As transições seguem a máquina de estados definida pelos
 * tópicos de evento em payloads.md: pedido.criado, pedido.validado, entrega.aceita, pedido.retirado
 * e pedido.entregue.
 */
public enum PedidoStatus {
	AGUARDANDO_VALIDACAO,
	VALIDADO,
	ENTREGA_ACEITA,
	EM_ENTREGA,
	ENTREGUE,
	CANCELADO
}
