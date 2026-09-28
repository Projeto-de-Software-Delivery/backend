package br.insper.delivery.loja.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.loja.domain.Loja;

public interface LojaRepository extends JpaRepository<Loja, Long> {
}
