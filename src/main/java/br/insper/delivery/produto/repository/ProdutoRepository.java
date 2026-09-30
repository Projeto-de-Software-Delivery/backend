package br.insper.delivery.produto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.produto.domain.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
