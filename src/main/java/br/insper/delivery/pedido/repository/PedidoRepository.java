package br.insper.delivery.pedido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.pedido.domain.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByClienteId(Long clienteId);
}
