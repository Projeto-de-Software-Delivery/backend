package br.insper.delivery.endereco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.insper.delivery.endereco.domain.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

	List<Endereco> findByClienteId(Long clienteId);
}
