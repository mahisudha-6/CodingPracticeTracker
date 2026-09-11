package ui;

import exception.ValidationException;
import model.User;
import service.UserService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Screen for user login. Modern split-screen layout inspired by TaskFlow.
 */
public class LoginScreen extends JPanel {
    private final MainFrame mainFrame;
    private final UserService userService;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegisterLink; // Link to register screen
    private JLabel lblError;

    public LoginScreen(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridLayout(1, 2));
        setBackground(Color.WHITE);

        // ===================================================
        // LEFT PANEL: BRANDING (Blue background #4F46E5)
        // ===================================================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.decode("#4F46E5"));
        
        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        
        JLabel lblLogo = new JLabel("💻");
        lblLogo.setFont(new Font("SansSerif", Font.PLAIN, 80));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblLeftTitle = new JLabel("CodeTrack");
        lblLeftTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblLeftTitle.setForeground(Color.WHITE);
        lblLeftTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblLeftSub = new JLabel("Your Daily Practice Companion");
        lblLeftSub.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblLeftSub.setForeground(Color.decode("#EDE9FE")); // Light sky blue
        lblLeftSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblLeftDesc = new JLabel("<html><center>Manage your coding problems, track your practice consistency, and analyze your placement readiness in one beautiful interface.</center></html>");
        lblLeftDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblLeftDesc.setForeground(Color.decode("#F5F3FF"));
        lblLeftDesc.setPreferredSize(new Dimension(300, 80));
        lblLeftDesc.setMaximumSize(new Dimension(300, 80));
        lblLeftDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        leftContent.add(lblLogo);
        leftContent.add(Box.createRigidArea(new Dimension(0, 20)));
        leftContent.add(lblLeftTitle);
        leftContent.add(Box.createRigidArea(new Dimension(0, 8)));
        leftContent.add(lblLeftSub);
        leftContent.add(Box.createRigidArea(new Dimension(0, 25)));
        leftContent.add(lblLeftDesc);
        
        leftPanel.add(leftContent);

        // ===================================================
        // RIGHT PANEL: FORM (White background)
        // ===================================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(350, 420));
        formCard.setMinimumSize(new Dimension(350, 420));
        formCard.setMaximumSize(new Dimension(350, 420));
        
        // Headers
        JLabel lblTitle = new JLabel("Welcome Back!");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(Color.decode("#1E293B")); // Dark slate
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Please enter your details to sign in.");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.decode("#64748B")); // Slate gray
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblError.setForeground(Color.decode("#EF4444")); // Red-500
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        txtPassword = new JPasswordField(20);
        txtPassword.setEchoChar('*');
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Styling input field heights
        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 40);
        txtUsername.setMaximumSize(fieldSize);
        txtPassword.setMaximumSize(fieldSize);

        // Buttons
        btnLogin = new JButton("Login");
        ButtonStyleUtil.applyBlueStyle(btnLogin); // Blue #4F46E5
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Force the button to match form width
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setPreferredSize(new Dimension(350, 40));
        
        btnRegisterLink = new JButton("Don't have an account? Register");
        btnRegisterLink.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnRegisterLink.setForeground(Color.decode("#4F46E5"));
        btnRegisterLink.setBorderPainted(false);
        btnRegisterLink.setContentAreaFilled(false);
        btnRegisterLink.setFocusPainted(false);
        btnRegisterLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegisterLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble Layout inside Card
        formCard.add(lblTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 6)));
        formCard.add(lblSubtitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));
        formCard.add(lblError);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Username Area
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUsername.setForeground(Color.decode("#475569"));
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formCard.add(lblUsername);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));
        formCard.add(txtUsername);
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Password Area
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPassword.setForeground(Color.decode("#475569"));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formCard.add(lblPassword);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));
        formCard.add(txtPassword);
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));
        
        formCard.add(btnLogin);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Add centered registration row
        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        registerRow.setOpaque(false);
        registerRow.add(btnRegisterLink);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(registerRow);

        rightPanel.add(formCard);

        add(leftPanel);
        add(rightPanel);

        // Listeners
        btnLogin.addActionListener(e -> handleLogin());

        ActionListener enterAction = e -> handleLogin();
        txtUsername.addActionListener(enterAction);
        txtPassword.addActionListener(enterAction);

        btnRegisterLink.addActionListener(e -> {
            lblError.setText(" ");
            txtUsername.setText("");
            txtPassword.setText("");
            mainFrame.showScreen("REGISTER");
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        try {
            User user = userService.loginUser(username, password);
            lblError.setText(" ");
            txtUsername.setText("");
            txtPassword.setText("");
            mainFrame.loginSuccess(user);
        } catch (ValidationException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
