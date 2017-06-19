package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.Controller;
import net.miginfocom.swing.MigLayout;

public class RegExAnalyser extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int HEIGHT = 620;
    private static final int MIDDLE_PANEL_WIDTH = 500;

    private Controller controller;
    private JTextArea regexInputArea;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JTable inputTable;

    private JButton btnNewRegex = new JButton("New regex");
    private JButton btnNewAutomaton = new JButton("New automaton");
    private JButton btnAddRegex = new JButton("Add");
    private JButton btnAddAutomaton = new JButton("Add");;
    private JButton btnRegexToDFA = new JButton("Convert to DFA");
    private JButton btnRemoveRegex = new JButton("Remove");
    private JButton btnRemoveAutomaton = new JButton("Remove");

    private JList<String> regexList;
    private JList<String> automatonList;
    private List<Integer> selectedRegexes;
    private List<Integer> selectedAutomatons;

    private DefaultListModel<String> regexListModel;
    private DefaultListModel<String> automatonListModel;

    private static Set<Character> regexAllowedSymbols = new HashSet<>();
    private static Set<Character> vocabularySymbols = new HashSet<>();
    private static Set<Character> stateSymbols = new HashSet<>();
    static {
        regexAllowedSymbols.addAll(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', '(', ')', '|', '*', '+', '?'));

        vocabularySymbols.addAll(
                Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                        's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));

        stateSymbols.addAll(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'));
    }

    private void removeAutomaton() {
        int index = automatonList.getSelectedIndex();
        controller.removeAutomaton(index);
        automatonListModel.remove(index);
        resetInputPanel();
        resetOutputPanel();
        if (automatonListModel.size() == 0) {
            btnRemoveAutomaton.setEnabled(false);
            // TODO: add other buttons
        }
    }

    private void removeRegex() {
        int index = regexList.getSelectedIndex();
        controller.removeRegex(index);
        regexListModel.remove(index);
        resetInputPanel();
        resetOutputPanel();
        if (regexListModel.size() == 0) {
            btnRemoveRegex.setEnabled(false);
            btnRegexToDFA.setEnabled(false);
        }
    }

    private void showAutomaton(int index) {
        Vector<String> columnNames = controller.columnNamesFromAutomaton(index);
        Vector<Vector<String>> data = controller.dataFromAutomaton(index);

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(false);

        // Aligns every cell in the table in the center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col = 0; col < model.getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }

        JScrollPane tableScroller = new JScrollPane(table);
        tableScroller.setPreferredSize(new Dimension(MIDDLE_PANEL_WIDTH - 20, 200));

        cleanOutputPanel();
        outputPanel.add(tableScroller);
    }

    private void addAutomaton() {
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        List<String> states = new ArrayList<>();
        List<String> vocabulary = new ArrayList<>();
        String initialState = null;
        String errorMessage = null;

        // Get states
        for (int row = 1; row < model.getRowCount(); row++) {
            String value = (String) model.getValueAt(row, 2);
            if (value != null && !value.isEmpty()) {
                if (!stateSymbols.contains(value.charAt(0))) {
                    errorMessage = "The symbol '" + value + "' is not allowed as a state.";
                    showInputErrorMessage(errorMessage);
                    return;
                }
                states.add(value);
            } else {
                break; // No more states
            }
        }
        if (states.isEmpty()) {
            errorMessage = "There are no states in the automaton.";
            showInputErrorMessage(errorMessage);
            return;
        }

        // Get vocabulary
        for (int col = 3; col < model.getColumnCount(); col++) {
            String value = (String) model.getValueAt(0, col);
            if (value != null && !value.isEmpty()) {
                if (!vocabularySymbols.contains(value.charAt(0))) {
                    errorMessage = "The symbol '" + value + "' is not allowed in the vocabulary.";
                    showInputErrorMessage(errorMessage);
                    return;
                }
                vocabulary.add(value);
            } else {
                break; // Vocabulary is over
            }
        }
        if (vocabulary.isEmpty()) {
            errorMessage = "There are no symbols in the vocabulary.";
            JOptionPane.showMessageDialog(this, errorMessage, "Input error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<List<String>> transitions = new ArrayList<>();
        transitions.add(vocabulary);

        // Get transitions
        for (int row = 1; row <= states.size(); row++) {
            List<String> rowTransitions = new ArrayList<>();
            for (int col = 0; col < vocabulary.size() + 3; col++) {
                String value = (String) model.getValueAt(row, col);

                // Initial state column
                if (col == 0 && value != null && value.equals("->")) {
                    initialState = (String) model.getValueAt(row, 2);
                    ;
                }

                // Accepting state column
                if (col == 1) {
                    if (value != null && value.equals("*")) {
                        rowTransitions.add("*");
                    } else {
                        rowTransitions.add("");
                    }
                }

                // State column
                if (col == 2) {
                    rowTransitions.add(value);
                }

                // Transitions columns
                if (col > 2) {
                    if (value != null && !value.isEmpty()) {
                        if (states.contains(value)) {
                            rowTransitions.add(value);
                        } else {
                            errorMessage = "State " + value + " at cell (" + row + "," + col + ") is invalid.";
                            showInputErrorMessage(errorMessage);
                            return;
                        }
                    } else {
                        rowTransitions.add("-");
                    }
                }
            }
            transitions.add(rowTransitions);
        }
        if (initialState == null) {
            errorMessage = "This automaton has no initial state.";
            showInputErrorMessage(errorMessage);
            return;
        }

        // Everything looks fine...
        int index = controller.createAutomaton(transitions, initialState);
        automatonListModel.addElement("AF " + index);
        automatonList.setSelectedIndex(index);
        btnRemoveAutomaton.setEnabled(true);
    }

    private void showInputErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Input error", JOptionPane.ERROR_MESSAGE);
    }

    private void convertRegexToDFA() {
        int regexIndex = regexList.getSelectedIndex();
        int index = controller.convertRegExToAutomaton(regexIndex);
        automatonListModel.addElement("FA " + index + " from Regex " + regexIndex);
        showAutomaton(index);
    }

    private void addRegularExpression() {
        String input = regexInputArea.getText();

        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            if (!regexAllowedSymbols.contains(symbol)) {
                String errorMessage = "The symbol '" + symbol + "' is not allowed.";
                showInputErrorMessage(errorMessage);
                return;
            }
        }

        int index = controller.createRegularExpression(input);
        regexListModel.addElement("Regex " + index);
        regexList.setSelectedIndex(index);
        btnRegexToDFA.setEnabled(true);
        btnRemoveRegex.setEnabled(true);
    }

    private void createListSelectionListeners() {
        regexList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                onRegexSelection(e);
            }
        });
        automatonList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                onAutomatonSelection(e);
            }
        });
    }

    private void onAutomatonSelection(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!e.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            selectedAutomatons = new ArrayList<>();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    selectedAutomatons.add(i);
                }
            }
            resetInputPanel();
            regexList.clearSelection();
            if (selectedAutomatons.size() == 1) {
                btnRemoveAutomaton.setEnabled(true);
                // TODO: add other buttons
                resetOutputPanel();
                showAutomaton(minIndex);
            } else {
                btnRemoveAutomaton.setEnabled(false);
                // TODO: add other buttons
            }
        }
    }

    private void onRegexSelection(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!e.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            selectedRegexes = new ArrayList<>();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    selectedRegexes.add(i);
                }
            }
            cleanInputPanel();
            regexInputPanel();
            automatonList.clearSelection();
            if (selectedRegexes.size() == 1) {
                btnRemoveRegex.setEnabled(true);
                btnRegexToDFA.setEnabled(true);
                regexInputArea.setText(controller.getRegexInputFor(minIndex));
                int index = controller.getAutomatonForRegex(minIndex);
                if (index == -1) {
                    resetOutputPanel();
                } else {
                    showAutomaton(index);
                }
            } else {
                btnRemoveRegex.setEnabled(false);
                btnRegexToDFA.setEnabled(false);
            }
        }
    }

    private void createActionListeners() {

        // Regex related
        btnNewRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                regexList.clearSelection();
                automatonList.clearSelection();
                regexInputPanel();
            }
        });
        btnAddRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addRegularExpression();
            }
        });
        btnRegexToDFA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                convertRegexToDFA();
            }
        });
        btnRemoveRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                removeRegex();
            }
        });

        // Automaton related
        btnNewAutomaton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                regexList.clearSelection();
                automatonList.clearSelection();
                automatonInputPanel();
            }
        });
        btnAddAutomaton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addAutomaton();
            }
        });
        btnRemoveAutomaton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                removeAutomaton();
            }
        });
    }

    public void addComponentsToPane(Container pane) {

        JPanel leftPanel = new JPanel(new MigLayout("wrap 1", "[c]", "[c]"));
        leftPanel.setBorder(new TitledBorder("Regular Expressions"));
        leftPanel.setPreferredSize(new Dimension(200, HEIGHT));

        regexList = new JList<>();
        regexList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        regexList.setModel(regexListModel);
        regexList.setLayoutOrientation(JList.VERTICAL);
        regexList.setVisibleRowCount(-1);

        JScrollPane leftScrollPane = new JScrollPane(regexList);
        leftScrollPane.setPreferredSize(new Dimension(180, 400));
        leftPanel.add(leftScrollPane);
        leftPanel.add(btnNewRegex, "growx");
        leftPanel.add(btnRegexToDFA, "growx");
        leftPanel.add(btnRemoveRegex, "growx");

        btnRegexToDFA.setEnabled(false);
        btnRemoveRegex.setEnabled(false);

        // ---- Middle Panel -----------------------------------------

        inputPanel = new JPanel();
        inputPanel.setBorder(new TitledBorder("Input"));
        inputPanel.setPreferredSize(new Dimension(MIDDLE_PANEL_WIDTH - 5, HEIGHT / 2 - 5));
        resetInputPanel();

        outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder("Output"));
        outputPanel.setPreferredSize(new Dimension(MIDDLE_PANEL_WIDTH - 5, HEIGHT / 2 - 5));
        resetOutputPanel();

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.add(inputPanel);
        middlePanel.add(outputPanel);
        middlePanel.setPreferredSize(new Dimension(MIDDLE_PANEL_WIDTH, HEIGHT));

        // ---- Right Panel -----------------------------------------

        JPanel rightPanel = new JPanel(new MigLayout("wrap 1", "[c]", "[c]"));
        rightPanel.setBorder(new TitledBorder("Automatons"));
        rightPanel.setPreferredSize(new Dimension(200, HEIGHT));

        automatonList = new JList<>();
        automatonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        automatonList.setModel(automatonListModel);
        automatonList.setLayoutOrientation(JList.VERTICAL);
        automatonList.setVisibleRowCount(-1);

        JScrollPane rightScrollPane = new JScrollPane(automatonList);
        rightScrollPane.setPreferredSize(new Dimension(180, 400));
        rightPanel.add(rightScrollPane);
        rightPanel.add(btnNewAutomaton, "growx");
        rightPanel.add(btnRemoveAutomaton, "growx");

        btnRemoveAutomaton.setEnabled(false);

        pane.setLayout(new BorderLayout(10, 10));
        pane.add(leftPanel, BorderLayout.WEST);
        pane.add(middlePanel, BorderLayout.CENTER);
        pane.add(rightPanel, BorderLayout.EAST);
    }

    private void regexInputPanel() {
        JPanel regexInputPanel = new JPanel(new MigLayout("", "[l][r]", "[c][c][c]"));
        regexInputArea = new JTextArea(5, 42);

        regexInputPanel.add(new JLabel("Enter the regular expression:"), "span,wrap");
        regexInputPanel.add(regexInputArea, "span,wrap");
        regexInputPanel.add(new JLabel());
        regexInputPanel.add(btnAddRegex);

        resetOutputPanel();
        cleanInputPanel();
        inputPanel.add(regexInputPanel);
    }

    private void automatonInputPanel() {
        JPanel automatonInputPanel = new JPanel(new MigLayout("", "[l][r]", "[c][c][c]"));

        DefaultTableModel model = new DefaultTableModel(0, 8);
        model.addRow(new Object[] { "Initial", "Accepting", "\u03B4", "", "", "", "", "" });
        for (int i = 0; i < 10; i++) {
            model.addRow(new Object[] { "", "", "", "", "", "", "", "" });
        }

        inputTable = new JTable(model);
        inputTable.setFillsViewportHeight(false);

        // Aligns every cell in the table in the center
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col = 0; col < model.getColumnCount(); col++) {
            inputTable.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }
        JScrollPane tableScroller = new JScrollPane(inputTable);
        tableScroller.setPreferredSize(new Dimension(MIDDLE_PANEL_WIDTH - 20, 200));

        automatonInputPanel.add(new JLabel("Enter the automaton:"), "span,wrap");
        automatonInputPanel.add(tableScroller, "span,wrap");
        automatonInputPanel.add(new JLabel());
        automatonInputPanel.add(btnAddAutomaton);

        resetOutputPanel();
        cleanInputPanel();
        inputPanel.add(automatonInputPanel);
    }

    private void resetInputPanel() {
        cleanInputPanel();
        inputPanel.add(new JLabel("Use of one the 'New' buttons on the sides."));
    }

    private void resetOutputPanel() {
        cleanOutputPanel();
        outputPanel.add(new JLabel("Select a regular expression or an automaton and an operation."));
    }

    private void cleanInputPanel() {
        inputPanel.removeAll();
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void cleanOutputPanel() {
        outputPanel.removeAll();
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private void createAndShowGUI() {
        JFrame frame = new JFrame("GridBagLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        addComponentsToPane(frame.getContentPane());
        createActionListeners();
        createListSelectionListeners();

        // Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public RegExAnalyser() {
        controller = Controller.instance();
        regexListModel = new DefaultListModel<>();
        automatonListModel = new DefaultListModel<>();
        createAndShowGUI();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegExAnalyser();
            }
        });
    }

}
