package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class RegExAnalyser extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int HEIGHT = 620;
    private JTextArea regexInputArea;
    private JPanel middlePanel;
    private JButton btnAddRegex;
    private JButton btnNewRegex;
    private JButton btnNewAutomaton;
    private JList<String> regexList;
    private JList<String> automatonsList;
    private JButton btnAddAutomaton;

    private void createActionListeners() {
        btnNewRegex.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cleanMiddlePanel();
                regexInputPanel();
            }
        });
        btnNewAutomaton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cleanMiddlePanel();
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
        regexList.setModel(new DefaultListModel<>());
        regexList.setLayoutOrientation(JList.VERTICAL);
        regexList.setVisibleRowCount(-1);
        
        JScrollPane leftScrollPane = new JScrollPane(regexList);
        leftScrollPane.setPreferredSize(new Dimension(180, 400));
        leftPanel.add(leftScrollPane);
        leftPanel.add(btnNewRegex, "growx");

        // ---- Middle Panel -----------------------------------------

        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        resetMiddlePanel();

        // ---- Right Panel -----------------------------------------

        JPanel rightPanel = new JPanel(new MigLayout("wrap 1", "[c]", "[c]"));
        rightPanel.setBorder(new TitledBorder("Automatons"));
        rightPanel.setPreferredSize(new Dimension(200, HEIGHT));
        
        btnNewAutomaton = new JButton("New Automaton");

        automatonsList = new JList<>();
        automatonsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        automatonsList.setModel(new DefaultListModel<>());
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
        JPanel inputPanel = new JPanel(new MigLayout("", "[l][r]", "[c][c][c]"));
        inputPanel.setBorder(new TitledBorder("Input"));
        regexInputArea = new JTextArea(3, 30);
        btnAddRegex = new JButton("Add");

        inputPanel.add(new JLabel("Enter the regular expression:"), "span,wrap");
        inputPanel.add(regexInputArea, "span,wrap");
        inputPanel.add(new JLabel());
        inputPanel.add(btnAddRegex);
        
        middlePanel.add(inputPanel);
    }

    private void automatonInputPanel() {
        JPanel inputPanel = new JPanel(new MigLayout("", "[l][r]", "[c][c][c]"));
        inputPanel.setBorder(new TitledBorder("Input"));
        btnAddAutomaton = new JButton("Add");

        DefaultTableModel model = new DefaultTableModel(11, 7);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(false);
        JScrollPane tableScroller = new JScrollPane(table);
        tableScroller.setPreferredSize(new Dimension(380, 200));
        
        inputPanel.add(new JLabel("Enter the automaton:"), "span,wrap");
        inputPanel.add(tableScroller, "span,wrap");
        inputPanel.add(new JLabel());
        inputPanel.add(btnAddAutomaton);
        
        middlePanel.add(inputPanel);
    }
    
    private void resetMiddlePanel() {
        cleanMiddlePanel();
        JPanel input = new JPanel();
        JPanel output = new JPanel();
        
        input.setBorder(new TitledBorder("Input"));
        output.setBorder(new TitledBorder("Output"));
        
        input.add(new JLabel("Use of one the 'New' buttons on the sides."));
        output.add(new JLabel("Select a regular expression or an automaton and an operation."));

        middlePanel.add(input);
        middlePanel.add(output);
        middlePanel.setPreferredSize(new Dimension(400, HEIGHT));
    }
    
    private void cleanMiddlePanel() {
        middlePanel.removeAll();
        middlePanel.revalidate();
        middlePanel.repaint();
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
