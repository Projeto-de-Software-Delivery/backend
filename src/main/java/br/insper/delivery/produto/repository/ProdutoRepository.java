package br.insper.delivery.produto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.produto.domain.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	List<Produto> findByLojaId(Long lojaId);
}
