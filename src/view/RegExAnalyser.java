package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.Controller;
import net.miginfocom.swing.MigLayout;

public class RegExAnalyser extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int HEIGHT = 620;
    private Controller controller;
    private JTextArea regexInputArea;
    private JPanel inputPanel;
    private JPanel outputPanel;

    private JButton btnNewRegex;
    private JButton btnNewAutomaton;
    private JButton btnAddRegex = new JButton("Add");
    private JButton btnAddAutomaton = new JButton("Add");;
    private JButton btnRegexToDFA = new JButton("Convert to DFA");
    private JButton btnRemoveRegex = new JButton("Remove");

    private JList<String> regexList;
    private JList<String> automatonsList;

    private DefaultListModel<String> regexListModel;
    private DefaultListModel<String> automatonListModel;

    private static Set<Character> regexAllowedSymbols = new HashSet<>();
    static {
        regexAllowedSymbols.addAll(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', '(', ')', '|', '*', '+', '?'));
    }

    private void removeRegex() {
        regexListModel.remove(regexList.getSelectedIndex());
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
        tableScroller.setPreferredSize(new Dimension(380, 200));

        cleanOutputPanel();
        outputPanel.add(tableScroller);
    }

    private void convertRegexToDFA() {
        int regexIndex = regexList.getSelectedIndex();
        int index = controller.convertRegExToAutomaton(regexIndex);
        automatonListModel.addElement("FA " + index + " from Regex " + regexIndex);
        showAutomaton(index);
    }

    private void addRegularExpression() {
        String input = regexInputArea.getText();

        boolean regexIsValid = true;
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            if (!regexAllowedSymbols.contains(symbol)) {
                JOptionPane.showMessageDialog(this, "The symbol '" + symbol + "' is not allowed.", "Input error",
                        JOptionPane.ERROR_MESSAGE);
                regexIsValid = false;
                break;
            }
        }

        if (regexIsValid) {
            int index = controller.createRegularExpression(input);
            String name = "Regex " + index;
            regexListModel.addElement(name);
            regexList.setSelectedIndex(index);
            btnRegexToDFA.setEnabled(true);
            btnRemoveRegex.setEnabled(true);
        }
    }

    private void createActionListeners() {

        // Regex related
        btnNewRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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
                automatonInputPanel();
            }
        });
    }

    public void addComponentsToPane(Container pane) {

        JPanel leftPanel = new JPanel(new MigLayout("wrap 1", "[c]", "[c]"));
        leftPanel.setBorder(new TitledBorder("Regular Expressions"));
        leftPanel.setPreferredSize(new Dimension(200, HEIGHT));

        btnNewRegex = new JButton("New RegEx");

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
        resetInputPanel();

        outputPanel = new JPanel();
        outputPanel.setBorder(new TitledBorder("Output"));
        resetOutputPanel();

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.add(inputPanel);
        middlePanel.add(outputPanel);
        middlePanel.setPreferredSize(new Dimension(400, HEIGHT));

        // ---- Right Panel -----------------------------------------

        JPanel rightPanel = new JPanel(new MigLayout("wrap 1", "[c]", "[c]"));
        rightPanel.setBorder(new TitledBorder("Automatons"));
        rightPanel.setPreferredSize(new Dimension(200, HEIGHT));

        btnNewAutomaton = new JButton("New Automaton");

        automatonsList = new JList<>();
        automatonsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        automatonsList.setModel(automatonListModel);
        automatonsList.setLayoutOrientation(JList.VERTICAL);
        automatonsList.setVisibleRowCount(-1);

        JScrollPane rightScrollPane = new JScrollPane(automatonsList);
        rightScrollPane.setPreferredSize(new Dimension(180, 400));
        rightPanel.add(rightScrollPane);
        rightPanel.add(btnNewAutomaton, "growx");

        pane.setLayout(new BorderLayout(10, 10));
        pane.add(leftPanel, BorderLayout.WEST);
        pane.add(middlePanel, BorderLayout.CENTER);
        pane.add(rightPanel, BorderLayout.EAST);
    }

    private void regexInputPanel() {
        JPanel regexInputPanel = new JPanel(new MigLayout("", "[l][r]", "[c][c][c]"));
        regexInputArea = new JTextArea(3, 30);

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

        DefaultTableModel model = new DefaultTableModel(11, 7);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(false);
        JScrollPane tableScroller = new JScrollPane(table);
        tableScroller.setPreferredSize(new Dimension(380, 200));

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
     * Create the GUI and show it. For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        JFrame frame = new JFrame("GridBagLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        addComponentsToPane(frame.getContentPane());
        createActionListeners();

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
