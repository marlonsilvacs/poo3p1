package org.suaempresa.service;

import org.suaempresa.model.Categoria;
import org.suaempresa.model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProdutoService {
    private List<Produto> produtos;
    private List<Categoria> categorias;
    private final String CSV_FILE = "produtos.csv";
    private final String CSV_CATEGORIA_FILE = "categorias.csv";

    public ProdutoService() {
        produtos = new ArrayList<>();
        categorias = new ArrayList<>();
        carregarCategorias();
        carregarProdutos();
    }

    // Validações
    public void validarProduto(Produto produto) throws IllegalArgumentException {
        if (produto.getCodigo() == null || !produto.getCodigo().matches("[A-Za-z0-9]{8}")) {
            throw new IllegalArgumentException("Código deve ter 8 caracteres alfanuméricos.");
        }
        if (produto.getNome() == null || produto.getNome().trim().isEmpty() || produto.getNome().length() < 3) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 3 caracteres.");
        }
        if (produto.getDataFabricacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de fabricação não pode ser futura.");
        }
        if (produto.getDataValidade().isBefore(produto.getDataFabricacao())) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior à fabricação.");
        }
        if (produto.getPrecoCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de compra deve ser positivo.");
        }
        if (produto.getPrecoVenda().compareTo(produto.getPrecoCompra()) <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que o preço de compra.");
        }
        if (produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa.");
        }
    }

    public void cadastrarProduto(Produto produto) {
        validarProduto(produto);
        produtos.add(produto);
        salvarProdutos();
    }

    public void excluirProduto(String codigo) {
        produtos.removeIf(p -> p.getCodigo().equals(codigo));
        salvarProdutos();
    }

    public Produto consultarProduto(String codigo) {
        return produtos.stream()
                .filter(p -> p.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    // Relatórios com Stream API
    public List<Produto> produtosProximosVencimento() {
        LocalDate limite = LocalDate.now().plusDays(60);
        return produtos.stream()
                .filter(p -> p.getDataValidade().isBefore(limite))
                .collect(Collectors.toList());
    }

    public List<Produto> produtosEstoqueBaixo() {
        return produtos.stream()
                .filter(p -> p.getQuantidadeEstoque() < 10)
                .collect(Collectors.toList());
    }

    public Map<String, Double> margemLucroMediaPorCategoria() {
        return produtos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoria().getNome(),
                        Collectors.averagingDouble(p ->
                                p.getPrecoVenda().subtract(p.getPrecoCompra())
                                        .divide(p.getPrecoCompra(), 2, BigDecimal.ROUND_HALF_UP)
                                        .doubleValue() * 100
                        )
                ));
    }

    public Map<String, List<Produto>> produtosPorSetor() {
        return produtos.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria().getSetor()));
    }

    // Manipulação de CSV
    private void carregarCategorias() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_CATEGORIA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dados = line.split(";");
                categorias.add(new Categoria(
                        Integer.parseInt(dados[0]),
                        dados[1],
                        dados[2],
                        dados[3]
                ));
            }
        } catch (IOException e) {
            // Inicializa com algumas categorias padrão se o arquivo não existir
            categorias.add(new Categoria(1, "Alimentos", "Produtos alimentícios", "Perecíveis"));
            categorias.add(new Categoria(2, "Bebidas", "Bebidas em geral", "Perecíveis"));
            salvarCategorias();
        }
    }

    private void salvarCategorias() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_CATEGORIA_FILE))) {
            for (Categoria c : categorias) {
                bw.write(c.getId() + ";" + c.getNome() + ";" + c.getDescricao() + ";" + c.getSetor());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarProdutos() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dados = line.split(";");
                Categoria categoria = categorias.stream()
                        .filter(c -> c.getId() == Integer.parseInt(dados[8]))
                        .findFirst()
                        .orElse(categorias.get(0));
                produtos.add(new Produto(
                        dados[0],
                        dados[1],
                        dados[2],
                        LocalDate.parse(dados[3]),
                        LocalDate.parse(dados[4]),
                        new BigDecimal(dados[5]),
                        new BigDecimal(dados[6]),
                        Integer.parseInt(dados[7]),
                        categoria
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarProdutos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (Produto p : produtos) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }
}