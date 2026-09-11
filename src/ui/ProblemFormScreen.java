package ui;

import exception.ValidationException;
import model.CodingProblem;
import model.Topic;
import service.ProblemService;
import util.ButtonStyleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Screen used to both Add and Edit coding problems.
 * Improved styling and button styles.
 */
public class ProblemFormScreen extends JPanel {
    private final MainFrame mainFrame;
    private final ProblemService problemService;
    private CodingProblem editingProblem; // null if Add mode

    // UI Fields
    private JLabel lblHeaderTitle;
    private JTextField txtTitle;
    private JComboBox<String> cbPlatform;
    private JComboBox<String> cbDifficulty;
    private JComboBox<Topic> cbTopic;
    private JComboBox<String> cbStatus;
    private JTextField txtDateSolved;
    private JTextArea taNotes;
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnAddTopic;

    public ProblemFormScreen(MainFrame mainFrame, ProblemService problemService) {
        this.mainFrame = mainFrame;
        this.problemService = problemService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Title Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        lblHeaderTitle = new JLabel("Add Coding Problem");
        lblHeaderTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeaderTitle.setForeground(Color.decode("#0F172A"));
        lblHeaderTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.add(lblHeaderTitle);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Form Fields Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1),
                new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        JLabel lblTitle = new JLabel("Problem Name *:");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblTitle, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 2;
        txtTitle = new JTextField();
        txtTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(txtTitle, gbc);

        // Platform
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1; gbc.gridwidth = 1;
        JLabel lblPlatform = new JLabel("Platform *:");
        lblPlatform.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblPlatform, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 2;
        String[] platforms = {"LeetCode", "NeetCode", "CodeChef", "HackerRank", "CodeStudio", "Codeforces", "GeeksforGeeks", "Other"};
        cbPlatform = new JComboBox<>(platforms);
        cbPlatform.setEditable(true);
        cbPlatform.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cbPlatform, gbc);

        // Difficulty
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1; gbc.gridwidth = 1;
        JLabel lblDifficulty = new JLabel("Difficulty *:");
        lblDifficulty.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblDifficulty, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 2;
        cbDifficulty = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        cbDifficulty.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cbDifficulty, gbc);

        // Topic
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1; gbc.gridwidth = 1;
        JLabel lblTopic = new JLabel("Topic *:");
        lblTopic.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblTopic, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cbTopic = new JComboBox<>();
        cbTopic.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cbTopic, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        btnAddTopic = new JButton("+ New Topic");
        btnAddTopic.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAddTopic.setBackground(Color.decode("#F1F5F9"));
        btnAddTopic.setForeground(Color.decode("#4F46E5"));
        btnAddTopic.setFocusPainted(false);
        btnAddTopic.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnAddTopic, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.1; gbc.gridwidth = 1;
        JLabel lblStatus = new JLabel("Status *:");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 2;
        cbStatus = new JComboBox<>(new String[]{"Solved", "Pending", "Revising"});
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(cbStatus, gbc);

        // Date Solved
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.1; gbc.gridwidth = 1;
        JLabel lblDate = new JLabel("Solved Date (YYYY-MM-DD):");
        lblDate.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblDate, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 2;
        txtDateSolved = new JTextField();
        txtDateSolved.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDateSolved.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CBD5E1"), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(txtDateSolved, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblNotes = new JLabel("Notes:");
        lblNotes.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(lblNotes, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.8;
        taNotes = new JTextArea(5, 20);
        taNotes.setFont(new Font("SansSerif", Font.PLAIN, 13));
        taNotes.setLineWrap(true);
        taNotes.setWrapStyleWord(true);
        taNotes.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        JScrollPane notesScroll = new JScrollPane(taNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(Color.decode("#CBD5E1")));
        formPanel.add(notesScroll, gbc);

        // Action Buttons Row
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3; gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonRow.setBackground(Color.WHITE);

        btnSave = new JButton("Save");
        ButtonStyleUtil.applyBlueStyle(btnSave); // Blue style Save button

        btnClear = new JButton("Clear");
        ButtonStyleUtil.applyGrayStyle(btnClear); // Gray style Clear button

        buttonRow.add(btnSave);
        buttonRow.add(btnClear);
        formPanel.add(buttonRow, gbc);

        add(formPanel, BorderLayout.CENTER);

        // UI Event triggers
        cbStatus.addActionListener(e -> updateDateSolvedAvailability());
        btnAddTopic.addActionListener(e -> handleAddTopic());
        btnSave.addActionListener(e -> handleSave());
        btnClear.addActionListener(e -> handleCancel());
    }

    private void updateDateSolvedAvailability() {
        String status = (String) cbStatus.getSelectedItem();
        if ("Pending".equalsIgnoreCase(status)) {
            txtDateSolved.setText("");
            txtDateSolved.setEnabled(false);
        } else {
            txtDateSolved.setEnabled(true);
            if (txtDateSolved.getText().trim().isEmpty() && editingProblem == null) {
                txtDateSolved.setText(LocalDate.now().toString());
            }
        }
    }

    public void loadTopics() {
        cbTopic.removeAllItems();
        try {
            List<Topic> topics = problemService.getAllTopics();
            for (Topic t : topics) {
                cbTopic.addItem(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEditMode(CodingProblem problem) {
        this.editingProblem = problem;
        loadTopics();

        if (problem == null) {
            lblHeaderTitle.setText("Add Coding Problem");
            txtTitle.setText("");
            cbPlatform.setSelectedIndex(0);
            cbDifficulty.setSelectedIndex(0);
            if (cbTopic.getItemCount() > 0) cbTopic.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0); // Solved
            txtDateSolved.setText(LocalDate.now().toString());
            txtDateSolved.setEnabled(true);
            taNotes.setText("");
            btnSave.setText("Save");
        } else {
            lblHeaderTitle.setText("Edit Coding Problem");
            txtTitle.setText(problem.getTitle());
            cbPlatform.setSelectedItem(problem.getPlatform());
            cbDifficulty.setSelectedItem(problem.getDifficulty());
            
            for (int i = 0; i < cbTopic.getItemCount(); i++) {
                Topic t = cbTopic.getItemAt(i);
                if (t.getId() == problem.getTopicId()) {
                    cbTopic.setSelectedIndex(i);
                    break;
                }
            }
            
            cbStatus.setSelectedItem(problem.getStatus());
            if (problem.getDateSolved() != null) {
                txtDateSolved.setText(problem.getDateSolved().toString());
            } else {
                txtDateSolved.setText("");
            }
            updateDateSolvedAvailability();
            taNotes.setText(problem.getNotes());
            btnSave.setText("Save");
        }
    }

    private void handleAddTopic() {
        String newTopic = JOptionPane.showInputDialog(this, 
                "Enter new coding topic name:", 
                "Add Custom Topic", JOptionPane.QUESTION_MESSAGE);
        
        if (newTopic != null && !newTopic.trim().isEmpty()) {
            try {
                int newId = problemService.addTopic(newTopic);
                if (newId > 0) {
                    loadTopics();
                    for (int i = 0; i < cbTopic.getItemCount(); i++) {
                        if (cbTopic.getItemAt(i).getId() == newId) {
                            cbTopic.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void handleSave() {
        String title = txtTitle.getText().trim();
        String platform = (String) cbPlatform.getSelectedItem();
        if (platform != null) platform = platform.trim();
        
        String difficulty = (String) cbDifficulty.getSelectedItem();
        Topic selectedTopic = (Topic) cbTopic.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();
        String dateText = txtDateSolved.getText().trim();
        String notes = taNotes.getText().trim();

        CodingProblem cp = new CodingProblem();
        cp.setUserId(mainFrame.getCurrentUser().getId());
        cp.setTitle(title);
        cp.setPlatform(platform);
        cp.setDifficulty(difficulty);
        cp.setStatus(status);
        cp.setNotes(notes);

        if (selectedTopic != null) {
            cp.setTopicId(selectedTopic.getId());
        } else {
            cp.setTopicId(-1);
        }

        if (!"Pending".equalsIgnoreCase(status) && !dateText.isEmpty()) {
            try {
                cp.setDateSolved(Date.valueOf(dateText));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                        "Solved Date must be in format YYYY-MM-DD (e.g. 2026-07-19).", 
                        "Date Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            boolean success;
            if (editingProblem == null) {
                success = problemService.addProblem(cp);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                            "Coding problem saved successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.navigateTo("VIEW_PROBLEMS");
                }
            } else {
                cp.setId(editingProblem.getId());
                success = problemService.updateProblem(cp);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                            "Coding problem updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.navigateTo("VIEW_PROBLEMS");
                }
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleCancel() {
        if (editingProblem == null) {
            // In ADD mode, "Clear" resets the form
            txtTitle.setText("");
            cbPlatform.setSelectedIndex(0);
            cbDifficulty.setSelectedIndex(0);
            if (cbTopic.getItemCount() > 0) cbTopic.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            txtDateSolved.setText(LocalDate.now().toString());
            taNotes.setText("");
        } else {
            // In EDIT mode, "Clear" cancels and returns
            mainFrame.navigateTo("VIEW_PROBLEMS");
        }
    }
}
