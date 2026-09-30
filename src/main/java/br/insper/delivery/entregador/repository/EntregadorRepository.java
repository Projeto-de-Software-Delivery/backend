package br.insper.delivery.entregador.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.entregador.domain.Entregador;

public interface EntregadorRepository extends JpaRepository<Entregador, Long> {
}
