package servico;

import dao.ProdutoCSVDAO;
import modelo.Produto;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ServicoProduto {
    private List<Produto> produtos;
    private final ProdutoCSVDAO dao;
    private static final Pattern CODIGO_PATTERN = Pattern.compile("^[a-zA-Z0-9]{8}$");

    public ServicoProduto() {
        this.dao = new ProdutoCSVDAO();
        try {
            this.produtos = dao.carregar();
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados dos produtos: " + e.getMessage());
            this.produtos = new ArrayList<>();
        }
    }
    private void validar(Produto produto) throws IllegalArgumentException {
        Objects.requireNonNull(produto, "Produto não pode ser nulo.");
        if (produto.getCodigo() == null || !CODIGO_PATTERN.matcher(produto.getCodigo()).matches()) {
            throw new IllegalArgumentException("Código inválido. Deve ter 8 caracteres alfanuméricos.");
        }
        if (produtos.stream().anyMatch(p -> p.getCodigo().equals(produto.getCodigo()) && p != produto)) {
             throw new IllegalArgumentException("Produto com o código " + produto.getCodigo() + " já cadastrado.");
        }
        if (produto.getNome() == null || produto.getNome().trim().length() < 3) {
            throw new IllegalArgumentException("Nome inválido. Deve ter no mínimo 3 caracteres.");
        }
        if (produto.getDataFabricacao() == null || produto.getDataFabricacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de fabricação inválida. Não pode ser futura.");
        }
        if (produto.getDataValidade() == null || produto.getDataValidade().isBefore(produto.getDataFabricacao())) {
            throw new IllegalArgumentException("Data de validade inválida. Não pode ser anterior à data de fabricação.");
        }
        if (produto.getPrecoCompra() == null || produto.getPrecoCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de compra deve ser um valor positivo.");
        }
        if (produto.getPrecoVenda() == null || produto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser um valor positivo.");
        }
        if (produto.getPrecoVenda().compareTo(produto.getPrecoCompra()) <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que o preço de compra.");
        }
        if (produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa.");
        }
        Objects.requireNonNull(produto.getCategoria(), "O produto deve ter uma categoria.");
    }
    
    public void adicionar(Produto produto) throws IllegalArgumentException, IOException {
        validar(produto);
        if (produtos.stream().anyMatch(p -> p.getCodigo().equals(produto.getCodigo()))) {
            throw new IllegalArgumentException("Produto com o código " + produto.getCodigo() + " já cadastrado.");
        }
        produtos.add(produto);
        dao.salvar(produtos);
    }

    public void atualizar(Produto produto) throws IllegalArgumentException, IOException {
        validar(produto); 
        
        Optional<Produto> produtoExistente = produtos.stream()
            .filter(p -> p.getCodigo().equals(produto.getCodigo()))
            .findFirst();

        if (produtoExistente.isPresent()) {
            Produto p = produtoExistente.get();
            p.setNome(produto.getNome());
            p.setDescricao(produto.getDescricao());
            p.setDataFabricacao(produto.getDataFabricacao());
            p.setDataValidade(produto.getDataValidade());
            p.setPrecoCompra(produto.getPrecoCompra());
            p.setPrecoVenda(produto.getPrecoVenda());
            p.setQuantidadeEstoque(produto.getQuantidadeEstoque());
            p.setCategoria(produto.getCategoria());
            
            dao.salvar(produtos);
        } else {
            throw new IllegalArgumentException("Produto com o código " + produto.getCodigo() + " não encontrado para atualização.");
        }
    }

    public boolean remover(String codigo) throws IOException {
        boolean removido = produtos.removeIf(p -> p.getCodigo().equals(codigo));
        if (removido) {
            dao.salvar(produtos);
        }
        return removido;
    }
    
    public Optional<Produto> buscarPorCodigo(String codigo) {
        return produtos.stream()
                       .filter(p -> p.getCodigo().equals(codigo))
                       .findFirst();
    }
    
    public List<Produto> listarTodos() {
        return produtos;
    }
    public List<Produto> relatorioProdutosProximosVencimento(long dias) {
        final LocalDate dataLimite = LocalDate.now().plusDays(dias);
        
        return produtos.stream()
            .filter(p -> p.getDataValidade() != null)
            .filter(p -> p.getDataValidade().isAfter(LocalDate.now())) 
            .filter(p -> p.getDataValidade().isBefore(dataLimite) || p.getDataValidade().isEqual(dataLimite))
            .collect(Collectors.toList());
    }
    public List<Produto> relatorioProdutosEstoqueBaixo(int limite) {
        return produtos.stream()
            .filter(p -> p.getQuantidadeEstoque() < limite)
            .collect(Collectors.toList());
    }
    public Map<String, Double> relatorioMargemLucroMediaPorCategoria() {
        return produtos.stream()
            .collect(Collectors.groupingBy(
                p -> p.getCategoria().getNome(), 
                Collectors.averagingDouble(p -> p.calcularMargemLucro().doubleValue()) 
            ));
    }
    public Map<String, List<Produto>> relatorioProdutosAgrupadosPorSetor() {
        return produtos.stream()
            .collect(Collectors.groupingBy(p -> p.getCategoria().getSetor()));
    }
}
