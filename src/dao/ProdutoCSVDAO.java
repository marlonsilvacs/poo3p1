package dao;

import modelo.Produto;
import modelo.Categoria;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoCSVDAO {
    private static final String NOME_ARQUIVO = "produtos.csv";
    private static final String SEPARADOR = ";";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String CABECALHO = "codigo;nome;descricao;dataFabricacao;dataValidade;precoCompra;precoVenda;quantidadeEstoque;cat_id;cat_nome;cat_descricao;cat_setor\n";

    public List<Produto> carregar() throws IOException {
        List<Produto> produtos = new ArrayList<>();
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                writer.write(CABECALHO);
            }
            return produtos;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine(); 
            if (linha == null || !linha.trim().equals(CABECALHO.trim())) {
                System.err.println("Aviso: Arquivo CSV com cabeçalho ausente ou incorreto. Tentando carregar dados mesmo assim.");
            }

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] campos = linha.split(SEPARADOR, -1);

                try {
                    String codigo = campos[0];
                    String nome = campos[1];
                    String descricao = campos[2];
                    LocalDate dataFabricacao = LocalDate.parse(campos[3], DATE_FORMATTER);
                    LocalDate dataValidade = LocalDate.parse(campos[4], DATE_FORMATTER);
                    BigDecimal precoCompra = new BigDecimal(campos[5]);
                    BigDecimal precoVenda = new BigDecimal(campos[6]);
                    int quantidadeEstoque = Integer.parseInt(campos[7]);
                    Categoria categoria = new Categoria(
                        Integer.parseInt(campos[8]),
                        campos[9],                  
                        campos[10],                 
                        campos[11]                  
                    );

                    produtos.add(new Produto(codigo, nome, descricao, dataFabricacao, dataValidade,
                                             precoCompra, precoVenda, quantidadeEstoque, categoria));
                } catch (Exception e) {
                    System.err.println("Erro ao ler linha do CSV: " + linha + ". Erro: " + e.getMessage());
                }
            }
        }
        return produtos;
    }

    public void salvar(List<Produto> produtos) throws IOException {
        String conteudo = produtos.stream()
                .map(this::produtoParaLinhaCSV)
                .collect(Collectors.joining("\n"));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO))) {
            writer.write(CABECALHO);
            writer.write(conteudo);
            if (!produtos.isEmpty()) {
                writer.write("\n"); 
            }
        }
    }

    private String produtoParaLinhaCSV(Produto p) {
        return String.join(SEPARADOR,
            p.getCodigo(),
            p.getNome(),
            p.getDescricao(),
            p.getDataFabricacao().format(DATE_FORMATTER),
            p.getDataValidade().format(DATE_FORMATTER),
            p.getPrecoCompra().toPlainString(),
            p.getPrecoVenda().toPlainString(),
            String.valueOf(p.getQuantidadeEstoque()),
            String.valueOf(p.getCategoria().getId()),
            p.getCategoria().getNome(),
            p.getCategoria().getDescricao(),
            p.getCategoria().getSetor()
        );
    }
}