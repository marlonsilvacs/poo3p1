package modelo;

import java.io.Serializable;
import java.util.Objects;

public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String descricao;
    private String setor;

    public Categoria(int id, String nome, String descricao, String setor) {
        this.id = id;
        this.nome = Objects.requireNonNull(nome, "A categoria tem que ter um nome.");
        this.descricao = Objects.requireNonNull(descricao, "Está faltando uma descrição na categoria.");
        this.setor = Objects.requireNonNull(setor, "Está faltando setor na categoria.");
    }

    public Categoria() {}
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getSetor() { return setor; }
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setSetor(String setor) { this.setor = setor; }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return id == categoria.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome + " (Setor: " + setor + ")";
    }
}