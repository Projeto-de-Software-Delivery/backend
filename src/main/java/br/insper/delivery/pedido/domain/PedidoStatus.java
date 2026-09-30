package br.insper.delivery.pedido.domain;

/**
 * Status do ciclo de vida de um pedido.
 */
public enum PedidoStatus {
	RECEBIDO,
	EM_PREPARO,
	EM_ENTREGA,
	ENTREGUE,
	CANCELADO
}
