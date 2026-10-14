package br.com.vr.miniautorizador.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "cartoes")
public class CartaoJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "numero_cartao", nullable = false, unique = true, length = 32)
	private String numeroCartao;

	@Column(name = "senha_hash", nullable = false)
	private String senhaHash;

	@Column(name = "saldo", nullable = false, precision = 19, scale = 2)
	private BigDecimal saldo;

	@Version
	@Column(name = "versao", nullable = false)
	private Long versao;

	protected CartaoJpaEntity() {
	}

	public CartaoJpaEntity(String numeroCartao, String senhaHash, BigDecimal saldo) {
		this.numeroCartao = numeroCartao;
		this.senhaHash = senhaHash;
		this.saldo = saldo;
	}

	public Long getId() {
		return id;
	}

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public String getSenhaHash() {
		return senhaHash;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}
}
