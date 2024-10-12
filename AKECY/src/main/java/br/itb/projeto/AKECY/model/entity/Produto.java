package br.itb.projeto.AKECY.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduto;
    
    private String nome;
    private String descricao;
    private String descricaoCompleta;
    private String tamanhosDisponiveis;
    private byte[] foto1; 
    private byte[] foto2;
    private byte[] foto3;
    private byte[] foto4;
    private byte[] foto5;
    private String preco;
    
    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;
    
    private String statusProd;
    private transient String base64Image;
    private transient String base64Image2;
    private transient String base64Image3;
    private transient String base64Image4;
    private transient String base64Image5;
    
 // Getters and Setters 
    
	public Long getIdProduto() {
		return idProduto;
	}
	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricao_completa() {
		return descricaoCompleta;
	}
	public void setDescricao_completa(String descricao_completa) {
		this.descricaoCompleta = descricao_completa;
	}
	public String getTamanhos_disponiveis() {
		return tamanhosDisponiveis;
	}
	public void setTamanhos_disponiveis(String tamanhos_disponiveis) {
		this.tamanhosDisponiveis = tamanhos_disponiveis;
	}
	public byte[] getFoto1() {
		return foto1;
	}
	public void setFoto1(byte[] foto1) {
		this.foto1 = foto1;
	}
	public byte[] getFoto2() {
		return foto2;
	}
	public void setFoto2(byte[] foto2) {
		this.foto2 = foto2;
	}
	public byte[] getFoto3() {
		return foto3;
	}
	public void setFoto3(byte[] foto3) {
		this.foto3 = foto3;
	}
	public byte[] getFoto4() {
		return foto4;
	}
	public void setFoto4(byte[] foto4) {
		this.foto4 = foto4;
	}
	public byte[] getFoto5() {
		return foto5;
	}
	public void setFoto5(byte[] foto5) {
		this.foto5 = foto5;
	}
	public String getPreco() {
		return preco;
	}
	public void setPreco(String preco) {
		this.preco = preco;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public String getStatusProd() {
		return statusProd;
	}
	public void setStatusProd(String statusProd) {
		this.statusProd = statusProd;
	}
	public String getBase64Image() {
		return base64Image;
	}
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}
	public String getBase64Image2() {
		return base64Image2;
	}
	public void setBase64Image2(String base64Image2) {
		this.base64Image2 = base64Image2;
	}
	public String getBase64Image3() {
		return base64Image3;
	}
	public void setBase64Image3(String base64Image3) {
		this.base64Image3 = base64Image3;
	}
	public String getBase64Image4() {
		return base64Image4;
	}
	public void setBase64Image4(String base64Image4) {
		this.base64Image4 = base64Image4;
	}
	public String getBase64Image5() {
		return base64Image5;
	}
	public void setBase64Image5(String base64Image5) {
		this.base64Image5 = base64Image5;
	}
}