package ui;

import exception.ValidationException;
import service.UserService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Screen for user registration. Modern split-screen layout inspired by TaskFlow.
 */
public class RegistrationScreen extends JPanel {
    private final MainFrame mainFrame;
    private final UserService userService;

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnBack;
    private JLabel lblError;

    public RegistrationScreen(MainFrame mainFrame, UserService userService) {
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
        lblLeftSub.setForeground(Color.decode("#EDE9FE"));
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
        formCard.setPreferredSize(new Dimension(360, 560));
        formCard.setMinimumSize(new Dimension(360, 560));
        formCard.setMaximumSize(new Dimension(360, 560));

        // Header Labels
        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitle.setForeground(Color.decode("#1E293B"));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Join CodeTrack to track your progress");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.decode("#64748B"));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Error message label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblError.setForeground(Color.decode("#EF4444"));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Form Fields
        txtName = createStyledTextField();
        txtEmail = createStyledTextField();
        txtUsername = createStyledTextField();
        txtPassword = createStyledPasswordField();
        txtConfirmPassword = createStyledPasswordField();

        // Assemble fields
        formCard.add(lblTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(lblSubtitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 8)));
        formCard.add(lblError);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));

        addFormField(formCard, "Full Name", txtName);
        addFormField(formCard, "Email Address", txtEmail);
        addFormField(formCard, "Username", txtUsername);
        addFormField(formCard, "Password", txtPassword);
        addFormField(formCard, "Confirm Password", txtConfirmPassword);

        formCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Action Buttons
        btnRegister = new JButton("Register");
        ButtonStyleUtil.applyBlueStyle(btnRegister); // Blue #4F46E5

        btnBack = new JButton("Back");
        ButtonStyleUtil.applyGrayStyle(btnBack); // Gray #757575

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(btnRegister);
        buttonRow.add(btnBack);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(buttonRow);

        rightPanel.add(formCard);

        add(leftPanel);
        add(rightPanel);

        // Action Listeners
        btnRegister.addActionListener(e -> handleRegister());
        btnBack.addActionListener(e -> {
            clearFields();
            mainFrame.showScreen("LOGIN");
        });
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(20);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setEchoChar('*');
        pf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return pf;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(Color.decode("#475569"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void handleRegister() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        try {
            boolean success = userService.registerUser(name, email, username, password, confirmPassword);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Registration Successful! You can now log in.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                mainFrame.showScreen("LOGIN");
            } else {
                lblError.setText("Registration failed. Please try again.");
            }
        } catch (ValidationException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        lblError.setText(" ");
        txtName.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }
}
