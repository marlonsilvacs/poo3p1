package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    private String nome;
    private String descricao;
    private LocalDate dataFabricacao;
    private LocalDate dataValidade;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private int quantidadeEstoque;
    private Categoria categoria;

    public Produto(String codigo, String nome, String descricao, LocalDate dataFabricacao,
                   LocalDate dataValidade, BigDecimal precoCompra, BigDecimal precoVenda,
                   int quantidadeEstoque, Categoria categoria) {
        
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.dataFabricacao = dataFabricacao;
        this.dataValidade = dataValidade;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
    }
    
    public Produto() {}
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataFabricacao() { return dataFabricacao; }
    public LocalDate getDataValidade() { return dataValidade; }
    public BigDecimal getPrecoCompra() { return precoCompra; }
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public Categoria getCategoria() { return categoria; }
    public BigDecimal calcularMargemLucro() {
        if (precoVenda == null || precoCompra == null || precoCompra.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return precoVenda.subtract(precoCompra);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(codigo, produto.codigo);
    }
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}