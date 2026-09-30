package br.insper.delivery.pedido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.pedido.domain.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

	List<ItemPedido> findByPedidoId(Long pedidoId);
}
