package br.itb.projeto.AKECY.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Cupom")
public class Cupom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idCupom;

	private String desconto;
	private String cashback;
	private String descricao;
	private String codigo;
	private String statusCupom;

	// Getters and Setters

	public String getDesconto() {
		return desconto;
	}

	public void setDesconto(String desconto) {
		this.desconto = desconto;
	}

	public String getCashback() {
		return cashback;
	}

	public void setCashback(String cashback) {
		this.cashback = cashback;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public long getIdCupom() {
		return idCupom;
	}

	public void setIdCupom(long idCupom) {
		this.idCupom = idCupom;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getStatusCupom() {
		return statusCupom;
	}

	public void setStatusCupom(String statusCupom) {
		this.statusCupom = statusCupom;
	}

}
