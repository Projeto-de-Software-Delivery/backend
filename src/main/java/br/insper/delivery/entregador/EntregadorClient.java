package br.insper.delivery.entregador;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP do serviço de entregador. O serviço só expõe CRUD + status (sem fila nem endpoint
 * de oferta de corrida), então a atribuição é feita por polling síncrono: busca o primeiro
 * DISPONIVEL e marca como EM_ENTREGA.
 */
@Service
public class EntregadorClient {

	private static final Logger log = LoggerFactory.getLogger(EntregadorClient.class);
	private static final String STATUS_DISPONIVEL = "DISPONIVEL";

	private final RestClient restClient;

	public EntregadorClient(@Value("${entregador.service.url}") String baseUrl) {
		this.restClient = RestClient.create(baseUrl);
	}

	/** Best-effort: uma indisponibilidade do serviço de entregador não deve falhar o pedido. */
	public Optional<EntregadorDados> buscarDisponivel() {
		try {
			List<EntregadorDados> entregadores = restClient.get()
					.uri("/entregadores")
					.retrieve()
					.body(new ParameterizedTypeReference<List<EntregadorDados>>() {
					});
			return (entregadores == null ? List.<EntregadorDados>of() : entregadores).stream()
					.filter(entregador -> STATUS_DISPONIVEL.equals(entregador.status()))
					.findFirst();
		} catch (RestClientException e) {
			log.error("Falha ao consultar entregadores disponíveis", e);
			return Optional.empty();
		}
	}

	/** Best-effort: se falhar, o entregador fica com status desatualizado no serviço dele. */
	public void atualizarStatus(String entregadorId, String status) {
		try {
			restClient.patch()
					.uri("/entregadores/{id}/status", entregadorId)
					.body(new EntregadorStatusUpdate(status))
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientException e) {
			log.error("Falha ao atualizar status do entregador {} para {}", entregadorId, status, e);
		}
	}

	private record EntregadorStatusUpdate(String status) {
	}
}
