package CRUD;

import CONEXAO_BANCO.Banco_dados;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * author Analice
 */
public class Vendas extends javax.swing.JFrame {

    public Vendas() {
        initComponents();

    }

    Banco_dados bd = new Banco_dados();

    private void pesquisa_cliente() {
        if (bd.getConnection()) {
            try {
                String query = "select * from cliente where nome_cli like ?";
                PreparedStatement smtp = bd.conexao.prepareStatement(query);
                smtp.setString(1, "%" + jT_nome_cliente.getText() + "%");
                ResultSet rs = smtp.executeQuery();
                while (rs.next()) {
                    String add1 = rs.getString("id_cliente");
                    jT_codigo_cliente.setText(add1);
                    String add2 = rs.getString("nome_cli");
                    jT_nome_cliente.setText(add2);
                    String add3 = rs.getString("cpf_cli");
                    jT_cpf_cliente.setText(add3);
                }
                smtp.close();
                bd.conexao.close();
            } catch (SQLException e) {
                System.out.println("Erro ao pesquisar." + e.toString());
            }
        }
    }

    private void realizar_venda() {
        try {
            String query = "INSERT INTO vendas (id_cliente) VALUES (?)";
            PreparedStatement smtp = bd.conexao.prepareStatement(query);
            smtp.setString(1, jT_codigo_cliente.getText());
            smtp.executeUpdate();
            smtp.close();
            bd.conexao.close();
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro de gravação!" + erro.toString());
        }
    }

    private void pesquisa_produto() {
        if (bd.getConnection()) {
            try {
                String query = "select * from produto where descricao like ?";
                PreparedStatement smtp = bd.conexao.prepareStatement(query);
                smtp.setString(1, "%" + jT_codigo_produto.getText() + "%");
                ResultSet rs = smtp.executeQuery();
                while (rs.next()) {
                    String add1 = rs.getString("id_produto");
                    jT_codigo_produto.setText(add1);
                    String add2 = rs.getString("descricao");
                    jT_descricao_produto.setText(add2);
                    String add3 = rs.getString("valor_unitario");
                    jT_preco_produto.setText(add3);
                }
                smtp.close();
                bd.conexao.close();

            } catch (SQLException e) {
                System.out.println("Erro ao pesquisar." + e.toString());
            }
        }
    }

    private void venda() {
        if (bd.getConnection()) {
            try {
                String query = "INSERT vendas(id_cliente) VALUES(?)";
                PreparedStatement smtp = bd.conexao.prepareStatement(query);
                smtp.setString(1, jT_codigo_cliente.getText());
                smtp.executeUpdate();
                smtp.close();
                bd.conexao.close();
            } catch (SQLException e) {
                System.out.println("Erro ao inserir venda!" + e.toString());
            }
        }
    }

    private void id_venda() {
        if (bd.getConnection()) {
            try {
                String query = "select max(id_vendas) as id_vendas from vendas";
                PreparedStatement smtp = bd.conexao.prepareStatement(query);
                ResultSet rs = smtp.executeQuery();
                while (rs.next()) {
                    String add1 = rs.getString("id_vendas");
                    jT_nota_fiscal.setText(add1);
                }
                smtp.close();
                bd.conexao.close();

            } catch (SQLException e) {
                System.out.println("Erro de gravação no Banco!." + e.toString());
            }
        }
    }

    private void calcular_produto() {
        String quant_text = jT_quant_produto.getText();
        String preco_text = jT_preco_produto.getText();

        // Verifica se os campos de texto não estão vazios
        if (!quant_text.isEmpty() && !preco_text.isEmpty()) {
            float quantidade = Float.parseFloat(quant_text);
            float valor = Float.parseFloat(preco_text);
            float total = quantidade * valor;
            jT_valor_total.setText(String.valueOf(total));
        } else {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            jT_valor_total.setText("0.0"); // Defina o total como zero
        }
    }

    private void adicionar_itens_vendas() {
        if (bd.getConnection()) {
            try {
                String query = "INSERT vendas_has_produto(id_vendas, id_produto, item_quantidade, item_valor, item_total) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement smtp = bd.conexao.prepareStatement(query);
                smtp.setString(1, jT_nota_fiscal.getText());
                smtp.setString(2, jT_codigo_produto.getText());
                smtp.setString(3, jT_quant_produto.getText());
                smtp.setString(4, jT_preco_produto.getText());
                smtp.setString(5, jT_valor_total.getText());
                smtp.executeUpdate();
                smtp.close();
                bd.conexao.close();
                JOptionPane.showMessageDialog(null, "Venda inserida no banco!");
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null, "Erro de gravação!" + erro.toString());
            }
        }
    }

    private void consultarNf() {
        if (bd.getConnection()) {
            try {
                String query = "SELECT vendas_has_produto.id_produto,produto.descricao,	vendas_has_produto.item_valor,vendas_has_produto.item_quantidade,vendas_has_produto.item_total FROM vendas_has_produto INNER JOIN produto ON vendas_has_produto.id_produto = produto.id_produto   WHERE vendas_has_produto.id_vendas = ?";
                PreparedStatement stmt = bd.conexao.prepareStatement(query);
                stmt.setString(1,jT_nota_fiscal.getText()); // Corrigido: "+" para concatenar

                ResultSet rs = stmt.executeQuery();
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setNumRows(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id_produto"),
                        rs.getString("descricao"),
                        rs.getString("item_valor"),
                        rs.getString("item_quantidade"),
                        rs.getString("item_total") // Corrigido: "total" para "item_total"
                    });
                }
            } catch (SQLException erro) {
                JOptionPane.showMessageDialog(null, "Erro de pesquisa: " + erro.toString());
            }
        }
    }

    private void somarNf() {
        try {
            if (bd.getConnection()) {
                String query = "SELECT SUM(item_total) AS total_sum FROM vendas_has_produto WHERE id_vendas = ?";
                PreparedStatement stmt = bd.conexao.prepareStatement(query);
                stmt.setString(1, jT_nota_fiscal.getText());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String totalSum = rs.getString("total_sum");
                    jT_soma.setText(totalSum);
                }
            }
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro de conexão: " + erro.toString());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jT_codigo_cliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jT_cpf_cliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jT_nome_cliente = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jT_quant_produto = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jT_codigo_produto = new javax.swing.JTextField();
        jT_nota_fiscal = new javax.swing.JTextField();
        jT_descricao_produto = new javax.swing.JTextField();
        jT_preco_produto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jT_valor_total = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jT_soma = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(208, 226, 237));

        jLabel3.setText("CPF:");

        jLabel2.setText("Código do Cliente");

        jLabel1.setText("Nome:");

        jT_nome_cliente.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jT_nome_clienteCaretUpdate(evt);
            }
        });
        jT_nome_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jT_nome_clienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jT_codigo_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jT_nome_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jT_cpf_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jT_nome_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jT_codigo_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jT_cpf_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jButton1.setText("INICIAR VENDA");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("ADICIONAR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel7.setText("Valor Unitário:");

        jT_quant_produto.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jT_quant_produtoCaretUpdate(evt);
            }
        });

        jLabel8.setText("Quantidade");

        jLabel6.setText("Produto:");

        jT_codigo_produto.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jT_codigo_produtoCaretUpdate(evt);
            }
        });
        jT_codigo_produto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jT_codigo_produtoActionPerformed(evt);
            }
        });

        jLabel4.setText("Nota Fiscal:");

        jLabel5.setText("Código do Produto:");

        jLabel12.setText("Valor Total:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jT_preco_produto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jT_codigo_produto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(69, 69, 69)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jT_descricao_produto)
                        .addGap(198, 198, 198))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jT_quant_produto))
                                .addGap(68, 68, 68)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jT_valor_total, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jT_nota_fiscal, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(288, 288, 288)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jT_nota_fiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(41, 41, 41)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jT_codigo_produto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jT_descricao_produto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jT_preco_produto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jT_quant_produto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jT_valor_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton3.setText("NOVA VENDA");

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton4.setText("CANCELAR");

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton5.setText("FINALIZAR VENDA");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Código", "Descrição", "Valor Unitário", "Quantidade", "Valor Total"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel9.setText("VALOR TOTAL NF:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jT_soma)))))
                .addGap(60, 60, 60)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(44, 44, 44)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jT_soma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jT_nome_clienteCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jT_nome_clienteCaretUpdate
        //pesquisa_cliente();
    }//GEN-LAST:event_jT_nome_clienteCaretUpdate

    private void jT_codigo_produtoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jT_codigo_produtoCaretUpdate
        //pesquisa_produto();
    }//GEN-LAST:event_jT_codigo_produtoCaretUpdate

    private void jT_quant_produtoCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jT_quant_produtoCaretUpdate
        calcular_produto();
    }//GEN-LAST:event_jT_quant_produtoCaretUpdate

    private void jT_nome_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jT_nome_clienteActionPerformed
        pesquisa_cliente();
    }//GEN-LAST:event_jT_nome_clienteActionPerformed

    private void jT_codigo_produtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jT_codigo_produtoActionPerformed
        pesquisa_produto();
    }//GEN-LAST:event_jT_codigo_produtoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        venda(); // gerar a venda
        id_venda(); // apresentar o número da nota fiscal
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       // adicionar_itens_vendas();
        consultarNf();
        somarNf();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Vendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Vendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Vendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Vendas.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Vendas().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jT_codigo_cliente;
    private javax.swing.JTextField jT_codigo_produto;
    private javax.swing.JTextField jT_cpf_cliente;
    private javax.swing.JTextField jT_descricao_produto;
    private javax.swing.JTextField jT_nome_cliente;
    private javax.swing.JTextField jT_nota_fiscal;
    private javax.swing.JTextField jT_preco_produto;
    private javax.swing.JTextField jT_quant_produto;
    private javax.swing.JTextField jT_soma;
    private javax.swing.JTextField jT_valor_total;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
