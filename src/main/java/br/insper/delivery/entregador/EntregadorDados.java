package br.insper.delivery.entregador;

/**
 * Subconjunto do EntregadorRead exposto pelo serviço de entregador (GET /entregadores).
 */
public record EntregadorDados(String id, String nome, String veiculo, String status) {
}
