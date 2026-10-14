package br.com.vr.miniautorizador.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface SpringDataCartaoRepository extends JpaRepository<CartaoJpaEntity, Long> {

	boolean existsByNumeroCartao(String numeroCartao);

	Optional<CartaoJpaEntity> findByNumeroCartao(String numeroCartao);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		update CartaoJpaEntity c
		   set c.saldo = c.saldo - :valor
		 where c.numeroCartao = :numeroCartao
		   and c.saldo >= :valor
		""")
	int debitarSeHouverSaldo(@Param("numeroCartao") String numeroCartao, @Param("valor") BigDecimal valor);
}
