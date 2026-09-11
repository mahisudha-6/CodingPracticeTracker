package ui;

import model.CodingProblem;
import service.ProblemService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Compact Dashboard Screen matching the dense reference mockup in colors, layouts, and cards.
 */
public class DashboardScreen extends JPanel {
    private final MainFrame mainFrame;
    private final ProblemService problemService;

    // Top Summary Row card labels
    private JLabel lblTotalCount;
    private JLabel lblTotalSub;
    private JLabel lblSolvedCount;
    private JLabel lblSolvedSub;
    private JLabel lblPendingCount;
    private JLabel lblPendingSub;
    private JLabel lblReviseCount;
    private JLabel lblReviseSub;

    // Middle JTable and empty state
    private JTable tblRecent;
    private DefaultTableModel tableModel;
    private JPanel tableContainerPanel;
    private CardLayout tableCardLayout;

    // Right Progress widgets
    private JLabel lblGoalStatus;
    private JProgressBar barGoal;
    private JLabel lblOverallStatus;
    private JProgressBar barOverall;
    private JLabel lblLastPracticeVal;
    private JLabel lblStreakVal;

    // Bottom Row difficulty card labels
    private JLabel lblEasyCount;
    private JLabel lblMediumCount;
    private JLabel lblHardCount;

    public DashboardScreen(MainFrame mainFrame, ProblemService problemService) {
        this.mainFrame = mainFrame;
        this.problemService = problemService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC")); // Mockup background light gray
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // ===================================================
        // TOP GREETING & DATE CARD PANEL
        // ===================================================
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        titleContainer.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel textGreetings = new JPanel(new GridLayout(2, 1, 0, 1));
        textGreetings.setOpaque(false);
        JLabel lblScreenTitle = new JLabel("Practice Dashboard \uD83D\uDC4B");
        lblScreenTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblScreenTitle.setForeground(Color.decode("#0F172A"));

        JLabel lblScreenSub = new JLabel("Track your progress and stay consistent!");
        lblScreenSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblScreenSub.setForeground(Color.decode("#64748B"));

        textGreetings.add(lblScreenTitle);
        textGreetings.add(lblScreenSub);

        // Date Card Widget
        JPanel dateCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        dateCard.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
        dateCard.setOpaque(false);

        // Dynamic system date string formatting e.g. "Saturday, 26 July 2026"
        LocalDate today = LocalDate.now();
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);
        int dayOfMonth = today.getDayOfMonth();
        String month = today.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
        int year = today.getYear();
        String formattedDate = String.format("\uD83D\uDCC5 %s, %d %s %d", dayOfWeek, dayOfMonth, month, year);

        JLabel lblDate = new JLabel(formattedDate);
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblDate.setForeground(Color.decode("#475569"));
        dateCard.add(lblDate);

        titleContainer.add(textGreetings, BorderLayout.WEST);
        titleContainer.add(dateCard, BorderLayout.EAST);
        add(titleContainer, BorderLayout.NORTH);

        // Center Panel container — GridBagLayout so the middle section can
        // stretch to absorb any leftover vertical space (no dead gray gap
        // at the bottom of the window).
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.weightx = 1.0;

        // ===================================================
        // 1. TOP SECTION: FOUR ROUNDED STATS CARDS
        // ===================================================
        JPanel cardsContainer = new JPanel(new GridLayout(1, 4, 12, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        cardsContainer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 78));

        // Soft colors accents
        JPanel cardTotal = createStatCard("Total Problems", "\uD83D\uDCC4", "0", "All time problems",
                Color.decode("#EFF6FF"), Color.decode("#4F46E5"), Color.decode("#4F46E5")); // Blue
        lblTotalCount = (JLabel) cardTotal.getClientProperty("countLabel");
        lblTotalSub = (JLabel) cardTotal.getClientProperty("subLabel");

        JPanel cardSolved = createStatCard("Solved Problems", "\u2705", "0", "Keep it up! \uD83C\uDF89",
                Color.decode("#F0FDF4"), Color.decode("#16A34A"), Color.decode("#16A34A")); // Green
        lblSolvedCount = (JLabel) cardSolved.getClientProperty("countLabel");
        lblSolvedSub = (JLabel) cardSolved.getClientProperty("subLabel");

        JPanel cardPending = createStatCard("Pending Problems", "\u23F3", "0", "Keep practicing!",
                Color.decode("#FFF7ED"), Color.decode("#EA580C"), Color.decode("#EA580C")); // Orange
        lblPendingCount = (JLabel) cardPending.getClientProperty("countLabel");
        lblPendingSub = (JLabel) cardPending.getClientProperty("subLabel");

        JPanel cardRevise = createStatCard("Problems to Revise", "\uD83D\uDD04", "0", "Click to view →",
                Color.decode("#FEF2F2"), Color.decode("#DC2626"), Color.decode("#DC2626")); // Red
        lblReviseCount = (JLabel) cardRevise.getClientProperty("countLabel");
        lblReviseSub = (JLabel) cardRevise.getClientProperty("subLabel");
        cardRevise.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cardRevise.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showRevisionWatchlistDialog();
            }
        });

        cardsContainer.add(cardTotal);
        cardsContainer.add(cardSolved);
        cardsContainer.add(cardPending);
        cardsContainer.add(cardRevise);

        centerGbc.gridy = 0;
        centerGbc.weighty = 0.0;
        centerPanel.add(cardsContainer, centerGbc);

        // ===================================================
        // 2. MAIN SECTION: RECENT PRACTICE & TARGETS CARD
        // ===================================================
        JPanel mainContentContainer = new JPanel(new BorderLayout(14, 0));
        mainContentContainer.setOpaque(false);
        mainContentContainer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 230));
        mainContentContainer.setMinimumSize(new Dimension(200, 200));

        // --- Left Panel: Recent submissions inside rounded white card ---
        JPanel recentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        recentPanel.setLayout(new BorderLayout());
        recentPanel.setOpaque(false);
        recentPanel.setBorder(new EmptyBorder(10, 14, 8, 14));

        // Header Panel (Icon + Title and link View All button)
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.setBorder(new EmptyBorder(0, 0, 7, 0));

        JPanel recentTitleLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        recentTitleLeft.setOpaque(false);
        JLabel lblRecentIcon = new JLabel("\uD83C\uDFE0");
        lblRecentIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRecentIcon.setForeground(Color.decode("#4F46E5"));
        JLabel lblRecentTitle = new JLabel("Recent Practice Submissions");
        lblRecentTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRecentTitle.setForeground(Color.decode("#4F46E5"));
        recentTitleLeft.add(lblRecentIcon);
        recentTitleLeft.add(lblRecentTitle);

        JButton btnViewAllLink = new JButton("View All");
        btnViewAllLink.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnViewAllLink.setForeground(Color.decode("#4F46E5"));
        btnViewAllLink.setBackground(Color.WHITE);
        btnViewAllLink.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        btnViewAllLink.setFocusPainted(false);
        btnViewAllLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewAllLink.addActionListener(e -> mainFrame.navigateTo("VIEW_PROBLEMS"));

        recentHeader.add(recentTitleLeft, BorderLayout.WEST);
        recentHeader.add(btnViewAllLink, BorderLayout.EAST);
        recentPanel.add(recentHeader, BorderLayout.NORTH);

        // Table Container panel
        tableCardLayout = new CardLayout();
        tableContainerPanel = new JPanel(tableCardLayout);
        tableContainerPanel.setBackground(Color.WHITE);

        String[] columnNames = {"Problem Name", "Platform", "Difficulty", "Topic", "Status", "Date Solved"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRecent = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#F8FAFC"));
                }
                return c;
            }
        };
        tblRecent.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tblRecent.setRowHeight(20);
        tblRecent.setGridColor(Color.decode("#F1F5F9"));
        tblRecent.setShowHorizontalLines(true);
        tblRecent.setShowVerticalLines(false);
        tblRecent.getTableHeader().setPreferredSize(new Dimension(0, 22));

        // Blue header styling
        tblRecent.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#4F46E5")); // Theme Blue
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 10));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#CBD5E1")));
                return label;
            }
        });

        // Add Cell Color Renderers for tags Difficulty & Status matching reference mockup
        tblRecent.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                String val = (String) value;
                if (val != null) {
                    if (val.equalsIgnoreCase("Easy")) {
                        l.setForeground(Color.decode("#16A34A")); // Green
                    } else if (val.equalsIgnoreCase("Medium")) {
                        l.setForeground(Color.decode("#EA580C")); // Orange
                    } else if (val.equalsIgnoreCase("Hard")) {
                        l.setForeground(Color.decode("#DC2626")); // Red
                    }
                }
                return l;
            }
        });

        tblRecent.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                String val = (String) value;
                if (val != null) {
                    if (val.equalsIgnoreCase("Solved")) {
                        l.setForeground(Color.decode("#16A34A"));
                    } else if (val.equalsIgnoreCase("Pending")) {
                        l.setForeground(Color.decode("#EA580C"));
                    } else if (val.equalsIgnoreCase("Revising")) {
                        l.setForeground(Color.decode("#DC2626"));
                    }
                }
                return l;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblRecent);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        tableContainerPanel.add(scrollPane, "TABLE");

        // Empty placeholder card
        JPanel emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setBackground(Color.decode("#F8FAFC"));
        emptyStatePanel.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1));
        JLabel lblEmptyMsg = new JLabel("No Problems Added Yet");
        lblEmptyMsg.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEmptyMsg.setForeground(Color.decode("#94A3B8"));
        emptyStatePanel.add(lblEmptyMsg);
        tableContainerPanel.add(emptyStatePanel, "EMPTY");

        recentPanel.add(tableContainerPanel, BorderLayout.CENTER);

        // Bottom view all problems button row
        JPanel bottomBtnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        bottomBtnRow.setOpaque(false);
        JButton btnViewAllProblems = new JButton("View All Problems");
        btnViewAllProblems.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnViewAllProblems.setForeground(Color.decode("#4F46E5"));
        btnViewAllProblems.setBackground(Color.WHITE);
        btnViewAllProblems.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnViewAllProblems.setFocusPainted(false);
        btnViewAllProblems.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewAllProblems.addActionListener(e -> mainFrame.navigateTo("VIEW_PROBLEMS"));

        JButton btnSuggestProblem = new JButton("\uD83C\uDFB2 Suggest a Problem");
        ButtonStyleUtil.applyStyle(btnSuggestProblem, ButtonStyleUtil.COLOR_BLUE, new Dimension(150, 30));
        btnSuggestProblem.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnSuggestProblem.addActionListener(e -> showSuggestedProblemDialog());

        bottomBtnRow.add(btnViewAllProblems);
        bottomBtnRow.add(btnSuggestProblem);
        recentPanel.add(bottomBtnRow, BorderLayout.SOUTH);

        mainContentContainer.add(recentPanel, BorderLayout.CENTER);

        // --- Right Panel: Target & Consistency card ---
        JPanel targetPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        targetPanel.setLayout(new BoxLayout(targetPanel, BoxLayout.Y_AXIS));
        targetPanel.setOpaque(false);
        targetPanel.setPreferredSize(new Dimension(240, 0));
        targetPanel.setMinimumSize(new Dimension(240, 0));
        targetPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Header
        JPanel targetHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        targetHeader.setOpaque(false);
        JLabel lblTargetIcon = new JLabel("\uD83C\uDFAF");
        lblTargetIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTargetIcon.setForeground(Color.decode("#4F46E5"));
        JLabel lblTargetTitle = new JLabel("Target & Consistency");
        lblTargetTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTargetTitle.setForeground(Color.decode("#4F46E5"));
        targetHeader.add(lblTargetIcon);
        targetHeader.add(lblTargetTitle);
        targetHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        targetPanel.add(targetHeader);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Target Widget 1: Daily Goal
        JPanel panelGoal = createWidgetCard("Daily Goal", "\uD83C\uDFAF", Color.decode("#EFF6FF"), Color.decode("#4F46E5"));
        lblGoalStatus = new JLabel("Daily Goal: Solve 3 Problems");
        lblGoalStatus.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblGoalStatus.setForeground(Color.decode("#64748B"));
        lblGoalStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        barGoal = new JProgressBar(0, 3);
        barGoal.setForeground(Color.decode("#4F46E5"));
        barGoal.setBackground(Color.decode("#E2E8F0"));
        barGoal.setStringPainted(true);
        barGoal.setFont(new Font("SansSerif", Font.BOLD, 9));
        barGoal.setAlignmentX(Component.LEFT_ALIGNMENT);
        barGoal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        barGoal.setPreferredSize(new Dimension(100, 14));
        panelGoal.add(lblGoalStatus);
        panelGoal.add(Box.createRigidArea(new Dimension(0, 4)));
        panelGoal.add(barGoal);

        // Target Widget 2: Overall Progress
        JPanel panelProgress = createWidgetCard("Overall Progress", "\uD83D\uDCC8", Color.decode("#F0FDF4"), Color.decode("#16A34A"));
        lblOverallStatus = new JLabel("Solved 0 of 0 problems");
        lblOverallStatus.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblOverallStatus.setForeground(Color.decode("#64748B"));
        lblOverallStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        barOverall = new JProgressBar(0, 100);
        barOverall.setForeground(Color.decode("#16A34A"));
        barOverall.setBackground(Color.decode("#E2E8F0"));
        barOverall.setStringPainted(true);
        barOverall.setFont(new Font("SansSerif", Font.BOLD, 9));
        barOverall.setAlignmentX(Component.LEFT_ALIGNMENT);
        barOverall.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        barOverall.setPreferredSize(new Dimension(100, 14));
        panelProgress.add(lblOverallStatus);
        panelProgress.add(Box.createRigidArea(new Dimension(0, 4)));
        panelProgress.add(barOverall);

        // Target Widget 3: Last Activity
        JPanel panelLast = createWidgetCard("Last Activity", "\uD83D\uDCC5", Color.decode("#F5F3FF"), Color.decode("#7C3AED"));
        lblLastPracticeVal = new JLabel("Last Practice: -");
        lblLastPracticeVal.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblLastPracticeVal.setForeground(Color.decode("#475569"));
        lblLastPracticeVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLast.add(lblLastPracticeVal);

        // Target Widget 4: Current Streak
        JPanel panelStreak = createWidgetCard("Current Streak", "\uD83D\uDD25", Color.decode("#FFF7ED"), Color.decode("#EA580C"));
        lblStreakVal = new JLabel("Start your streak today!");
        lblStreakVal.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblStreakVal.setForeground(Color.decode("#475569"));
        lblStreakVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelStreak.add(lblStreakVal);

        targetPanel.add(panelGoal);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        targetPanel.add(panelProgress);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        targetPanel.add(panelLast);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 7)));
        targetPanel.add(panelStreak);
        targetPanel.add(Box.createVerticalGlue());

        mainContentContainer.add(targetPanel, BorderLayout.EAST);
        centerGbc.gridy = 1;
        centerGbc.weighty = 1.0;
        centerGbc.insets = new Insets(12, 0, 12, 0);
        centerPanel.add(mainContentContainer, centerGbc);

        // ===================================================
        // 3. BOTTOM SECTION: DIFFICULTY METRICS (THREE CARDS)
        // ===================================================
        JPanel bottomCardsContainer = new JPanel(new GridLayout(1, 3, 14, 0));
        bottomCardsContainer.setOpaque(false);
        bottomCardsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        bottomCardsContainer.setPreferredSize(new Dimension(Integer.MAX_VALUE, 58));

        JPanel cardEasy = createSmallStatCard("Easy Problems", "\u2705", "0", Color.decode("#F0FDF4"), Color.decode("#16A34A"));
        lblEasyCount = (JLabel) cardEasy.getClientProperty("valueLabel");

        JPanel cardMedium = createSmallStatCard("Medium Problems", "\u23F3", "0", Color.decode("#FFF7ED"), Color.decode("#EA580C"));
        lblMediumCount = (JLabel) cardMedium.getClientProperty("valueLabel");

        JPanel cardHard = createSmallStatCard("Hard Problems", "\uD83D\uDD25", "0", Color.decode("#FEF2F2"), Color.decode("#DC2626"));
        lblHardCount = (JLabel) cardHard.getClientProperty("valueLabel");

        bottomCardsContainer.add(cardEasy);
        bottomCardsContainer.add(cardMedium);
        bottomCardsContainer.add(cardHard);

        centerGbc.gridy = 2;
        centerGbc.weighty = 0.0;
        centerGbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(bottomCardsContainer, centerGbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String emoji, String count, String subtext, Color bgColor, Color accentColor, Color textColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setLayout(new GridBagLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        // Left circular icon
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 3;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString(emoji, 9, 21);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(32, 32));
        iconPanel.setOpaque(false);
        card.add(iconPanel, gbc);

        // Right details
        gbc.gridx = 1; gbc.gridheight = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 10, 0, 0);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(textColor);
        card.add(lblTitle, gbc);

        gbc.gridy = 1;
        JLabel lblCount = new JLabel(count);
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 21));
        lblCount.setForeground(Color.decode("#1E293B"));
        card.add(lblCount, gbc);

        gbc.gridy = 2;
        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSub.setForeground(Color.decode("#64748B"));
        card.add(lblSub, gbc);

        card.putClientProperty("countLabel", lblCount);
        card.putClientProperty("subLabel", lblSub);

        return card;
    }

    /**
     * Builds a compact widget card with an icon+title header row.
     * Layout is BoxLayout Y_AXIS so callers can simply .add() extra rows
     * (status text, progress bars) below the header without overlap.
     */
    private JPanel createWidgetCard(String title, String emoji, Color iconBgColor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#FAFAFA"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 10, 8, 10));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header row: icon + title, laid out horizontally
        JPanel headerRow = new JPanel();
        headerRow.setLayout(new BoxLayout(headerRow, BoxLayout.X_AXIS));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBgColor);
                g2.fillOval(0, 0, 22, 22);
                g2.setColor(accentColor);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(emoji, 5, 15);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(22, 22));
        iconPanel.setMaximumSize(new Dimension(22, 22));
        iconPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(Color.decode("#1E293B"));

        headerRow.add(iconPanel);
        headerRow.add(Box.createRigidArea(new Dimension(8, 0)));
        headerRow.add(lblTitle);
        headerRow.add(Box.createHorizontalGlue());

        card.add(headerRow);
        card.add(Box.createRigidArea(new Dimension(0, 5)));

        return card;
    }

    private JPanel createSmallStatCard(String title, String emoji, String countText, Color bgColor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setLayout(new GridBagLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        // Left circular icon
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 3;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillOval(0, 0, 26, 26);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g2.drawString(emoji, 6, 18);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(26, 26));
        iconPanel.setOpaque(false);
        card.add(iconPanel, gbc);

        // Right details
        gbc.gridx = 1; gbc.gridheight = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 10, 0, 0);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(Color.decode("#475569"));
        card.add(lblTitle, gbc);

        gbc.gridy = 1;
        JLabel lblCount = new JLabel(countText);
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblCount.setForeground(Color.decode("#1E293B"));
        card.add(lblCount, gbc);

        gbc.gridy = 2;
        JLabel lblSub = new JLabel("Solved");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSub.setForeground(Color.decode("#64748B"));
        card.add(lblSub, gbc);

        card.putClientProperty("valueLabel", lblCount);
        return card;
    }

    public void refreshData() {
        if (mainFrame.getCurrentUser() == null) return;
        int userId = mainFrame.getCurrentUser().getId();

        try {
            // 1. Dashboard summary counters
            Map<String, Integer> stats = problemService.getDashboardStats(userId);
            int total = stats.getOrDefault("Total", 0);
            int solved = stats.getOrDefault("Solved", 0);
            int pending = stats.getOrDefault("Pending", 0);
            int revising = stats.getOrDefault("Revising", 0);

            lblTotalCount.setText(String.valueOf(total));
            lblSolvedCount.setText(String.valueOf(solved));
            lblPendingCount.setText(String.valueOf(pending));
            lblReviseCount.setText(String.valueOf(revising));

            lblTotalSub.setText("All time problems");
            lblSolvedSub.setText("Keep it up! \uD83C\uDF89");
            lblPendingSub.setText("Keep practicing!");
            lblReviseSub.setText("Click to view →");

            // 2. Daily Goal progress (Target: 3 problems solved today)
            int solvedToday = problemService.getSolvedTodayCount(userId);
            lblGoalStatus.setText("Daily Goal: Solve 3 Problems");
            barGoal.setValue(solvedToday);
            barGoal.setString(solvedToday + " / 3 Completed");

            // 3. Overall Progress
            double pct = total > 0 ? ((double) solved / total) * 100 : 0.0;
            lblOverallStatus.setText(String.format("Solved %d of %d problems", solved, total));
            barOverall.setValue((int) pct);
            barOverall.setString(String.format("%.1f%% Solved", pct));

            // 4. Recent problems table and empty states
            tableModel.setRowCount(0);
            List<CodingProblem> recentProblems = problemService.getRecentProblems(userId, 5);

            if (recentProblems.isEmpty()) {
                tableCardLayout.show(tableContainerPanel, "EMPTY");
                lblLastPracticeVal.setText("Last Practice: -");
            } else {
                tableCardLayout.show(tableContainerPanel, "TABLE");

                // Get most recent solved date for Last practice
                Date lastSolvedDate = null;
                for (CodingProblem cp : recentProblems) {
                    if (cp.getDateSolved() != null) {
                        lastSolvedDate = cp.getDateSolved();
                        break;
                    }
                }

                if (lastSolvedDate != null) {
                    lblLastPracticeVal.setText("Last Practice: " + lastSolvedDate.toString());
                } else {
                    lblLastPracticeVal.setText("Last Practice: -");
                }

                for (CodingProblem cp : recentProblems) {
                    Date dSolved = cp.getDateSolved();
                    String dateStr = (dSolved != null) ? dSolved.toString() : "-";
                    tableModel.addRow(new Object[]{
                        cp.getTitle(),
                        cp.getPlatform(),
                        cp.getDifficulty(),
                        cp.getTopicName(),
                        cp.getStatus(),
                        dateStr
                    });
                }
            }

            // 5. Difficulty Solved stats for bottom row cards
            Map<String, Integer> diffCounts = problemService.getProblemsCountByDifficulty(userId);
            lblEasyCount.setText(String.valueOf(diffCounts.getOrDefault("Easy", 0)));
            lblMediumCount.setText(String.valueOf(diffCounts.getOrDefault("Medium", 0)));
            lblHardCount.setText(String.valueOf(diffCounts.getOrDefault("Hard", 0)));

            // 6. Current practice streak (consecutive days with a solve, ending today/yesterday)
            int streak = problemService.getCurrentStreak(userId);
            if (streak <= 0) {
                lblStreakVal.setText("Start your streak today!");
            } else if (streak == 1) {
                lblStreakVal.setText("1 day — keep it going!");
            } else {
                lblStreakVal.setText(streak + " days in a row! \uD83D\uDD25");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Revision Watchlist popup — lists every problem marked "Revising" and
     * lets the user mark one as Solved directly from here without navigating
     * away. Backed by ProblemService.getRevisionList(), which already existed
     * but wasn't wired up to any screen.
     */
    private void showRevisionWatchlistDialog() {
        if (mainFrame.getCurrentUser() == null) return;
        int userId = mainFrame.getCurrentUser().getId();

        List<CodingProblem> revisionList;
        try {
            revisionList = problemService.getRevisionList(userId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading revision list: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Revision Watchlist", true);
        dialog.setSize(620, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("\uD83D\uDD04 Revision Watchlist");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.decode("#DC2626"));
        lblTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        contentPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Problem Name", "Platform", "Difficulty", "Topic"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (CodingProblem cp : revisionList) {
            model.addRow(new Object[]{cp.getTitle(), cp.getPlatform(), cp.getDifficulty(), cp.getTopicName()});
        }

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#DC2626"));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 11));
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
        });
        table.setSelectionBackground(Color.decode("#FEF2F2"));
        table.setSelectionForeground(Color.decode("#1E293B"));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        if (revisionList.isEmpty()) {
            JLabel lblEmpty = new JLabel("Nothing to revise right now — nice work!", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblEmpty.setForeground(Color.decode("#64748B"));
            contentPanel.remove(scrollPane);
            contentPanel.add(lblEmpty, BorderLayout.CENTER);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnMarkSolved = new JButton("Mark as Solved");
        ButtonStyleUtil.applyBlueStyle(btnMarkSolved);
        btnMarkSolved.setEnabled(!revisionList.isEmpty());
        btnMarkSolved.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a problem first.",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CodingProblem selected = revisionList.get(row);
            selected.setStatus("Solved");
            selected.setDateSolved(new Date(System.currentTimeMillis()));
            try {
                problemService.updateProblem(selected);
                JOptionPane.showMessageDialog(dialog, "Marked \"" + selected.getTitle() + "\" as Solved!",
                        "Updated", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to update: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnClose = new JButton("Close");
        ButtonStyleUtil.applyGrayStyle(btnClose);
        btnClose.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnMarkSolved);
        buttonPanel.add(btnClose);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * "Suggest a Problem" — picks a random Pending problem so the user
     * doesn't have to scroll through their list deciding what to solve next.
     * Reuses searchAndFilterProblems() (already used elsewhere) and filters
     * to Pending in Java, so no new DAO/service method was needed.
     */
    private void showSuggestedProblemDialog() {
        if (mainFrame.getCurrentUser() == null) return;
        int userId = mainFrame.getCurrentUser().getId();

        List<CodingProblem> pending;
        try {
            List<CodingProblem> all = problemService.searchAndFilterProblems(userId, null, null, null);
            pending = new java.util.ArrayList<>();
            for (CodingProblem cp : all) {
                if ("Pending".equalsIgnoreCase(cp.getStatus())) {
                    pending.add(cp);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading problems: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Suggest a Problem", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(24, 24, 16, 24));

        JLabel lblIcon = new JLabel("\uD83C\uDFB2", SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 32));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblHeading = new JLabel("Try this next:", SwingConstants.CENTER);
        lblHeading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHeading.setForeground(Color.decode("#64748B"));
        lblHeading.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHeading.setBorder(new EmptyBorder(10, 0, 6, 0));

        JLabel lblProblemTitle = new JLabel("", SwingConstants.CENTER);
        lblProblemTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblProblemTitle.setForeground(Color.decode("#1E293B"));
        lblProblemTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblProblemTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblProblemMeta = new JLabel("", SwingConstants.CENTER);
        lblProblemMeta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblProblemMeta.setForeground(Color.decode("#64748B"));
        lblProblemMeta.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblProblemMeta.setBorder(new EmptyBorder(6, 0, 0, 0));

        contentPanel.add(lblIcon);
        contentPanel.add(lblHeading);
        contentPanel.add(lblProblemTitle);
        contentPanel.add(lblProblemMeta);

        JButton btnMarkSolved = new JButton("Mark as Solved");
        ButtonStyleUtil.applyBlueStyle(btnMarkSolved);
        JButton btnPickAnother = new JButton("Pick Another");
        ButtonStyleUtil.applyGrayStyle(btnPickAnother);
        JButton btnClose = new JButton("Close");
        ButtonStyleUtil.applyGrayStyle(btnClose);
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        if (pending.isEmpty()) {
            lblHeading.setText("");
            lblProblemTitle.setText("<html><center>No pending problems right now!</center></html>");
            lblProblemMeta.setText("Add one, or tackle something from your Revision list.");
            buttonPanel.add(btnClose);
        } else {
            final CodingProblem[] current = {pending.get(new java.util.Random().nextInt(pending.size()))};
            Runnable render = () -> {
                CodingProblem cp = current[0];
                lblProblemTitle.setText(cp.getTitle());
                lblProblemMeta.setText(cp.getPlatform() + "  •  " + cp.getDifficulty() + "  •  " + cp.getTopicName());
            };
            render.run();

            btnPickAnother.addActionListener(e -> {
                if (pending.size() > 1) {
                    CodingProblem next;
                    do {
                        next = pending.get(new java.util.Random().nextInt(pending.size()));
                    } while (next == current[0]);
                    current[0] = next;
                    render.run();
                }
            });

            btnMarkSolved.addActionListener(e -> {
                CodingProblem cp = current[0];
                cp.setStatus("Solved");
                cp.setDateSolved(new Date(System.currentTimeMillis()));
                try {
                    problemService.updateProblem(cp);
                    JOptionPane.showMessageDialog(dialog, "Nice! Marked \"" + cp.getTitle() + "\" as Solved.",
                            "Updated", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Failed to update: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            buttonPanel.add(btnMarkSolved);
            if (pending.size() > 1) {
                buttonPanel.add(btnPickAnother);
            }
            buttonPanel.add(btnClose);
        }

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
