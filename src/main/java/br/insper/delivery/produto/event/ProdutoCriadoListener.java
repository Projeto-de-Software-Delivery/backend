package br.insper.delivery.produto.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProdutoCriadoListener {

	private static final Logger log = LoggerFactory.getLogger(ProdutoCriadoListener.class);

	@EventListener
	public void aoCriarProduto(ProdutoCriadoEvent evento) {
		log.info("Produto criado: id={}, nome={}", evento.getProduto().getId(), evento.getProduto().getNome());
	}
}
