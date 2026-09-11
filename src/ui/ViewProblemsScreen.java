package ui;

import model.CodingProblem;
import model.Topic;
import service.ProblemService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen displaying the full list of problems with search, filtering, 
 * sorting, and update/delete actions. Displays interactive note previews.
 */
public class ViewProblemsScreen extends JPanel {
    private final MainFrame mainFrame;
    private final ProblemService problemService;
    private List<CodingProblem> loadedProblems = new ArrayList<>();
    private List<Topic> loadedTopics = new ArrayList<>();

    // Search and Filters
    private JTextField txtSearch;
    private JComboBox<String> cbPlatform;
    private JComboBox<String> cbDifficulty;
    private JComboBox<Object> cbTopic;
    private JComboBox<String> cbStatus;
    private JComboBox<String> cbSort;
    private JButton btnReset;

    // Table elements
    private JTable tblProblems;
    private DefaultTableModel tableModel;
    private JPanel tableContainerPanel;
    private CardLayout tableCardLayout;

    // Actions
    private JButton btnView;   // Indigo style View button
    private JButton btnEdit;   // Styled as "Edit" (Orange)
    private JButton btnDelete; // Styled as "Delete" (Red)
    private JButton btnExportCsv; // Exports the currently filtered list to a .csv file

    public ViewProblemsScreen(MainFrame mainFrame, ProblemService problemService) {
        this.mainFrame = mainFrame;
        this.problemService = problemService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Section (Title + Filters)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Coding Problems Directory");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#0F172A"));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(lblTitle);

        // Filter Bar Panel (Row flow layout)
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterBar.setBackground(Color.decode("#F8FAFC"));
        filterBar.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 12));
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        txtSearch.setPreferredSize(new Dimension(130, 32));

        JLabel lblPlatform = new JLabel("Platform:");
        lblPlatform.setFont(new Font("SansSerif", Font.BOLD, 12));
        cbPlatform = new JComboBox<>(new String[]{"All Platforms", "LeetCode", "NeetCode", "CodeChef", "HackerRank", "CodeStudio", "Codeforces", "GeeksforGeeks", "Other"});
        cbPlatform.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbPlatform.setPreferredSize(new Dimension(120, 32));

        JLabel lblDifficulty = new JLabel("Difficulty:");
        lblDifficulty.setFont(new Font("SansSerif", Font.BOLD, 12));
        cbDifficulty = new JComboBox<>(new String[]{"All", "Easy", "Medium", "Hard"});
        cbDifficulty.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbDifficulty.setPreferredSize(new Dimension(90, 32));

        JLabel lblTopic = new JLabel("Topic:");
        lblTopic.setFont(new Font("SansSerif", Font.BOLD, 12));
        cbTopic = new JComboBox<>(new Object[]{"All Topics"});
        cbTopic.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbTopic.setPreferredSize(new Dimension(130, 32));

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        cbStatus = new JComboBox<>(new String[]{"All Statuses", "Solved", "Pending", "Revising"});
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbStatus.setPreferredSize(new Dimension(110, 32));

        JLabel lblSort = new JLabel("Sort:");
        lblSort.setFont(new Font("SansSerif", Font.BOLD, 12));
        cbSort = new JComboBox<>(new String[]{"Problem Name", "Date Solved", "Difficulty"});
        cbSort.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cbSort.setPreferredSize(new Dimension(120, 32));

        btnReset = new JButton("Clear");
        ButtonStyleUtil.applyGrayStyle(btnReset, new Dimension(80, 32)); // Gray style Clear button

        filterBar.add(lblSearch);
        filterBar.add(txtSearch);
        filterBar.add(lblPlatform);
        filterBar.add(cbPlatform);
        filterBar.add(lblDifficulty);
        filterBar.add(cbDifficulty);
        filterBar.add(lblTopic);
        filterBar.add(cbTopic);
        filterBar.add(lblStatus);
        filterBar.add(cbStatus);
        filterBar.add(lblSort);
        filterBar.add(cbSort);
        filterBar.add(btnReset);

        topPanel.add(filterBar);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Section (Table Card Container)
        tableCardLayout = new CardLayout();
        tableContainerPanel = new JPanel(tableCardLayout);
        tableContainerPanel.setBackground(Color.WHITE);

        String[] columnNames = {"Problem", "Platform", "Difficulty", "Topic", "Status", "Solved Date", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProblems = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#F8FAFC"));
                }
                return c;
            }
        };
        tblProblems.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tblProblems.setRowHeight(28);
        tblProblems.setGridColor(Color.decode("#F1F5F9"));
        tblProblems.setShowHorizontalLines(true);
        tblProblems.setShowVerticalLines(false);
        tblProblems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header style
        tblProblems.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#4F46E5")); // Blue
                label.setForeground(Color.WHITE);
                label.setFont(new Font("SansSerif", Font.BOLD, 12));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#CBD5E1")));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblProblems);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        tableContainerPanel.add(scrollPane, "TABLE");

        // Empty state card
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(Color.decode("#F8FAFC"));
        emptyPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1));
        JLabel lblEmptyMsg = new JLabel("No Problems Added Yet");
        lblEmptyMsg.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblEmptyMsg.setForeground(Color.decode("#94A3B8"));
        emptyPanel.add(lblEmptyMsg);
        tableContainerPanel.add(emptyPanel, "EMPTY");

        add(tableContainerPanel, BorderLayout.CENTER);

        // 3. Bottom Section (Action Buttons: Export | View, Edit, Delete)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JPanel actionButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionButtonsRight.setBackground(Color.WHITE);

        btnView = new JButton("View");
        ButtonStyleUtil.applyBlueStyle(btnView); // Indigo style View details button

        btnEdit = new JButton("Edit");
        ButtonStyleUtil.applyOrangeStyle(btnEdit); // Orange style Edit button

        btnDelete = new JButton("Delete");
        ButtonStyleUtil.applyRedStyle(btnDelete); // Red style Delete button

        actionButtonsRight.add(btnView);
        actionButtonsRight.add(btnEdit);
        actionButtonsRight.add(btnDelete);

        JPanel actionButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        actionButtonsLeft.setBackground(Color.WHITE);

        btnExportCsv = new JButton("Export to CSV");
        ButtonStyleUtil.applyGrayStyle(btnExportCsv);
        actionButtonsLeft.add(btnExportCsv);

        bottomPanel.add(actionButtonsLeft, BorderLayout.WEST);
        bottomPanel.add(actionButtonsRight, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Trigger filters on search text input
        DocumentListener searchTrigger = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        txtSearch.getDocument().addDocumentListener(searchTrigger);

        cbPlatform.addActionListener(e -> applyFilters());
        cbDifficulty.addActionListener(e -> applyFilters());
        cbTopic.addActionListener(e -> applyFilters());
        cbStatus.addActionListener(e -> applyFilters());
        cbSort.addActionListener(e -> applyFilters());

        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cbPlatform.setSelectedIndex(0);
            cbDifficulty.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            cbSort.setSelectedIndex(0);
            if (cbTopic.getItemCount() > 0) {
                cbTopic.setSelectedIndex(0);
            }
            applyFilters();
        });

        // Double-click table row listener to trigger Notes viewer JDialog
        tblProblems.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblProblems.getSelectedRow();
                    int col = tblProblems.getSelectedColumn();
                    if (row != -1 && col == 6) { // Notes column
                        showProblemDetailsDialog(row);
                    }
                }
            }
        });

        btnView.addActionListener(e -> handleView());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());
        btnExportCsv.addActionListener(e -> handleExportCsv());
    }

    public void refreshData() {
        if (mainFrame.getCurrentUser() == null) return;
        loadFilterTopics();
        applyFilters();
    }

    private void loadFilterTopics() {
        cbTopic.removeAllItems();
        cbTopic.addItem("All Topics");
        try {
            loadedTopics = problemService.getAllTopics();
            for (Topic t : loadedTopics) {
                cbTopic.addItem(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDifficultyWeight(String diff) {
        if (diff == null) return 3;
        if (diff.equalsIgnoreCase("Easy")) return 1;
        if (diff.equalsIgnoreCase("Medium")) return 2;
        if (diff.equalsIgnoreCase("Hard")) return 3;
        return 4;
    }

    private void applyFilters() {
        if (mainFrame.getCurrentUser() == null) return;

        int userId = mainFrame.getCurrentUser().getId();
        String searchTitle = txtSearch.getText();
        String difficulty = (String) cbDifficulty.getSelectedItem();
        
        Integer topicId = null;
        Object selectedTopic = cbTopic.getSelectedItem();
        if (selectedTopic instanceof Topic) {
            topicId = ((Topic) selectedTopic).getId();
        }

        try {
            tableModel.setRowCount(0);
            
            // Query DB using existing search criteria
            List<CodingProblem> dbProblems = problemService.searchAndFilterProblems(userId, searchTitle, difficulty, topicId);
            
            // Perform Java memory filters for Platform and Status
            loadedProblems = new ArrayList<>();
            for (CodingProblem cp : dbProblems) {
                // Platform Filter
                String selectedPlatform = (String) cbPlatform.getSelectedItem();
                if (selectedPlatform != null && !selectedPlatform.equals("All Platforms")) {
                    if (!selectedPlatform.equalsIgnoreCase(cp.getPlatform())) {
                        continue;
                    }
                }
                
                // Status Filter
                String selectedStatus = (String) cbStatus.getSelectedItem();
                if (selectedStatus != null && !selectedStatus.equals("All Statuses")) {
                    if (!selectedStatus.equalsIgnoreCase(cp.getStatus())) {
                        continue;
                    }
                }
                
                loadedProblems.add(cp);
            }

            // Perform Java memory Sorting
            String sortBy = (String) cbSort.getSelectedItem();
            if (sortBy != null) {
                if (sortBy.equals("Problem Name")) {
                    loadedProblems.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                } else if (sortBy.equals("Date Solved")) {
                    loadedProblems.sort((a, b) -> {
                        Date d1 = a.getDateSolved();
                        Date d2 = b.getDateSolved();
                        if (d1 == null && d2 == null) return 0;
                        if (d1 == null) return 1; // Pending solved date at bottom
                        if (d2 == null) return -1;
                        return d2.compareTo(d1); // Descending solved date
                    });
                } else if (sortBy.equals("Difficulty")) {
                    loadedProblems.sort((a, b) -> {
                        int w1 = getDifficultyWeight(a.getDifficulty());
                        int w2 = getDifficultyWeight(b.getDifficulty());
                        return Integer.compare(w1, w2);
                    });
                }
            }

            // Bind to table or swap card to EMPTY if list is empty
            if (loadedProblems.isEmpty()) {
                tableCardLayout.show(tableContainerPanel, "EMPTY");
            } else {
                tableCardLayout.show(tableContainerPanel, "TABLE");
                for (CodingProblem cp : loadedProblems) {
                    Date dSolved = cp.getDateSolved();
                    String dateStr = (dSolved != null) ? dSolved.toString() : "-";
                    
                    // Note preview truncation (max 30 characters)
                    String notesPreview = cp.getNotes();
                    if (notesPreview != null && notesPreview.length() > 30) {
                        notesPreview = notesPreview.substring(0, 30) + "...";
                    } else if (notesPreview == null) {
                        notesPreview = "";
                    }
                    
                    tableModel.addRow(new Object[]{
                        cp.getTitle(),
                        cp.getPlatform(),
                        cp.getDifficulty(),
                        cp.getTopicName(),
                        cp.getStatus(),
                        dateStr,
                        notesPreview
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error querying coding problems: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleView() {
        int selectedRow = tblProblems.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a problem from the table to view.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showProblemDetailsDialog(selectedRow);
    }

    private void handleEdit() {
        int selectedRow = tblProblems.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a problem from the table to edit.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CodingProblem selectedProblem = loadedProblems.get(selectedRow);
        mainFrame.navigateToEditProblem(selectedProblem);
    }

    private void handleDelete() {
        int selectedRow = tblProblems.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a problem from the table to delete.", 
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CodingProblem selectedProblem = loadedProblems.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this problem?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = problemService.deleteProblem(selectedProblem.getId(), mainFrame.getCurrentUser().getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                            "Problem deleted successfully.", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    applyFilters();
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Could not delete problem.", 
                            "Failure", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error deleting problem: " + ex.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Exports the currently visible (filtered) problem list to a CSV file,
     * so users can back up or share their practice history outside the app.
     */
    private void handleExportCsv() {
        if (loadedProblems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "There are no problems to export.",
                    "Nothing to Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Problems to CSV");
        fileChooser.setSelectedFile(new File("coding_problems_export.csv"));

        int userChoice = fileChooser.showSaveDialog(this);
        if (userChoice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Problem Name,Platform,Difficulty,Topic,Status,Solved Date,Notes");
            for (CodingProblem cp : loadedProblems) {
                String dateStr = cp.getDateSolved() != null ? cp.getDateSolved().toString() : "";
                writer.println(String.join(",",
                        csvEscape(cp.getTitle()),
                        csvEscape(cp.getPlatform()),
                        csvEscape(cp.getDifficulty()),
                        csvEscape(cp.getTopicName()),
                        csvEscape(cp.getStatus()),
                        csvEscape(dateStr),
                        csvEscape(cp.getNotes())
                ));
            }
            JOptionPane.showMessageDialog(this,
                    "Exported " + loadedProblems.size() + " problem(s) to:\n" + file.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to write CSV file: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Wraps a value in double quotes and escapes embedded quotes, so commas,
     * quotes, or newlines inside notes/titles don't corrupt the CSV structure.
     */
    private String csvEscape(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /**
     * Opens a read-only Swing dialog showing problem details and full scrollable notes.
     */
    private void showProblemDetailsDialog(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= loadedProblems.size()) return;
        CodingProblem cp = loadedProblems.get(rowIndex);

        JDialog dialog = new JDialog(mainFrame, "Problem details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(panel, gbc, row++, "Problem Name:", cp.getTitle());
        addDetailRow(panel, gbc, row++, "Platform:", cp.getPlatform());
        addDetailRow(panel, gbc, row++, "Difficulty:", cp.getDifficulty());
        addDetailRow(panel, gbc, row++, "Topic:", cp.getTopicName());
        addDetailRow(panel, gbc, row++, "Status:", cp.getStatus());
        
        Date dSolved = cp.getDateSolved();
        String dateStr = (dSolved != null) ? dSolved.toString() : "-";
        addDetailRow(panel, gbc, row++, "Solved Date:", dateStr);

        // Notes Label row
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        JLabel lblNotes = new JLabel("Notes:");
        lblNotes.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblNotes.setForeground(Color.decode("#475569"));
        panel.add(lblNotes, gbc);

        // Notes Text Area row
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea taFullNotes = new JTextArea(cp.getNotes() == null ? "" : cp.getNotes());
        taFullNotes.setFont(new Font("SansSerif", Font.PLAIN, 13));
        taFullNotes.setLineWrap(true);
        taFullNotes.setWrapStyleWord(true);
        taFullNotes.setEditable(false);
        taFullNotes.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        
        JScrollPane notesScroll = new JScrollPane(taFullNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(Color.decode("#CBD5E1")));
        panel.add(notesScroll, gbc);

        // Close Action Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Close");
        ButtonStyleUtil.applyBlueStyle(btnClose);
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int rowY, String labelText, String valueText) {
        gbc.gridy = rowY;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;

        gbc.gridx = 0; gbc.weightx = 0.3;
        JLabel title = new JLabel(labelText);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(Color.decode("#475569"));
        panel.add(title, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel val = new JLabel(valueText == null || valueText.isEmpty() ? "-" : valueText);
        val.setFont(new Font("SansSerif", Font.PLAIN, 13));
        val.setForeground(Color.decode("#1E293B"));
        panel.add(val, gbc);
    }
}
