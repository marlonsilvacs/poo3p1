package org.suaempresa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.suaempresa.model.Categoria;
import org.suaempresa.model.Produto;
import org.suaempresa.service.ProdutoService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MainController {
    @FXML private TableView<Produto> produtoTable;
    @FXML private TableColumn<Produto, String> codigoColumn;
    @FXML private TableColumn<Produto, String> nomeColumn;
    @FXML private TableColumn<Produto, String> descricaoColumn;
    @FXML private TableColumn<Produto, LocalDate> dataFabricacaoColumn;
    @FXML private TableColumn<Produto, LocalDate> dataValidadeColumn;
    @FXML private TableColumn<Produto, BigDecimal> precoCompraColumn;
    @FXML private TableColumn<Produto, BigDecimal> precoVendaColumn;
    @FXML private TableColumn<Produto, Integer> quantidadeEstoqueColumn;
    @FXML private TableColumn<Produto, Categoria> categoriaColumn;
    @FXML private TextField codigoConsulta;

    private ProdutoService produtoService;

    public void setProdutoService(ProdutoService produtoService) {
        this.produtoService = produtoService;
        initializeTable();
    }

    private void initializeTable() {
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        descricaoColumn.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        dataFabricacaoColumn.setCellValueFactory(new PropertyValueFactory<>("dataFabricacao"));
        dataValidadeColumn.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));
        precoCompraColumn.setCellValueFactory(new PropertyValueFactory<>("precoCompra"));
        precoVendaColumn.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        quantidadeEstoqueColumn.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        atualizarTabela();
    }

    private void atualizarTabela() {
        produtoTable.setItems(FXCollections.observableArrayList(produtoService.listarTodos()));
    }

    @FXML
    private void handleCadastrar() {
        abrirFormularioProduto(null);
    }

    @FXML
    private void handleExcluir() {
        Produto selected = produtoTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            produtoService.excluirProduto(selected.getCodigo());
            atualizarTabela();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído com sucesso!");
        } else {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Selecione um produto para excluir.");
        }
    }

    @FXML
    private void handleConsultar() {
        String codigo = codigoConsulta.getText();
        if (!codigo.isEmpty()) {
            Produto produto = produtoService.consultarProduto(codigo);
            if (produto != null) {
                abrirFormularioProduto(produto);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Produto não encontrado.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Digite um código para consultar.");
        }
    }

    @FXML
    private void handleListar() {
        atualizarTabela();
    }

    @FXML
    private void handleRelatorios() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Produtos próximos ao vencimento:\n");
        produtoService.produtosProximosVencimento().forEach(p ->
                relatorio.append(p.getNome()).append(" - Vence em: ").append(p.getDataValidade()).append("\n"));

        relatorio.append("\nProdutos com estoque baixo:\n");
        produtoService.produtosEstoqueBaixo().forEach(p ->
                relatorio.append(p.getNome()).append(" - Estoque: ").append(p.getQuantidadeEstoque()).append("\n"));

        relatorio.append("\nMargem de lucro média por categoria:\n");
        produtoService.margemLucroMediaPorCategoria().forEach((cat, margem) ->
                relatorio.append(cat).append(": ").append(String.format("%.2f%%", margem)).append("\n"));

        relatorio.append("\nProdutos por setor:\n");
        produtoService.produtosPorSetor().forEach((setor, produtos) -> {
            relatorio.append(setor).append(":\n");
            produtos.forEach(p -> relatorio.append("  - ").append(p.getNome()).append("\n"));
        });

        TextArea textArea = new TextArea(relatorio.toString());
        textArea.setEditable(false);
        Scene scene = new Scene(textArea, 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Relatórios");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void abrirFormularioProduto(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/suaempresa/view/ProductForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(produto == null ? "Cadastrar Produto" : "Consultar Produto");

            ProductFormController controller = loader.getController();
            controller.setProdutoService(produtoService);
            controller.setProduto(produto);
            controller.setStage(stage);

            stage.showAndWait();
            atualizarTabela();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o formulário.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}