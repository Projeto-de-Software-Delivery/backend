package br.insper.delivery.loja.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.domain.Loja;
import br.insper.delivery.loja.dto.LojaRequest;
import br.insper.delivery.loja.event.LojaCriadaEvent;
import br.insper.delivery.loja.repository.LojaRepository;

@Service
public class LojaService {

	private final LojaRepository lojaRepository;
	private final ApplicationEventPublisher eventPublisher;

	public LojaService(LojaRepository lojaRepository, ApplicationEventPublisher eventPublisher) {
		this.lojaRepository = lojaRepository;
		this.eventPublisher = eventPublisher;
	}

	public Loja criar(LojaRequest request) {
		Loja loja = new Loja(request.nome(), request.cnpj(), request.endereco());
		Loja salva = lojaRepository.save(loja);
		eventPublisher.publishEvent(new LojaCriadaEvent(this, salva));
		return salva;
	}

	public Loja buscarPorId(Long id) {
		return lojaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));
	}
}
