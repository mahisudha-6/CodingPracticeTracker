package ui;

import exception.ValidationException;
import model.User;
import service.UserService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;

/**
 * Redesigned User Profile Screen containing two clean panels:
 * Card 1: User Profile details with circular avatar, name, and Java Developer subtitle.
 * Card 2: Security settings for changing passwords.
 */
public class UserProfileScreen extends JPanel {
    private final MainFrame mainFrame;
    private final UserService userService;

    // Profile detail labels
    private JLabel lblNameHeader;
    private JLabel lblNameVal;
    private JLabel lblEmailVal;
    private JLabel lblUsernameVal;
    private JLabel lblJoinedVal;

    // Password inputs
    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnUpdatePassword;
    private JButton btnEditProfile;

    public UserProfileScreen(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Screen Title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        JLabel lblScreenTitle = new JLabel("My Account Settings");
        lblScreenTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblScreenTitle.setForeground(Color.decode("#0F172A"));
        lblScreenTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.add(lblScreenTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Center split Panel (Left Card: User Profile, Right Card: Security)
        JPanel bodyPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        bodyPanel.setBackground(Color.WHITE);

        // ==========================================
        // CARD 1: USER PROFILE CARD
        // ==========================================
        JPanel profileCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#F8FAFC"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setOpaque(false);
        profileCard.setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. Circular Avatar Placeholder
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#CBD5E1")); // Circle fill
                g2.fillOval(0, 0, 70, 70);
                g2.setColor(Color.decode("#FFFFFF"));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 40));
                g2.drawString("👤", 15, 50); // Draws centered avatar
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(70, 70));
        avatarPanel.setMinimumSize(new Dimension(70, 70));
        avatarPanel.setMaximumSize(new Dimension(70, 70));
        avatarPanel.setOpaque(false);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Profile Names Header
        lblNameHeader = new JLabel("Mahisudha");
        lblNameHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNameHeader.setForeground(Color.decode("#1E293B"));
        lblNameHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        profileCard.add(avatarPanel);
        profileCard.add(Box.createRigidArea(new Dimension(0, 10)));
        profileCard.add(lblNameHeader);
        profileCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. User details list
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblNameVal = new JLabel("-");
        lblEmailVal = new JLabel("-");
        lblUsernameVal = new JLabel("-");
        lblJoinedVal = new JLabel("-");

        addDetailRow(detailsPanel, gbc, 0, "Full Name :", lblNameVal);
        addDetailRow(detailsPanel, gbc, 1, "Email :", lblEmailVal);
        addDetailRow(detailsPanel, gbc, 2, "Username :", lblUsernameVal);
        addDetailRow(detailsPanel, gbc, 3, "Member Since :", lblJoinedVal);

        profileCard.add(detailsPanel);
        profileCard.add(Box.createVerticalGlue());

        // 4. Edit Profile Button
        btnEditProfile = new JButton("Edit Profile");
        ButtonStyleUtil.applyBlueStyle(btnEditProfile); // Blue #4F46E5
        btnEditProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileCard.add(btnEditProfile);

        // ==========================================
        // CARD 2: CHANGE PASSWORD CARD
        // ==========================================
        JPanel passwordCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#F8FAFC"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        passwordCard.setLayout(new BoxLayout(passwordCard, BoxLayout.Y_AXIS));
        passwordCard.setOpaque(false);
        passwordCard.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel lblPassHeader = new JLabel("Change Password");
        lblPassHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblPassHeader.setForeground(Color.decode("#1E293B"));
        lblPassHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordCard.add(lblPassHeader);
        passwordCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Password input fields
        txtCurrentPassword = createStyledPasswordField();
        txtNewPassword = createStyledPasswordField();
        txtConfirmPassword = createStyledPasswordField();

        addPasswordField(passwordCard, "Current Password", txtCurrentPassword);
        addPasswordField(passwordCard, "New Password", txtNewPassword);
        addPasswordField(passwordCard, "Confirm Password", txtConfirmPassword);

        passwordCard.add(Box.createVerticalGlue());

        // Update Password Button
        btnUpdatePassword = new JButton("Update Password");
        ButtonStyleUtil.applyBlueStyle(btnUpdatePassword); // Blue #4F46E5
        btnUpdatePassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordCard.add(btnUpdatePassword);

        // Add both cards to split grid
        bodyPanel.add(profileCard);
        bodyPanel.add(passwordCard);
        add(bodyPanel, BorderLayout.CENTER);

        // Action Trigger Listeners
        btnUpdatePassword.addActionListener(e -> handleChangePassword());
        btnEditProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                    "Edit Profile is a layout placeholder. Database updates are handled via Change Password.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setEchoChar('*');
        pf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int rowY, String labelText, JLabel valLabel) {
        gbc.gridy = rowY;
        
        gbc.gridx = 0; gbc.weightx = 0.4;
        JLabel title = new JLabel(labelText);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(Color.decode("#475569"));
        panel.add(title, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        valLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valLabel.setForeground(Color.decode("#1E293B"));
        panel.add(valLabel, gbc);
    }

    private void addPasswordField(JPanel card, String labelText, JPasswordField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.decode("#475569"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(label);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(field);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    /**
     * Loads the active user's details into the text display labels.
     */
    public void loadProfile() {
        User user = mainFrame.getCurrentUser();
        if (user != null) {
            lblNameHeader.setText(user.getFullName());
            lblNameVal.setText(user.getFullName());
            lblEmailVal.setText(user.getEmail());
            lblUsernameVal.setText(user.getUsername());
            
            Timestamp joined = user.getCreatedAt();
            if (joined != null) {
                lblJoinedVal.setText(joined.toString().substring(0, 10));
            } else {
                lblJoinedVal.setText("-");
            }
        }
        
        // Reset password fields
        txtCurrentPassword.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");
    }

    private void handleChangePassword() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        String currentPass = new String(txtCurrentPassword.getPassword());
        String newPass = new String(txtNewPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());

        try {
            boolean success = userService.changePassword(user, currentPass, newPass, confirmPass);
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Password changed successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                txtCurrentPassword.setText("");
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Password update failed.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
