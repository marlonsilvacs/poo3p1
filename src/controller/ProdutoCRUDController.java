package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.Categoria;
import modelo.Produto;
import servico.ServicoProduto;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProdutoCRUDController implements Initializable {

    private ServicoProduto servicoProduto;
    private ObservableList<Produto> produtosObservableList;
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextArea txtDescricao;
    @FXML private DatePicker dpFabricacao;
    @FXML private DatePicker dpValidade;
    @FXML private TextField txtPrecoCompra;
    @FXML private TextField txtPrecoVenda;
    @FXML private TextField txtEstoque;
    @FXML private ComboBox<Categoria> cmbCategoria;

    @FXML private TableView<Produto> tblProdutos;
    @FXML private TableColumn<Produto, String> colCodigo;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, BigDecimal> colPrecoVenda;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, LocalDate> colValidade;
    @FXML private TableColumn<Produto, Categoria> colCategoria;

    @FXML private Button btnSalvar;
    @FXML private Button btnExcluir;
    @FXML private Button btnLimpar;

    @FXML private TextArea txtRelatorio;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoProduto = new ServicoProduto();
        configurarTabela();
        carregarDados();
        configurarComboBox();
        
        // Listener para selecionar produto na tabela e preencher os campos
        tblProdutos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    preencherCampos(newSelection);
                }
            }
        );
    }
    
    private void configurarComboBox() {
        // Mock de categorias para demonstração
        ObservableList<Categoria> categorias = FXCollections.observableArrayList(
            new Categoria(1, "Higiene", "Produtos de uso pessoal", "Perfumaria"),
            new Categoria(2, "Alimentos", "Comestíveis não perecíveis", "Mercearia"),
            new Categoria(3, "Limpeza", "Produtos de limpeza geral", "Setor Químico")
        );
        cmbCategoria.setItems(categorias);
        
        // Exibe o nome da categoria no ComboBox
        cmbCategoria.setConverter(new javafx.util.StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria == null ? null : categoria.getNome();
            }

            @Override
            public Categoria fromString(String string) {

                return null;
            }
        });
        
        if (!categorias.isEmpty()) {
            cmbCategoria.getSelectionModel().selectFirst();
        }
    }

    private void configurarTabela() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPrecoVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        colValidade.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));
        
        // Configura a coluna Categoria para exibir o nome
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    private void carregarDados() {
        produtosObservableList = FXCollections.observableArrayList(servicoProduto.listarTodos());
        tblProdutos.setItems(produtosObservableList);
    }
    
    private void preencherCampos(Produto produto) {
        txtCodigo.setText(produto.getCodigo());
        txtCodigo.setDisable(true); // Não permite alterar o código ao editar
        txtNome.setText(produto.getNome());
        txtDescricao.setText(produto.getDescricao());
        dpFabricacao.setValue(produto.getDataFabricacao());
        dpValidade.setValue(produto.getDataValidade());
        txtPrecoCompra.setText(produto.getPrecoCompra().toPlainString());
        txtPrecoVenda.setText(produto.getPrecoVenda().toPlainString());
        txtEstoque.setText(String.valueOf(produto.getQuantidadeEstoque()));
        cmbCategoria.getSelectionModel().select(produto.getCategoria());
        btnSalvar.setText("Atualizar");
    }
    
    @FXML
    private void limparCampos() {
        txtCodigo.clear();
        txtCodigo.setDisable(false); // Libera o código para novo cadastro
        txtNome.clear();
        txtDescricao.clear();
        dpFabricacao.setValue(null);
        dpValidade.setValue(null);
        txtPrecoCompra.clear();
        txtPrecoVenda.clear();
        txtEstoque.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        tblProdutos.getSelectionModel().clearSelection();
        btnSalvar.setText("Salvar");
    }
    
    private Produto criarProdutoDosCampos() {
        // Tenta parsear os valores, usando 0/BigDecimal.ZERO em caso de erro, a validação completa está no ServicoProduto
        try {
            String codigo = txtCodigo.getText().trim();
            String nome = txtNome.getText().trim();
            String descricao = txtDescricao.getText().trim();
            LocalDate fab = dpFabricacao.getValue();
            LocalDate val = dpValidade.getValue();
            
            BigDecimal precoCompra = new BigDecimal(txtPrecoCompra.getText().replace(",", ".").trim());
            BigDecimal precoVenda = new BigDecimal(txtPrecoVenda.getText().replace(",", ".").trim());
            int estoque = Integer.parseInt(txtEstoque.getText().trim());
            
            Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();

            return new Produto(codigo, nome, descricao, fab, val, precoCompra, precoVenda, estoque, categoria);
        } catch (NumberFormatException | NullPointerException e) {
            // A exceção mais específica será tratada na validação do serviço, mas essa pega erros de formato numérico/data
            mostrarAlerta("Erro de Formato", "Verifique se os campos numéricos/data estão preenchidos corretamente.");
            return null;
        }
    }

    @FXML
    private void handleSalvar() {
        Produto produto = criarProdutoDosCampos();
        if (produto == null) return;

        try {
            if (btnSalvar.getText().equals("Salvar")) {
                servicoProduto.adicionar(produto);
                mostrarAlerta("Sucesso", "Produto cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            } else { // Atualizar
                servicoProduto.atualizar(produto);
                mostrarAlerta("Sucesso", "Produto atualizado com sucesso!", Alert.AlertType.INFORMATION);
            }
            limparCampos();
            carregarDados(); // Recarrega a tabela
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Erro de Validação", e.getMessage(), Alert.AlertType.ERROR);
        } catch (IOException e) {
            mostrarAlerta("Erro de Persistência", "Erro ao salvar no arquivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleExcluir() {
        Produto selecionado = tblProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Atenção", "Selecione um produto para excluir.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, 
            "Tem certeza que deseja excluir o produto " + selecionado.getNome() + "?", 
            ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText("Confirmação de Exclusão");
        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    if (servicoProduto.remover(selecionado.getCodigo())) {
                        mostrarAlerta("Sucesso", "Produto excluído com sucesso!", Alert.AlertType.INFORMATION);
                        limparCampos();
                        carregarDados();
                    }
                } catch (IOException e) {
                    mostrarAlerta("Erro de Persistência", "Erro ao excluir do arquivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleBuscar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarAlerta("Atenção", "Informe o código para a consulta.", Alert.AlertType.WARNING);
            return;
        }
        
        servicoProduto.buscarPorCodigo(codigo)
            .ifPresentOrElse(
                this::preencherCampos, // Se encontrado, preenche os campos
                () -> mostrarAlerta("Consulta", "Produto com código " + codigo + " não encontrado.", Alert.AlertType.INFORMATION)
            );
    }
    
    // --- MÉTODOS DE RELATÓRIO ---
    
    @FXML
    private void gerarRelatorioVencimento() {
        // Filtrar produtos próximos ao vencimento (próximos 60 dias)
        long DIAS_VENCIMENTO = 60;
        var lista = servicoProduto.relatorioProdutosProximosVencimento(DIAS_VENCIMENTO);
        
        String relatorio = " Produtos próximos de vencer " + DIAS_VENCIMENTO + " dias \n";
        if (lista.isEmpty()) {
             relatorio += "Nenhum produto encontrado.\n";
        } else {
            for (Produto p : lista) {
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), p.getDataValidade());
                relatorio += String.format("%s (%s) Data de Validade: %s (Faltam %d dias)\n", 
                                          p.getNome(), p.getCodigo(), p.getDataValidade(), dias);
            }
        }
        txtRelatorio.setText(relatorio);
    }
    
    @FXML
    private void gerarRelatorioEstoqueBaixo() {
        // Filtrar produtos com estoque baixo (menos de 10 unidades)
        int ESTOQUE_LIMITE = 10;
        var lista = servicoProduto.relatorioProdutosEstoqueBaixo(ESTOQUE_LIMITE);
        
        String relatorio = "Produtos com estoque abaixo de " + ESTOQUE_LIMITE + " unidades ---\n";
        if (lista.isEmpty()) {
             relatorio += "Nenhum produto encontrado.\n";
        } else {
            for (Produto p : lista) {
                relatorio += String.format("%s (%s) - Estoque Atual: %d\n", 
                                          p.getNome(), p.getCodigo(), p.getQuantidadeEstoque());
            }
        }
        txtRelatorio.setText(relatorio);
    }
    
    @FXML
    private void gerarRelatorioMargemMedia() {
        // Calcular margem de lucro média por categoria
        var mapa = servicoProduto.relatorioMargemLucroMediaPorCategoria();
        
        String relatorio = " Margem de Lucro Média por Categoria \n";
        if (mapa.isEmpty()) {
             relatorio += "Nenhuma margem calculada.\n";
        } else {
            for (Map.Entry<String, Double> entry : mapa.entrySet()) {
                relatorio += String.format("Categoria '%s': R$ %.2f\n", 
                                          entry.getKey(), entry.getValue());
            }
        }
        txtRelatorio.setText(relatorio);
    }

    // --- UTILS ---
    private void mostrarAlerta(String titulo, String mensagem) {
        mostrarAlerta(titulo, mensagem, Alert.AlertType.ERROR);
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}