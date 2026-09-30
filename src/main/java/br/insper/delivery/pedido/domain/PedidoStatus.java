package br.insper.delivery.pedido.domain;

/**
 * Status do ciclo de vida de um pedido.
 */
public enum PedidoStatus {
	AGUARDANDO_VALIDACAO,
	VALIDADO,
	EM_ENTREGA,
	ENTREGUE,
	CANCELADO
}
