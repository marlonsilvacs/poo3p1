package org.suaempresa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.suaempresa.model.Categoria;
import org.suaempresa.model.Produto;
import org.suaempresa.service.ProdutoService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductFormController {
    @FXML private TextField codigoField;
    @FXML private TextField nomeField;
    @FXML private TextArea descricaoField;
    @FXML private DatePicker dataFabricacaoPicker;
    @FXML private DatePicker dataValidadePicker;
    @FXML private TextField precoCompraField;
    @FXML private TextField precoVendaField;
    @FXML private TextField quantidadeEstoqueField;
    @FXML private ComboBox<Categoria> categoriaCombo;

    private ProdutoService produtoService;
    private Produto produto;
    private Stage stage;

    public void setProdutoService(ProdutoService produtoService) {
        this.produtoService = produtoService;
        categoriaCombo.getItems().addAll(produtoService.getCategorias());
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null) {
            codigoField.setText(produto.getCodigo());
            nomeField.setText(produto.getNome());
            descricaoField.setText(produto.getDescricao());
            dataFabricacaoPicker.setValue(produto.getDataFabricacao());
            dataValidadePicker.setValue(produto.getDataValidade());
            precoCompraField.setText(produto.getPrecoCompra().toString());
            precoVendaField.setText(produto.getPrecoVenda().toString());
            quantidadeEstoqueField.setText(String.valueOf(produto.getQuantidadeEstoque()));
            categoriaCombo.setValue(produto.getCategoria());
            codigoField.setDisable(true); // Não permite edição do código
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSalvar() {
        try {
            Produto novoProduto = new Produto(
                    codigoField.getText(),
                    nomeField.getText(),
                    descricaoField.getText(),
                    dataFabricacaoPicker.getValue(),
                    dataValidadePicker.getValue(),
                    new BigDecimal(precoCompraField.getText()),
                    new BigDecimal(precoVendaField.getText()),
                    Integer.parseInt(quantidadeEstoqueField.getText()),
                    categoriaCombo.getValue()
            );
            produtoService.cadastrarProduto(novoProduto);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Produto salvo com sucesso!");
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Formato inválido para números ou decimais.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}