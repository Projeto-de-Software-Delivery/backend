package br.insper.delivery.loja.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidade que representa o estoque de um produto em uma loja específica.
 * Cada registro vincula um produto a uma loja com a quantidade disponível.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "loja_id", "produto_id" }))
public class EstoqueLoja {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long lojaId;

	private Long produtoId;

	private Integer quantidade;

	protected EstoqueLoja() {
	}

	/**
	 * Construtor da classe EstoqueLoja.
	 *
	 * @param lojaId    ID da loja dona do estoque.
	 * @param produtoId ID do produto em estoque.
	 * @param quantidade Quantidade disponível do produto.
	 */
	public EstoqueLoja(Long lojaId, Long produtoId, Integer quantidade) {
		this.lojaId = lojaId;
		this.produtoId = produtoId;
		this.quantidade = quantidade;
	}

	public Long getId() {
		return id;
	}

	public Long getLojaId() {
		return lojaId;
	}

	public Long getProdutoId() {
		return produtoId;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	/**
	 * Atualiza a quantidade disponível no estoque.
	 *
	 * @param quantidade Nova quantidade.
	 */
	public void atualizarQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	/**
	 * Verifica se há quantidade suficiente no estoque.
	 *
	 * @param quantidadeDesejada Quantidade desejada.
	 * @return true se o estoque for suficiente.
	 */
	public boolean temEstoqueSuficiente(Integer quantidadeDesejada) {
		return this.quantidade >= quantidadeDesejada;
	}
}
