package br.insper.delivery.entregador.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um entregador.
 */
@Entity
public class Entregador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String cpf;

	private String telefone;

	private String placa;

	@Enumerated(EnumType.STRING)
	private StatusDisponibilidade status;

	protected Entregador() {
	}

	/**
	 * Construtor da classe Entregador. O entregador é criado com status DISPONIVEL.
	 *
	 * @param nome     Nome do entregador.
	 * @param cpf      CPF do entregador.
	 * @param telefone Telefone do entregador.
	 * @param placa    Placa do veículo do entregador.
	 */
	public Entregador(String nome, String cpf, String telefone, String placa) {
		this.nome = nome;
		this.cpf = cpf;
		this.telefone = telefone;
		this.placa = placa;
		this.status = StatusDisponibilidade.DISPONIVEL;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCpf() {
		return cpf;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getPlaca() {
		return placa;
	}

	public StatusDisponibilidade getStatus() {
		return status;
	}

	/**
	 * Atualiza os dados cadastrais do entregador.
	 *
	 * @param nome     Nome do entregador.
	 * @param cpf      CPF do entregador.
	 * @param telefone Telefone do entregador.
	 * @param placa    Placa do veículo do entregador.
	 */
	public void atualizar(String nome, String cpf, String telefone, String placa) {
		this.nome = nome;
		this.cpf = cpf;
		this.telefone = telefone;
		this.placa = placa;
	}

	/**
	 * Atualiza o status de disponibilidade do entregador.
	 *
	 * @param status Novo status de disponibilidade.
	 */
	public void atualizarStatus(StatusDisponibilidade status) {
		this.status = status;
	}

}
