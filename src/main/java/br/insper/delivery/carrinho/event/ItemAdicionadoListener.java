package br.insper.delivery.carrinho.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ItemAdicionadoListener {

	private static final Logger log = LoggerFactory.getLogger(ItemAdicionadoListener.class);

	@EventListener
	public void aoAdicionarItem(ItemAdicionadoEvent evento) {
		log.info("Item adicionado ao carrinho: clienteId={}, produtoId={}, quantidade={}",
				evento.getItem().getClienteId(), evento.getItem().getProdutoId(), evento.getItem().getQuantidade());
	}
}
