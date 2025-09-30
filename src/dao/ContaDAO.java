package dao;

import GerenciadorContaBancaria.ContaCorrente;
import GerenciadorContaBancaria.Movimentacao; // <-- IMPORT ADICIONADO
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp; 
import java.time.LocalDateTime; 
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    public void inserir(ContaCorrente conta) {
        String sql = "INSERT INTO contas (numero, titular, saldo) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, conta.getNumero());
            ps.setString(2, conta.getTitular());
            ps.setDouble(3, conta.getSaldo());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir conta no banco de dados", e);
        }
    }

    public List<ContaCorrente> listar() {
        String sql = "SELECT * FROM contas";
        List<ContaCorrente> contas = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int numero = rs.getInt("numero");
                String titular = rs.getString("titular");
                double saldo = rs.getDouble("saldo");
                contas.add(new ContaCorrente(numero, titular, saldo));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar contas do banco de dados", e);
        }
        
        System.out.println("--- [DEBUG] ContaDAO.listar(): Buscou do banco. Total de contas encontradas: " + contas.size());
        
        return contas;
    }

    public ContaCorrente buscarPorNumero(int numero) {
        String sql = "SELECT * FROM contas WHERE numero = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String titular = rs.getString("titular");
                    double saldo = rs.getDouble("saldo");
                    return new ContaCorrente(numero, titular, saldo);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar conta por número", e);
        }
        return null; 
    }

    public void atualizarSaldo(int numero, double novoSaldo) {
        String sql = "UPDATE contas SET saldo = ? WHERE numero = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, novoSaldo);
            ps.setInt(2, numero);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar saldo no banco de dados", e);
        }
    }

    public void remover(int numero) {
        String sql = "DELETE FROM contas WHERE numero = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, numero);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao remover conta do banco de dados", e);
        }
    }

    public void inserirMovimentacao(Movimentacao movimentacao) {
        String sql = "INSERT INTO movimentacoes (numero_conta, tipo, valor) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, movimentacao.getNumeroConta());
            ps.setString(2, movimentacao.getTipo());
            ps.setDouble(3, movimentacao.getValor());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao inserir movimentação no banco de dados", e);
        }
    }

   
    public List<Movimentacao> listarExtrato(int numeroConta) {
        String sql = "SELECT * FROM movimentacoes WHERE numero_conta = ? ORDER BY data DESC";
        List<Movimentacao> extrato = new ArrayList<>();
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, numeroConta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tipo = rs.getString("tipo");
                    double valor = rs.getDouble("valor");
                    Timestamp dataSql = rs.getTimestamp("data");
                    LocalDateTime data = dataSql.toLocalDateTime();
                    
                    Movimentacao mov = new Movimentacao(numeroConta, tipo, valor);
                    mov.setId(id);
                    mov.setData(data);
                    extrato.add(mov);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar extrato do banco de dados", e);
        }
        return extrato;
    }
}