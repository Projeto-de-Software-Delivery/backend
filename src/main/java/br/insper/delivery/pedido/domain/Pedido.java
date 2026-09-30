package br.insper.delivery.pedido.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um pedido.
 */
@Entity
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long clienteId;

	private Long lojaId;

	private BigDecimal valorTotal;

	@Enumerated(EnumType.STRING)
	private PedidoStatus status;

	private Instant dataCriacao;

	protected Pedido() {
	}

	/**
	 * Construtor da classe Pedido. O pedido é criado com status RECEBIDO.
	 *
	 * @param clienteId  ID do cliente que fez o pedido.
	 * @param lojaId     ID da loja do pedido.
	 * @param valorTotal Valor total do pedido.
	 */
	public Pedido(Long clienteId, Long lojaId, BigDecimal valorTotal) {
		this.clienteId = clienteId;
		this.lojaId = lojaId;
		this.valorTotal = valorTotal;
		this.status = PedidoStatus.RECEBIDO;
		this.dataCriacao = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public Long getLojaId() {
		return lojaId;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public PedidoStatus getStatus() {
		return status;
	}

	public Instant getDataCriacao() {
		return dataCriacao;
	}

}
