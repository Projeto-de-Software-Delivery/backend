package br.insper.delivery.loja.service;

import java.util.List;

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
	 * Lista todas as lojas.
	 *
	 * @return Lista de lojas.
	 */
	public List<Loja> listarTodas() {
		return lojaRepository.findAll();
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

	/**
	 * Atualiza os dados de uma loja existente.
	 *
	 * @param id      ID da loja a ser atualizada.
	 * @param request Novos dados da loja.
	 * @return Loja atualizada.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public Loja atualizar(Long id, LojaRequest request) {
		Loja loja = buscarPorId(id);
		loja.atualizar(request.nome(), request.cnpj(), request.endereco());
		return lojaRepository.save(loja);
	}

	/**
	 * Remove uma loja.
	 *
	 * @param id ID da loja a ser removida.
	 * @throws ResponseStatusException Se a loja não for encontrada.
	 */
	public void deletar(Long id) {
		Loja loja = buscarPorId(id);
		lojaRepository.delete(loja);
	}
}
