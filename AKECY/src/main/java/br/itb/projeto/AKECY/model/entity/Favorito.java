package br.itb.projeto.AKECY.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Favorito")
public class Favorito {
	
	@Id
	@GeneratedValue
	          (strategy = GenerationType.IDENTITY)
	private long idFavorito;
	
	@ManyToOne
	@JoinColumn(name = "idProduto")
	private Produto produto;
	
	@ManyToOne
	@JoinColumn(name = "idUsuario")
	private Usuario usuario;
	
	

	public long getIdFavorito() {
		return idFavorito;
	}

	public void setIdFavorito(long idFavorito) {
		this.idFavorito = idFavorito;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	

}
