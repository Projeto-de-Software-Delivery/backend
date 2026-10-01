package br.insper.delivery.loja.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.loja.domain.EstoqueLoja;

public interface EstoqueLojaRepository extends JpaRepository<EstoqueLoja, Long> {

	Optional<EstoqueLoja> findByLojaIdAndProdutoId(Long lojaId, Long produtoId);

	List<EstoqueLoja> findByLojaId(Long lojaId);
}
