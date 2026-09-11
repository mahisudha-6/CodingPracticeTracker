package ui;

import model.User;
import model.CodingProblem;
import service.ProblemService;
import service.UserService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window. Header and sidebar layout designed to match the mockup.
 */
public class MainFrame extends JFrame {
    private final UserService userService;
    private final ProblemService problemService;
    private User currentUser;

    // Root layouts
    private CardLayout rootCardLayout;
    private JPanel rootPanel;

    // App Workspace layouts
    private JPanel appPanel;
    private CardLayout contentCardLayout;
    private JPanel contentPanel;

    // Screen Panels
    private DashboardScreen dashboardScreen;
    private ViewProblemsScreen viewProblemsScreen;
    private ProblemFormScreen problemFormScreen;
    private ReportsScreen reportsScreen;
    private UserProfileScreen userProfileScreen;

    // Sidebar buttons for navigation
    private JButton btnDashboard;
    private JButton btnViewProblems;
    private JButton btnAddProblem;
    private JButton btnReports;
    private JButton btnProfile;
    private JButton btnLogout;

    public MainFrame() {
        this.userService = new UserService();
        this.problemService = new ProblemService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("CodeTrack - Coding Practice Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 780);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null); // Center on screen

        rootCardLayout = new CardLayout();
        rootPanel = new JPanel(rootCardLayout);

        // Add Login Panel
        LoginScreen loginScreen = new LoginScreen(this, userService);
        rootPanel.add(loginScreen, "LOGIN");

        // Add Registration Panel
        RegistrationScreen registrationScreen = new RegistrationScreen(this, userService);
        rootPanel.add(registrationScreen, "REGISTER");

        // Set up app container
        appPanel = new JPanel(new BorderLayout());
        rootPanel.add(appPanel, "APP");

        add(rootPanel);
        showScreen("LOGIN");
    }

    public void loginSuccess(User user) {
        this.currentUser = user;
        buildAppWorkspace();
        showScreen("APP");
        navigateTo("DASHBOARD");
    }

    public void showScreen(String name) {
        rootCardLayout.show(rootPanel, name);
    }

    private void buildAppWorkspace() {
        appPanel.removeAll();
        appPanel.setBackground(Color.decode("#F1F0FB")); // Light lavender page background
        appPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel outer = new JPanel(new BorderLayout(0, 14));
        outer.setOpaque(false);

        // ===================================================
        // 1. TOP HEADER — floating white rounded card
        // ===================================================
        JPanel headerPanel = new RoundedPanel(Color.WHITE, 16);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 56));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(3, 18, 3, 18));

        // Left logo and branding block
        JPanel brandingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        brandingPanel.setOpaque(false);
        
        JLabel lblLogoIcon = new JLabel("</>");
        lblLogoIcon.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblLogoIcon.setForeground(Color.decode("#4F46E5"));
        
        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        titleTextPanel.setOpaque(false);
        JLabel lblHeaderTitle = new JLabel("CodeTrack");
        lblHeaderTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblHeaderTitle.setForeground(Color.decode("#1E1B4B"));
        
        JLabel lblHeaderSubtitle = new JLabel("Coding Practice Tracker");
        lblHeaderSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblHeaderSubtitle.setForeground(Color.decode("#8B87C4"));
        
        titleTextPanel.add(lblHeaderTitle);
        titleTextPanel.add(lblHeaderSubtitle);
        
        brandingPanel.add(lblLogoIcon);
        brandingPanel.add(titleTextPanel);

        // Right user profile area matching mockup
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        userPanel.setOpaque(false);
        
        // Circular Avatar panel
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#EEF2FF"));
                g2.fillOval(0, 0, 28, 28);
                g2.setColor(Color.decode("#4F46E5"));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
                g2.drawString("👤", 6, 20);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(28, 28));
        avatarPanel.setMinimumSize(new Dimension(28, 28));
        avatarPanel.setMaximumSize(new Dimension(28, 28));
        avatarPanel.setOpaque(false);
        
        // Name & welcome text panel
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        
        JLabel lblUser = new JLabel(currentUser.getUsername().toLowerCase());
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblUser.setForeground(Color.decode("#1E1B4B"));
        
        JLabel lblWelcomeBack = new JLabel("Welcome back!");
        lblWelcomeBack.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblWelcomeBack.setForeground(Color.decode("#8B87C4"));
        
        namePanel.add(lblUser);
        namePanel.add(lblWelcomeBack);

        userPanel.add(avatarPanel);
        userPanel.add(namePanel);

        headerPanel.add(brandingPanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        outer.add(headerPanel, BorderLayout.NORTH);

        // ===================================================
        // 2. LEFT SIDEBAR NAVIGATION — light violet gradient floating panel
        // ===================================================
        JPanel workspaceRow = new JPanel(new BorderLayout(14, 0));
        workspaceRow.setOpaque(false);

        JPanel sidebarPanel = new GradientPanel(Color.decode("#F5F3FF"), Color.decode("#E4DEFB"), 18);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(140, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(16, 10, 14, 10));

        btnDashboard = createSidebarButton("🏠  Dashboard");
        btnAddProblem = createSidebarButton("➕  Add Problem");
        btnViewProblems = createSidebarButton("📋  View Problems");
        btnReports = createSidebarButton("📊  Reports");
        btnProfile = createSidebarButton("👤  My Profile");
        btnLogout = createSidebarButton("🚪  Logout");

        // Sidebar items spacing
        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarPanel.add(btnAddProblem);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarPanel.add(btnViewProblems);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarPanel.add(btnReports);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarPanel.add(btnProfile);

        sidebarPanel.add(Box.createVerticalGlue()); // Push avatar + logout to bottom

        // Bottom avatar chip — solid indigo fill so it still pops against the light sidebar
        JPanel bottomAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#4F46E5"));
                g2.fillOval(0, 0, 34, 34);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                String initials = currentUser.getUsername().length() >= 2
                        ? currentUser.getUsername().substring(0, 2).toUpperCase()
                        : currentUser.getUsername().toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(initials);
                g2.drawString(initials, (34 - tw) / 2, 22);
                g2.dispose();
            }
        };
        bottomAvatar.setPreferredSize(new Dimension(34, 34));
        bottomAvatar.setMaximumSize(new Dimension(34, 34));
        bottomAvatar.setOpaque(false);
        bottomAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebarPanel.add(bottomAvatar);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnLogout);

        workspaceRow.add(sidebarPanel, BorderLayout.WEST);

        // ===================================================
        // 3. RIGHT CONTENT PANELS (CardLayout)
        // ===================================================
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(Color.WHITE);

        dashboardScreen = new DashboardScreen(this, problemService);
        viewProblemsScreen = new ViewProblemsScreen(this, problemService);
        problemFormScreen = new ProblemFormScreen(this, problemService);
        reportsScreen = new ReportsScreen(this, problemService);
        userProfileScreen = new UserProfileScreen(this, userService);

        contentPanel.add(dashboardScreen, "DASHBOARD");
        contentPanel.add(viewProblemsScreen, "VIEW_PROBLEMS");
        contentPanel.add(problemFormScreen, "PROBLEM_FORM");
        contentPanel.add(reportsScreen, "REPORTS");
        contentPanel.add(userProfileScreen, "PROFILE");

        workspaceRow.add(contentPanel, BorderLayout.CENTER);
        outer.add(workspaceRow, BorderLayout.CENTER);
        appPanel.add(outer, BorderLayout.CENTER);

        // Action Listeners
        btnDashboard.addActionListener(e -> navigateTo("DASHBOARD"));
        btnViewProblems.addActionListener(e -> navigateTo("VIEW_PROBLEMS"));
        btnAddProblem.addActionListener(e -> {
            problemFormScreen.setEditMode(null);
            navigateTo("PROBLEM_FORM");
        });
        btnReports.addActionListener(e -> navigateTo("REPORTS"));
        btnProfile.addActionListener(e -> navigateTo("PROFILE"));
        btnLogout.addActionListener(e -> handleLogout());

        appPanel.revalidate();
        appPanel.repaint();
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(118, 34));
        button.setPreferredSize(new Dimension(118, 34));
        button.setMinimumSize(new Dimension(118, 34));
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(Color.decode("#4C1D95")); // Deep indigo — inactive text, readable on light sidebar
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(0, 10, 0, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("active", Boolean.FALSE);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean active = Boolean.TRUE.equals(b.getClientProperty("active"));
                if (active) {
                    g2.setColor(Color.decode("#4F46E5")); // Bright indigo pill — active item
                    g2.fillRoundRect(2, 0, b.getWidth() - 4, b.getHeight(), 10, 10);
                } else if (b.getModel().isRollover()) {
                    g2.setColor(new Color(79, 70, 229, 28)); // Subtle indigo overlay on hover
                    g2.fillRoundRect(2, 0, b.getWidth() - 4, b.getHeight(), 10, 10);
                }
                g2.dispose();
                super.paint(g, c);
            }
        });
        return button;
    }

    public void navigateTo(String cardName) {
        contentCardLayout.show(contentPanel, cardName);
        resetSidebarButtonStyles();
        
        switch (cardName) {
            case "DASHBOARD":
                highlightSidebarButton(btnDashboard);
                dashboardScreen.refreshData();
                break;
            case "VIEW_PROBLEMS":
                highlightSidebarButton(btnViewProblems);
                viewProblemsScreen.refreshData();
                break;
            case "PROBLEM_FORM":
                highlightSidebarButton(btnAddProblem);
                problemFormScreen.loadTopics();
                break;
            case "REPORTS":
                highlightSidebarButton(btnReports);
                reportsScreen.refreshData();
                break;
            case "PROFILE":
                highlightSidebarButton(btnProfile);
                userProfileScreen.loadProfile();
                break;
        }
    }

    public void navigateToEditProblem(CodingProblem problem) {
        problemFormScreen.setEditMode(problem);
        contentCardLayout.show(contentPanel, "PROBLEM_FORM");
        resetSidebarButtonStyles();
        highlightSidebarButton(btnViewProblems);
    }

    private void resetSidebarButtonStyles() {
        Color fg = Color.decode("#4C1D95"); // Deep indigo — inactive text

        for (JButton b : new JButton[]{btnDashboard, btnViewProblems, btnAddProblem, btnReports, btnProfile}) {
            b.putClientProperty("active", Boolean.FALSE);
            b.setForeground(fg);
            b.repaint();
        }

        btnLogout.putClientProperty("active", Boolean.FALSE);
        btnLogout.setForeground(Color.decode("#DC2626")); // Red-600 — visible on light bg
        btnLogout.repaint();
    }

    private void highlightSidebarButton(JButton button) {
        button.putClientProperty("active", Boolean.TRUE);
        button.setForeground(Color.WHITE);
        button.repaint();
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to log out?", "Logout Confirmation", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            currentUser = null;
            showScreen("LOGIN");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * A flat-colored panel with rounded corners — used for the floating
     * white header card.
     */
    private static class RoundedPanel extends JPanel {
        private final Color fillColor;
        private final int radius;

        RoundedPanel(Color fillColor, int radius) {
            this.fillColor = fillColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A rounded panel painted with a top-to-bottom gradient — used for the
     * dark violet sidebar, echoing the reference design's gradient nav card.
     */
    private static class GradientPanel extends JPanel {
        private final Color topColor;
        private final Color bottomColor;
        private final int radius;

        GradientPanel(Color topColor, Color bottomColor, int radius) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
