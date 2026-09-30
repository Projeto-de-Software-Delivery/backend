package br.insper.delivery.carrinho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.carrinho.domain.ItemCarrinho;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

	List<ItemCarrinho> findByClienteId(Long clienteId);

	Optional<ItemCarrinho> findByClienteIdAndProdutoId(Long clienteId, Long produtoId);
}
