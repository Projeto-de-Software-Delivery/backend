package br.insper.delivery.carrinho.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ItemRemovidoListener {

	private static final Logger log = LoggerFactory.getLogger(ItemRemovidoListener.class);

	@EventListener
	public void aoRemoverItem(ItemRemovidoEvent evento) {
		log.info("Item removido do carrinho: clienteId={}, produtoId={}", evento.getItem().getClienteId(),
				evento.getItem().getProdutoId());
	}
}
