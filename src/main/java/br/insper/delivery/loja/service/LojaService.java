package br.insper.delivery.loja.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.insper.delivery.loja.domain.Loja;
import br.insper.delivery.loja.dto.LojaRequest;
import br.insper.delivery.loja.event.LojaCriadaEvent;
import br.insper.delivery.loja.repository.LojaRepository;

/**
 * Serviço para gerenciar lojas.
 */
@Service
public class LojaService {

	private final LojaRepository lojaRepository;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * Construtor da classe LojaService.
	 *
	 * @param lojaRepository Repositório de lojas.
	 * @param eventPublisher Publicador de eventos.
	 */
	public LojaService(LojaRepository lojaRepository, ApplicationEventPublisher eventPublisher) {
		this.lojaRepository = lojaRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Cria uma nova loja.
	 *
	 * @param request Dados da loja a ser criada.
	 * @return Loja criada.
	 */
	public Loja criar(LojaRequest request) {
		Loja loja = new Loja(request.nome(), request.cnpj(), request.endereco());
		Loja salva = lojaRepository.save(loja);
		eventPublisher.publishEvent(new LojaCriadaEvent(this, salva));
		return salva;
	}

	/**
	 * Busca uma loja pelo seu ID.
	 *
	 * @param id ID da loja a ser buscada.
	 * @return Loja encontrada.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public Loja buscarPorId(Long id) {
		return lojaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));
	}
}
