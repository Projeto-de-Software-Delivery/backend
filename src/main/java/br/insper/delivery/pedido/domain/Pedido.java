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

	private BigDecimal total;

	@Enumerated(EnumType.STRING)
	private PedidoStatus status;

	private String enderecoRua;

	private Double enderecoLat;

	private Double enderecoLng;

	private Instant dataCriacao;

	protected Pedido() {
	}

	/** Status inicial sempre AGUARDANDO_VALIDACAO. */
	public Pedido(Long clienteId, Long lojaId, BigDecimal total, String enderecoRua, Double enderecoLat,
			Double enderecoLng) {
		this.clienteId = clienteId;
		this.lojaId = lojaId;
		this.total = total;
		this.enderecoRua = enderecoRua;
		this.enderecoLat = enderecoLat;
		this.enderecoLng = enderecoLng;
		this.status = PedidoStatus.AGUARDANDO_VALIDACAO;
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

	public BigDecimal getTotal() {
		return total;
	}

	public PedidoStatus getStatus() {
		return status;
	}

	public String getEnderecoRua() {
		return enderecoRua;
	}

	public Double getEnderecoLat() {
		return enderecoLat;
	}

	public Double getEnderecoLng() {
		return enderecoLng;
	}

	public Instant getDataCriacao() {
		return dataCriacao;
	}

	/** @throws IllegalStateException se o pedido não estiver em statusEsperado. */
	public void transicionar(PedidoStatus statusEsperado, PedidoStatus novoStatus) {
		if (this.status != statusEsperado) {
			throw new IllegalStateException(
					"Pedido está em " + this.status + ", esperado " + statusEsperado + " para transicionar para "
							+ novoStatus);
		}
		this.status = novoStatus;
	}

}
