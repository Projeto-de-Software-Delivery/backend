package br.insper.delivery.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.cliente.domain.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
