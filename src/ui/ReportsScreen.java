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
 * Practice Reports Screen — matches the CodeTrack reference layout:
 * four summary cards, a "Your Progress" difficulty breakdown, a two-column
 * bottom section (Platform Summary + Recent Activity | Goal & Achievement),
 * all backed by live ProblemService data.
 */
public class ReportsScreen extends JPanel {
    private final MainFrame mainFrame;
    private final ProblemService problemService;

    // Top summary cards
    private JLabel lblTotalSolvedValue;
    private JLabel lblEasyMediumValue;
    private JLabel lblHardValue;
    private JLabel lblLastPracticeValue;

    // Your Progress rows
    private JLabel lblEasyProgressText;
    private RoundedProgressBar barEasyProgress;
    private JLabel lblEasyPercent;
    private JLabel lblMediumProgressText;
    private RoundedProgressBar barMediumProgress;
    private JLabel lblMediumPercent;
    private JLabel lblHardProgressText;
    private RoundedProgressBar barHardProgress;
    private JLabel lblHardPercent;

    // Platform Summary (built dynamically per actual platform data)
    private JPanel platformValuesRow;

    // Recent Activity table
    private DefaultTableModel tableModel;

    // Goal & Achievement
    private JLabel lblTodaysProgressValue;
    private JProgressBar barDailyGoal;
    private JLabel lblWeekSolvedValue;

    public ReportsScreen(MainFrame mainFrame, ProblemService problemService) {
        this.mainFrame = mainFrame;
        this.problemService = problemService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFC"));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // ===================================================
        // TITLE + DATE CARD ROW
        // ===================================================
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);

        JLabel lblScreenTitle = new JLabel("Practice Reports");
        lblScreenTitle.setFont(new Font("SansSerif", Font.BOLD, 21));
        lblScreenTitle.setForeground(Color.decode("#0F172A"));
        lblScreenTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblScreenSub = new JLabel("A quick look at your practice journey.");
        lblScreenSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblScreenSub.setForeground(Color.decode("#64748B"));
        lblScreenSub.setBorder(new EmptyBorder(2, 0, 6, 0));
        lblScreenSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Small blue underline accent
        JPanel underline = new JPanel();
        underline.setBackground(Color.decode("#4F46E5"));
        underline.setPreferredSize(new Dimension(40, 3));
        underline.setMaximumSize(new Dimension(40, 3));
        underline.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleTextPanel.add(lblScreenTitle);
        titleTextPanel.add(lblScreenSub);
        titleTextPanel.add(underline);

        // Date card (top-right), same pattern as Dashboard
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

        JPanel dateCardWrap = new JPanel(new GridBagLayout());
        dateCardWrap.setOpaque(false);
        dateCardWrap.add(dateCard);

        titleRow.add(titleTextPanel, BorderLayout.WEST);
        titleRow.add(dateCardWrap, BorderLayout.EAST);
        add(titleRow, BorderLayout.NORTH);

        // ===================================================
        // CENTER: everything scrolls if needed, stacked vertically
        // ===================================================
        JPanel centerPanel = new ScrollableContentPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // --- 1. FOUR SUMMARY CARDS ---
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        cardsRow.setOpaque(false);
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        cardsRow.setPreferredSize(new Dimension(Integer.MAX_VALUE, 78));

        JPanel cardTotalSolved = createSummaryCard("\uD83D\uDCC4", Color.decode("#EFF6FF"), Color.decode("#4F46E5"),
                "0", "Total Solved", "All time", Color.decode("#4F46E5"));
        lblTotalSolvedValue = (JLabel) cardTotalSolved.getClientProperty("valueLabel");

        JPanel cardEasyMedium = createSummaryCard("\u2705", Color.decode("#F0FDF4"), Color.decode("#16A34A"),
                "0", "Easy + Medium", "Solved", Color.decode("#16A34A"));
        lblEasyMediumValue = (JLabel) cardEasyMedium.getClientProperty("valueLabel");

        JPanel cardHard = createSummaryCard("\uD83D\uDCC8", Color.decode("#FFF7ED"), Color.decode("#EA580C"),
                "0", "Hard Problems", "Solved", Color.decode("#EA580C"));
        lblHardValue = (JLabel) cardHard.getClientProperty("valueLabel");

        JPanel cardLastPractice = createSummaryCard("\uD83D\uDD52", Color.decode("#F5F3FF"), Color.decode("#7C3AED"),
                "-", "Last Practice", "Keep it consistent!", Color.decode("#7C3AED"));
        lblLastPracticeValue = (JLabel) cardLastPractice.getClientProperty("valueLabel");

        cardsRow.add(cardTotalSolved);
        cardsRow.add(cardEasyMedium);
        cardsRow.add(cardHard);
        cardsRow.add(cardLastPractice);

        centerPanel.add(cardsRow);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // --- 2. YOUR PROGRESS CARD ---
        JPanel progressCard = createRoundedCard(Color.WHITE);
        progressCard.setLayout(new BoxLayout(progressCard, BoxLayout.Y_AXIS));
        progressCard.setBorder(new EmptyBorder(14, 18, 14, 18));
        progressCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));

        JPanel progressHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        progressHeader.setOpaque(false);
        progressHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblProgressIcon = new JLabel("\uD83D\uDCC8");
        lblProgressIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblProgressIcon.setForeground(Color.decode("#4F46E5"));
        JLabel lblProgressTitle = new JLabel("Your Progress");
        lblProgressTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblProgressTitle.setForeground(Color.decode("#0F172A"));
        progressHeader.add(lblProgressIcon);
        progressHeader.add(lblProgressTitle);

        progressCard.add(progressHeader);
        progressCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel easyRow = createProgressRow(Color.decode("#16A34A"), "Easy", Color.decode("#16A34A"),
                Color.decode("#F0FDF4"), Color.decode("#16A34A"));
        lblEasyProgressText = (JLabel) easyRow.getClientProperty("progressText");
        barEasyProgress = (RoundedProgressBar) easyRow.getClientProperty("progressBar");
        lblEasyPercent = (JLabel) easyRow.getClientProperty("percentLabel");

        JPanel mediumRow = createProgressRow(Color.decode("#EA580C"), "Medium", Color.decode("#EA580C"),
                Color.decode("#FFF7ED"), Color.decode("#EA580C"));
        lblMediumProgressText = (JLabel) mediumRow.getClientProperty("progressText");
        barMediumProgress = (RoundedProgressBar) mediumRow.getClientProperty("progressBar");
        lblMediumPercent = (JLabel) mediumRow.getClientProperty("percentLabel");

        JPanel hardRow = createProgressRow(Color.decode("#DC2626"), "Hard", Color.decode("#DC2626"),
                Color.decode("#FEF2F2"), Color.decode("#DC2626"));
        lblHardProgressText = (JLabel) hardRow.getClientProperty("progressText");
        barHardProgress = (RoundedProgressBar) hardRow.getClientProperty("progressBar");
        lblHardPercent = (JLabel) hardRow.getClientProperty("percentLabel");

        progressCard.add(easyRow);
        progressCard.add(Box.createRigidArea(new Dimension(0, 8)));
        progressCard.add(mediumRow);
        progressCard.add(Box.createRigidArea(new Dimension(0, 8)));
        progressCard.add(hardRow);

        centerPanel.add(progressCard);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // --- 3. BOTTOM SECTION: TWO COLUMNS ---
        JPanel bottomRow = new JPanel(new BorderLayout(14, 0));
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        bottomRow.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));

        // LEFT COLUMN: Platform Summary (top) + Recent Activity (bottom)
        JPanel leftColumn = new JPanel(new BorderLayout(0, 12));
        leftColumn.setOpaque(false);

        // Platform Summary card
        JPanel platformCard = createRoundedCard(Color.WHITE);
        platformCard.setLayout(new BorderLayout());
        platformCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel platformHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        platformHeader.setOpaque(false);
        JLabel lblPlatformIcon = new JLabel("\uD83D\uDCBB");
        lblPlatformIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPlatformIcon.setForeground(Color.decode("#4F46E5"));
        JLabel lblPlatformTitle = new JLabel("Platform Summary");
        lblPlatformTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPlatformTitle.setForeground(Color.decode("#0F172A"));
        platformHeader.add(lblPlatformIcon);
        platformHeader.add(lblPlatformTitle);

        // Rebuilt dynamically in refreshData() — one box per platform actually
        // present in the data (not hardcoded), each clickable to view its problems.
        // GridLayout(0, 3, ...) auto-adds rows as needed and — unlike FlowLayout —
        // correctly reports its preferred height for wrapped rows, so a 4th+
        // platform no longer gets silently clipped off the bottom of the card.
        platformValuesRow = new JPanel(new GridLayout(0, 3, 12, 8));
        platformValuesRow.setOpaque(false);
        platformValuesRow.setBorder(new EmptyBorder(6, 0, 0, 0));

        platformCard.add(platformHeader, BorderLayout.NORTH);
        platformCard.add(platformValuesRow, BorderLayout.CENTER);

        // Recent Activity card
        JPanel activityCard = createRoundedCard(Color.WHITE);
        activityCard.setLayout(new BorderLayout());
        activityCard.setBorder(new EmptyBorder(12, 16, 10, 16));

        JPanel activityHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        activityHeader.setOpaque(false);
        JLabel lblActivityIcon = new JLabel("\uD83D\uDD52");
        lblActivityIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblActivityIcon.setForeground(Color.decode("#4F46E5"));
        JLabel lblActivityTitle = new JLabel("All Activity");
        lblActivityTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblActivityTitle.setForeground(Color.decode("#0F172A"));
        activityHeader.add(lblActivityIcon);
        activityHeader.add(lblActivityTitle);
        activityHeader.setBorder(new EmptyBorder(0, 0, 6, 0));

        String[] columnNames = {"Problem Name", "Platform", "Difficulty", "Topic", "Status", "Date Solved"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tblActivity = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#F8FAFC"));
                }
                return c;
            }
        };
        tblActivity.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tblActivity.setRowHeight(20);
        tblActivity.setGridColor(Color.decode("#F1F5F9"));
        tblActivity.setShowHorizontalLines(true);
        tblActivity.setShowVerticalLines(false);
        tblActivity.getTableHeader().setPreferredSize(new Dimension(0, 22));
        tblActivity.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.WHITE);
                label.setForeground(Color.decode("#4F46E5"));
                label.setFont(new Font("SansSerif", Font.BOLD, 10));
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")),
                        BorderFactory.createEmptyBorder(0, 4, 0, 0)
                ));
                return label;
            }
        });
        tblActivity.getColumnModel().getColumn(2).setCellRenderer(difficultyOrStatusRenderer(true));
        tblActivity.getColumnModel().getColumn(4).setCellRenderer(difficultyOrStatusRenderer(false));

        JScrollPane activityScroll = new JScrollPane(tblActivity);
        activityScroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));

        activityCard.add(activityHeader, BorderLayout.NORTH);
        activityCard.add(activityScroll, BorderLayout.CENTER);

        leftColumn.add(platformCard, BorderLayout.NORTH);
        leftColumn.add(activityCard, BorderLayout.CENTER);

        // RIGHT COLUMN: Goal & Achievement
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setOpaque(false);
        rightColumn.setPreferredSize(new Dimension(260, 0));
        rightColumn.setMinimumSize(new Dimension(260, 0));

        JPanel goalAchievementCard = createRoundedCard(Color.WHITE);
        goalAchievementCard.setLayout(new BoxLayout(goalAchievementCard, BoxLayout.Y_AXIS));
        goalAchievementCard.setBorder(new EmptyBorder(14, 16, 14, 16));
        goalAchievementCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel goalAchHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        goalAchHeader.setOpaque(false);
        goalAchHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblGoalAchIcon = new JLabel("\uD83C\uDFC1");
        lblGoalAchIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblGoalAchIcon.setForeground(Color.decode("#4F46E5"));
        JLabel lblGoalAchTitle = new JLabel("Goal & Achievement");
        lblGoalAchTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblGoalAchTitle.setForeground(Color.decode("#0F172A"));
        goalAchHeader.add(lblGoalAchIcon);
        goalAchHeader.add(lblGoalAchTitle);

        goalAchievementCard.add(goalAchHeader);
        goalAchievementCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Daily Goal sub-block (pastel green)
        JPanel dailyGoalBlock = createRoundedCard(Color.decode("#F0FDF4"));
        dailyGoalBlock.setLayout(new BoxLayout(dailyGoalBlock, BoxLayout.Y_AXIS));
        dailyGoalBlock.setBorder(new EmptyBorder(12, 14, 12, 14));
        dailyGoalBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyGoalBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel dailyGoalTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dailyGoalTitleRow.setOpaque(false);
        JLabel lblDailyGoalIcon = new JLabel("\uD83C\uDFAF");
        lblDailyGoalIcon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDailyGoalIcon.setForeground(Color.decode("#16A34A"));
        JLabel lblDailyGoalTitle = new JLabel("Daily Goal");
        lblDailyGoalTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDailyGoalTitle.setForeground(Color.decode("#1E293B"));
        dailyGoalTitleRow.add(lblDailyGoalIcon);
        dailyGoalTitleRow.add(lblDailyGoalTitle);
        dailyGoalTitleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDailyGoalSub = new JLabel("Solve 3 problems every day");
        lblDailyGoalSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblDailyGoalSub.setForeground(Color.decode("#64748B"));
        lblDailyGoalSub.setBorder(new EmptyBorder(2, 0, 8, 0));
        lblDailyGoalSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTodaysProgressTitle = new JLabel("Today's Progress");
        lblTodaysProgressTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTodaysProgressTitle.setForeground(Color.decode("#475569"));
        lblTodaysProgressTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTodaysProgressValue = new JLabel("0 / 3");
        lblTodaysProgressValue.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTodaysProgressValue.setForeground(Color.decode("#0F172A"));
        lblTodaysProgressValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTodaysProgressValue.setBorder(new EmptyBorder(1, 0, 5, 0));

        barDailyGoal = new JProgressBar(0, 3);
        barDailyGoal.setForeground(Color.decode("#16A34A"));
        barDailyGoal.setBackground(Color.decode("#E2E8F0"));
        barDailyGoal.setStringPainted(true);
        barDailyGoal.setFont(new Font("SansSerif", Font.BOLD, 9));
        barDailyGoal.setAlignmentX(Component.LEFT_ALIGNMENT);
        barDailyGoal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
        barDailyGoal.setPreferredSize(new Dimension(100, 14));

        dailyGoalBlock.add(dailyGoalTitleRow);
        dailyGoalBlock.add(lblDailyGoalSub);
        dailyGoalBlock.add(lblTodaysProgressTitle);
        dailyGoalBlock.add(lblTodaysProgressValue);
        dailyGoalBlock.add(barDailyGoal);

        goalAchievementCard.add(dailyGoalBlock);

        rightColumn.add(goalAchievementCard);
        rightColumn.add(Box.createRigidArea(new Dimension(0, 12)));

        // Row: This Week card + Keep Going motivational card, side by side
        JPanel bottomWidgetsRow = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomWidgetsRow.setOpaque(false);
        bottomWidgetsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomWidgetsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        // This Week card
        JPanel weekCard = createRoundedCard(Color.decode("#EFF6FF"));
        weekCard.setLayout(new BorderLayout(8, 0));
        weekCard.setBorder(new EmptyBorder(12, 12, 12, 8));

        JLabel lblWeekIcon = circleIconLabel("\uD83C\uDFC6", Color.WHITE, Color.decode("#4F46E5"), 28, 12);

        JPanel weekTextPanel = new JPanel();
        weekTextPanel.setLayout(new BoxLayout(weekTextPanel, BoxLayout.Y_AXIS));
        weekTextPanel.setOpaque(false);
        JLabel lblWeekTitle = new JLabel("This Week");
        lblWeekTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblWeekTitle.setForeground(Color.decode("#4F46E5"));
        lblWeekTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblWeekSubtitle = new JLabel("Problems Solved");
        lblWeekSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblWeekSubtitle.setForeground(Color.decode("#64748B"));
        lblWeekSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblWeekSolvedValue = new JLabel("0");
        lblWeekSolvedValue.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblWeekSolvedValue.setForeground(Color.decode("#0F172A"));
        lblWeekSolvedValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        weekTextPanel.add(lblWeekTitle);
        weekTextPanel.add(lblWeekSubtitle);
        weekTextPanel.add(lblWeekSolvedValue);

        weekCard.add(lblWeekIcon, BorderLayout.WEST);
        weekCard.add(weekTextPanel, BorderLayout.CENTER);

        // Keep Going motivational card
        JPanel motivateCard = createRoundedCard(Color.decode("#F5F3FF"));
        motivateCard.setLayout(new BorderLayout(8, 0));
        motivateCard.setBorder(new EmptyBorder(12, 8, 12, 8));

        JLabel lblFireIcon = circleIconLabel("\uD83D\uDD25", Color.WHITE, Color.decode("#7C3AED"), 28, 12);

        JPanel motivateTextPanel = new JPanel();
        motivateTextPanel.setLayout(new BoxLayout(motivateTextPanel, BoxLayout.Y_AXIS));
        motivateTextPanel.setOpaque(false);
        JLabel lblMotivateTitle = new JLabel("Keep Going!");
        lblMotivateTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblMotivateTitle.setForeground(Color.decode("#7C3AED"));
        lblMotivateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblMotivateLine1 = new JLabel("You're doing great.");
        lblMotivateLine1.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblMotivateLine1.setForeground(Color.decode("#475569"));
        lblMotivateLine1.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblMotivateLine2 = new JLabel("<html>Consistency is the<br>key to success.</html>");
        lblMotivateLine2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblMotivateLine2.setForeground(Color.decode("#475569"));
        lblMotivateLine2.setAlignmentX(Component.LEFT_ALIGNMENT);

        motivateTextPanel.add(lblMotivateTitle);
        motivateTextPanel.add(lblMotivateLine1);
        motivateTextPanel.add(lblMotivateLine2);

        motivateCard.add(lblFireIcon, BorderLayout.WEST);
        motivateCard.add(motivateTextPanel, BorderLayout.CENTER);

        bottomWidgetsRow.add(weekCard);
        bottomWidgetsRow.add(motivateCard);

        rightColumn.add(bottomWidgetsRow);
        rightColumn.add(Box.createVerticalGlue());

        bottomRow.add(leftColumn, BorderLayout.CENTER);
        bottomRow.add(rightColumn, BorderLayout.EAST);

        centerPanel.add(bottomRow);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // Footer line
        JLabel lblFooter = new JLabel("<html>\u00A9 2026 CodeTrack | Built with \u2764\uFE0F using Java, JDBC & MySQL</html>");
        lblFooter.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblFooter.setForeground(Color.decode("#94A3B8"));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel footerWrap = new JPanel();
        footerWrap.setOpaque(false);
        footerWrap.setLayout(new BoxLayout(footerWrap, BoxLayout.X_AXIS));
        footerWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        footerWrap.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E2E8F0")));
        footerWrap.add(Box.createHorizontalGlue());
        footerWrap.add(lblFooter);
        footerWrap.add(Box.createHorizontalGlue());

        centerPanel.add(footerWrap);
        centerPanel.add(Box.createVerticalGlue());

        JScrollPane mainScroll = new JScrollPane(centerPanel);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.getVerticalScrollBar().setUnitIncrement(14);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);
    }

    // ===================================================
    // Helper builders
    // ===================================================

    private JPanel createRoundedCard(Color bgColor) {
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
        card.setOpaque(false);
        return card;
    }

    private JPanel createSummaryCard(String emoji, Color iconBg, Color iconColor, String value, String title, String subtitle, Color valueColor) {
        JPanel card = createRoundedCard(Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 3;
        gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel iconLabel = circleIconLabel(emoji, iconBg, iconColor, 34, 15);
        card.add(iconLabel, gbc);

        gbc.gridx = 1; gbc.gridheight = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 10, 0, 0);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblValue.setForeground(Color.decode("#0F172A"));
        card.add(lblValue, gbc);

        gbc.gridy = 1;
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(Color.decode("#475569"));
        card.add(lblTitle, gbc);

        gbc.gridy = 2;
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSub.setForeground(valueColor);
        card.add(lblSub, gbc);

        card.putClientProperty("valueLabel", lblValue);
        return card;
    }

    private JLabel circleIconLabel(String emoji, Color circleColor, Color emojiColor, int size, int fontSize) {
        JLabel label = new JLabel(emoji) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(circleColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setPreferredSize(new Dimension(size, size));
        label.setMinimumSize(new Dimension(size, size));
        label.setMaximumSize(new Dimension(size, size));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        label.setForeground(emojiColor);
        return label;
    }

    /**
     * Builds one "Easy/Medium/Hard Problems" progress row: dot + label, thin
     * progress bar, "solved/total" text, and percentage — all left aligned
     * on a single horizontal line.
     */
    private JPanel createProgressRow(Color dotColor, String label, Color barColor, Color pillBg, Color pillText) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        labelPanel.setOpaque(false);
        labelPanel.setPreferredSize(new Dimension(92, 22));

        // Painted directly instead of relying on a colored-circle emoji glyph —
        // emoji color rendering is unreliable across OS/JDK font setups and can
        // silently fall back to a plain gray dot on some systems.
        JComponent dot = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setMaximumSize(new Dimension(8, 8));
        dot.setOpaque(false);

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblName.setForeground(Color.decode("#1E293B"));
        labelPanel.add(dot);
        labelPanel.add(lblName);

        RoundedProgressBar bar = new RoundedProgressBar(barColor, Color.decode("#EEF2F6"));
        bar.setPreferredSize(new Dimension(100, 10));

        JLabel lblProgressText = new JLabel("0/0");
        lblProgressText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblProgressText.setForeground(Color.decode("#64748B"));
        lblProgressText.setHorizontalAlignment(SwingConstants.RIGHT);
        lblProgressText.setPreferredSize(new Dimension(38, 22));

        PillBadge lblPercent = new PillBadge("0%", pillBg, pillText);
        lblPercent.setPreferredSize(new Dimension(50, 22));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(lblProgressText);
        rightPanel.add(lblPercent);
        rightPanel.setPreferredSize(new Dimension(108, 26));

        row.add(labelPanel, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);

        row.putClientProperty("progressText", lblProgressText);
        row.putClientProperty("progressBar", bar);
        row.putClientProperty("percentLabel", lblPercent);
        return row;
    }

    private JPanel createPlatformBox(String iconText, String platformName, int count, Color bg, Color accent, Runnable onClick) {
        JPanel box = createRoundedCard(bg);
        box.setLayout(new BorderLayout(8, 0));
        box.setBorder(new EmptyBorder(8, 10, 8, 10));
        box.setPreferredSize(new Dimension(150, 64));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
        box.setToolTipText("Click to view problems solved on " + platformName);

        JLabel lblIcon = new JLabel(iconText);
        lblIcon.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblIcon.setForeground(accent);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(28, 28));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblName = new JLabel(platformName);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblName.setForeground(Color.decode("#1E293B"));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(String.valueOf(count));
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblValue.setForeground(accent);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSolved = new JLabel("Solved \u2192");
        lblSolved.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblSolved.setForeground(Color.decode("#64748B"));
        lblSolved.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblName);
        textPanel.add(lblValue);
        textPanel.add(lblSolved);

        box.add(lblIcon, BorderLayout.WEST);
        box.add(textPanel, BorderLayout.CENTER);

        box.putClientProperty("valueLabel", lblValue);

        box.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onClick.run();
            }
        });

        return box;
    }

    private DefaultTableCellRenderer difficultyOrStatusRenderer(boolean isDifficultyColumn) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                String val = (String) value;
                if (val != null) {
                    if (isDifficultyColumn) {
                        if (val.equalsIgnoreCase("Easy")) l.setForeground(Color.decode("#16A34A"));
                        else if (val.equalsIgnoreCase("Medium")) l.setForeground(Color.decode("#EA580C"));
                        else if (val.equalsIgnoreCase("Hard")) l.setForeground(Color.decode("#DC2626"));
                    } else {
                        if (val.equalsIgnoreCase("Solved")) l.setForeground(Color.decode("#16A34A"));
                        else if (val.equalsIgnoreCase("Pending")) l.setForeground(Color.decode("#EA580C"));
                        else if (val.equalsIgnoreCase("Revising")) l.setForeground(Color.decode("#DC2626"));
                    }
                }
                return l;
            }
        };
    }

    /**
     * Refreshes all report values dynamically from the database.
     */
    public void refreshData() {
        if (mainFrame.getCurrentUser() == null) return;
        int userId = mainFrame.getCurrentUser().getId();

        try {
            // 1. Top summary cards
            Map<String, Integer> stats = problemService.getDashboardStats(userId);
            int solved = stats.getOrDefault("Solved", 0);
            lblTotalSolvedValue.setText(String.valueOf(solved));

            Map<String, Integer> totalByDiff = problemService.getProblemsCountByDifficulty(userId);
            Map<String, Integer> solvedByDiff = problemService.getSolvedCountByDifficulty(userId);

            int easySolved = solvedByDiff.getOrDefault("Easy", 0);
            int mediumSolved = solvedByDiff.getOrDefault("Medium", 0);
            int hardSolved = solvedByDiff.getOrDefault("Hard", 0);
            int easyTotal = totalByDiff.getOrDefault("Easy", 0);
            int mediumTotal = totalByDiff.getOrDefault("Medium", 0);
            int hardTotal = totalByDiff.getOrDefault("Hard", 0);

            lblEasyMediumValue.setText(String.valueOf(easySolved + mediumSolved));
            lblHardValue.setText(String.valueOf(hardSolved));

            // 2. Last Practice (most recent solved date) + full problem list for the table below
            List<CodingProblem> allProblems = problemService.searchAndFilterProblems(userId, null, null, null);
            Date lastSolvedDate = null;
            for (CodingProblem cp : allProblems) {
                if (cp.getDateSolved() != null) {
                    lastSolvedDate = cp.getDateSolved();
                    break;
                }
            }
            lblLastPracticeValue.setText(lastSolvedDate != null ? lastSolvedDate.toString() : "-");

            // 3. Your Progress rows
            updateProgressRow(lblEasyProgressText, barEasyProgress, lblEasyPercent, easySolved, easyTotal);
            updateProgressRow(lblMediumProgressText, barMediumProgress, lblMediumPercent, mediumSolved, mediumTotal);
            updateProgressRow(lblHardProgressText, barHardProgress, lblHardPercent, hardSolved, hardTotal);

            // 4. Platform Summary — one box per platform actually present in the
            // data (fixes platforms like GeeksforGeeks/CodeChef never showing),
            // each clickable to open a popup listing its solved problems.
            Map<String, Integer> solvedByPlatform = problemService.getSolvedCountByPlatform(userId);
            platformValuesRow.removeAll();

            if (solvedByPlatform.isEmpty()) {
                JLabel lblNoPlatforms = new JLabel("No solved problems yet.");
                lblNoPlatforms.setFont(new Font("SansSerif", Font.PLAIN, 11));
                lblNoPlatforms.setForeground(Color.decode("#94A3B8"));
                platformValuesRow.add(lblNoPlatforms);
            } else {
                Color[] palette = {
                    Color.decode("#4F46E5"), Color.decode("#16A34A"), Color.decode("#EA580C"),
                    Color.decode("#7C3AED"), Color.decode("#DC2626"), Color.decode("#0891B2")
                };
                Color[] paletteBg = {
                    Color.decode("#EFF6FF"), Color.decode("#F0FDF4"), Color.decode("#FFF7ED"),
                    Color.decode("#F5F3FF"), Color.decode("#FEF2F2"), Color.decode("#ECFEFF")
                };
                int i = 0;
                for (Map.Entry<String, Integer> entry : solvedByPlatform.entrySet()) {
                    String platform = entry.getKey();
                    int count = entry.getValue();
                    Color accent = palette[i % palette.length];
                    Color bg = paletteBg[i % paletteBg.length];
                    JPanel box = createPlatformBox("</>", platform, count, bg, accent,
                            () -> showPlatformProblemsDialog(userId, platform));
                    platformValuesRow.add(box);
                    i++;
                }
            }
            platformValuesRow.revalidate();
            platformValuesRow.repaint();

            // 5. All Activity table (every problem, most recent first)
            tableModel.setRowCount(0);
            for (CodingProblem cp : allProblems) {
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

            // 6. Goal & Achievement — Daily Goal (target: 3 problems today)
            int solvedToday = problemService.getSolvedTodayCount(userId);
            lblTodaysProgressValue.setText(solvedToday + " / 3");
            barDailyGoal.setValue(Math.min(solvedToday, 3));
            int dailyPct = Math.min(100, (int) (((double) solvedToday / 3) * 100));
            barDailyGoal.setString(dailyPct + "%");

            // 7. This Week solved count
            int solvedThisWeek = problemService.getSolvedThisWeekCount(userId);
            lblWeekSolvedValue.setText(String.valueOf(solvedThisWeek));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading report: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Opens a modal popup listing every solved problem for the clicked platform
     * (Platform Summary click-through), styled consistently with the rest of
     * the app's dialogs.
     */
    private void showPlatformProblemsDialog(int userId, String platform) {
        JDialog dialog = new JDialog(mainFrame, platform + " \u2014 Solved Problems", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lblTitle = new JLabel(platform + " \u2014 Solved Problems");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(Color.decode("#0F172A"));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        contentPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = {"Problem Name", "Difficulty", "Topic", "Date Solved"};
        DefaultTableModel dialogModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable dialogTable = new JTable(dialogModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#F8FAFC"));
                }
                return c;
            }
        };
        dialogTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dialogTable.setRowHeight(24);
        dialogTable.setGridColor(Color.decode("#F1F5F9"));
        dialogTable.setShowVerticalLines(false);
        dialogTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.WHITE);
                label.setForeground(Color.decode("#4F46E5"));
                label.setFont(new Font("SansSerif", Font.BOLD, 11));
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));
                return label;
            }
        });
        dialogTable.getColumnModel().getColumn(1).setCellRenderer(difficultyOrStatusRenderer(true));

        try {
            List<CodingProblem> problems = problemService.getSolvedProblemsByPlatform(userId, platform);
            for (CodingProblem cp : problems) {
                Date dSolved = cp.getDateSolved();
                String dateStr = (dSolved != null) ? dSolved.toString() : "-";
                dialogModel.addRow(new Object[]{cp.getTitle(), cp.getDifficulty(), cp.getTopicName(), dateStr});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog,
                    "Error loading problems: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        JScrollPane dialogScroll = new JScrollPane(dialogTable);
        dialogScroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        contentPanel.add(dialogScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        ButtonStyleUtil.applyBlueStyle(btnClose);
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateProgressRow(JLabel textLabel, RoundedProgressBar bar, JLabel percentLabel, int solved, int total) {
        textLabel.setText(solved + "/" + total);
        int pct = total > 0 ? (int) (((double) solved / total) * 100) : 0;
        bar.setValuePercent(pct);
        percentLabel.setText(pct + "%");
    }

    /**
     * A JPanel that always matches its enclosing JScrollPane viewport's width
     * instead of shrinking to its own preferred width. Without this, a
     * BoxLayout(Y_AXIS) panel placed inside a JScrollPane can size itself far
     * narrower than the visible area, producing a squished layout and an
     * unwanted horizontal scrollbar.
     */
    private static class ScrollableContentPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 100;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /**
     * A slim pill-shaped progress bar: rounded track + rounded fill, painted
     * directly (JProgressBar can't do rounded corners without a custom UI).
     */
    private static class RoundedProgressBar extends JComponent {
        private int percent = 0;
        private final Color fillColor;
        private final Color trackColor;

        RoundedProgressBar(Color fillColor, Color trackColor) {
            this.fillColor = fillColor;
            this.trackColor = trackColor;
            setOpaque(false);
        }

        void setValuePercent(int pct) {
            this.percent = Math.max(0, Math.min(100, pct));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            int w = getWidth();

            g2.setColor(trackColor);
            g2.fillRoundRect(0, 0, w, h, h, h);

            int fillW = (int) Math.round(w * (percent / 100.0));
            if (fillW > 0) {
                // Clamp to at least the track's rounded-cap width so a small
                // percentage still renders as a visible rounded pill segment.
                fillW = Math.max(fillW, h);
                fillW = Math.min(fillW, w);
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, fillW, h, h, h);
            }
            g2.dispose();
        }
    }

    /**
     * Small rounded "pill" badge used for the percentage readout on each
     * progress row — tinted background, matching-tone text.
     */
    private static class PillBadge extends JLabel {
        private final Color bgColor;

        PillBadge(String text, Color bgColor, Color textColor) {
            super(text);
            this.bgColor = bgColor;
            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
