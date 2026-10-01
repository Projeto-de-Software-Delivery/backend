package br.insper.delivery.cliente.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade que representa um cliente.
 */
@Entity
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String email;

	private String telefone;

	protected Cliente() {
	}

	public Cliente(String nome, String email, String telefone) {
		this.nome = nome;
		this.email = email;
		this.telefone = telefone;
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void atualizar(String nome, String email, String telefone) {
		this.nome = nome;
		this.email = email;
		this.telefone = telefone;
	}

}
