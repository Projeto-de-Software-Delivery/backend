package br.insper.delivery.endereco.event;

import org.springframework.context.ApplicationEvent;

import br.insper.delivery.endereco.domain.Endereco;

public class EnderecoCriadoEvent extends ApplicationEvent {

	private final Endereco endereco;

	public EnderecoCriadoEvent(Object source, Endereco endereco) {
		super(source);
		this.endereco = endereco;
	}

	public Endereco getEndereco() {
		return endereco;
	}
}
