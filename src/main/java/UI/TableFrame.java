package UI;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class TableFrame extends JFrame {
    private static final int NO_SELECTION = -1;
    private final DataTableModel model;
    private final List<JTextField> textFieldList;
    //    private int currentSelectedBookId = NO_SELECTION;
    private final JTable table;

    String arg1 = "MARA"; //ZCOMPTAB, MARA
    String arg2 = "200";
    String arg3 = "R";

    public TableFrame(String title) {
        super(title);
        table = new JTable();
        table.setAutoCreateRowSorter(true);

        model = new DataTableModel(arg1, arg2, arg3);
        table.setModel(model);
        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.BLACK);
        Font font = new Font("", Font.PLAIN, 22);
        table.setFont(font);
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setMinWidth(70);
        }

        // lables create and setting
        List<JLabel> lables = new LinkedList<>();
        for (String lable : model.getFields()) {
            lables.add(new JLabel(lable));
        }

        for (JLabel lable : lables) {
            String text = lable.getText();
            lable.setText(text);
        }

        // textFields create and setting
        textFieldList = new LinkedList<>();
        for (String fields : model.getFields()) {
            textFieldList.add(new JTextField());
        }

        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        JButton findAllButton = new JButton("find all");

        JPanel buttons = new JPanel(new GridLayout(0, 3));
        buttons.setMaximumSize(new Dimension(300, 100));

        buttons.add(findAllButton);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));

//        JPanel fieldsPanel = new JPanel();
//        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.LINE_AXIS));
//
//        for (JLabel fieldLable : lables) {
//            fieldsPanel.add(fieldLable);
//        }
//
//        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
//        controls.add(fieldsPanel);

        controls.add(buttons);
        controls.add(Box.createVerticalGlue());

        this.add(controls, BorderLayout.EAST);






        this.setSize(900, 400);
    }
}
