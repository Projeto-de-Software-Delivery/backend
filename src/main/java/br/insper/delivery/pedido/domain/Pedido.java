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

	/**
	 * Construtor da classe Pedido. O pedido é criado com status AGUARDANDO_VALIDACAO.
	 *
	 * @param clienteId   ID do cliente que fez o pedido.
	 * @param lojaId      ID da loja do pedido.
	 * @param total       Valor total do pedido.
	 * @param enderecoRua Logradouro do endereço de entrega.
	 * @param enderecoLat Latitude do endereço de entrega.
	 * @param enderecoLng Longitude do endereço de entrega.
	 */
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

	/**
	 * Aplica uma transição de status, validando que o pedido está no status de origem esperado.
	 *
	 * @param statusEsperado Status em que o pedido deve estar para que a transição seja permitida.
	 * @param novoStatus     Status para o qual o pedido deve transicionar.
	 * @throws IllegalStateException Se o pedido não estiver no status esperado.
	 */
	public void transicionar(PedidoStatus statusEsperado, PedidoStatus novoStatus) {
		if (this.status != statusEsperado) {
			throw new IllegalStateException(
					"Pedido está em " + this.status + ", esperado " + statusEsperado + " para transicionar para "
							+ novoStatus);
		}
		this.status = novoStatus;
	}

}
