package br.univali.cc.prog3.banco.visao;

import br.univali.cc.prog3.banco.dominio.Banco;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author 1978233
 */
public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private Banco banco;
    
    private static final Map<String, String> USUARIOS = new HashMap<>();
    static {
        USUARIOS.put("admin", "Admin@123");
        USUARIOS.put("usuario", "Usuario@456");
        USUARIOS.put("gerente", "Gerente@789");
    }
    
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public LoginGUI(Banco banco) {
        this.banco = banco;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Usuário:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        mainPanel.add(loginButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(errorLabel, gbc);
        
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Banco do Brasil - Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("<html><center>A senha deve conter:<br>" +
                                "• Mínimo 8 caracteres<br>" +
                                "• 1 letra maiúscula<br>" +
                                "• 1 número<br>" +
                                "• 1 caractere especial (@#$%^&+=)</center></html>"));
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
        
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
    }
    
    private void setupWindow() {
        setTitle("Login - Banco do Brasil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void realizarLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        errorLabel.setText(" ");
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, preencha todos os campos.");
            return;
        }
        
        if (!validarSenha(password)) {
            errorLabel.setText("Senha não atende aos requisitos mínimos.");
            return;
        }
        
        if (autenticarUsuario(username, password)) {
            errorLabel.setText(" ");
            JOptionPane.showMessageDialog(this, "Login realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            abrirBancoGUI();
            dispose();
        } else {
            errorLabel.setText("Usuário ou senha incorretos.");
            limparCampos();
        }
    }
    
    private boolean validarSenha(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    private boolean autenticarUsuario(String username, String password) {
        String senhaArmazenada = USUARIOS.get(username);
        return senhaArmazenada != null && senhaArmazenada.equals(password);
    }
    
    private void limparCampos() {
        passwordField.setText("");
        usernameField.requestFocus();
    }
    
    private void abrirBancoGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BancoGUI bancoGUI = new BancoGUI(banco);
                bancoGUI.setVisible(true);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Banco banco = new Banco("Banco do Brasil", 1);
                new LoginGUI(banco).setVisible(true);
            }
        });
    }
}