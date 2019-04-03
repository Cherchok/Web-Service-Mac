package UI;

import WebServiceApplication.WebData;
import WebServiceApplication.XMLresponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class TableFrame extends JFrame {
    private Map<String, List<String>> dataMap;

    TableFrame(String title) {
        super(title);
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);

        String arg1 = "T001";
        String arg2 = "200";
        String arg3 = "R";

        XMLresponse xmLresponse = new XMLresponse(arg1, arg2, arg3);
        WebData webData = new WebData();
        try {
            dataMap = new LinkedHashMap<>();
            dataMap = webData.getResponse(xmLresponse.getXMLresponse());

        } catch (Exception e) {
            e.printStackTrace();
        }

        DefaultTableModel model3 = new DefaultTableModel();

        for (String key: dataMap.keySet()) {
            Object[] arrTemp = dataMap.get(key).toArray();
            Arrays.sort(arrTemp);
            model3.addColumn(key, arrTemp);
        }

        table.setModel(model3);
        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.BLACK);
        Font font = new Font("", Font.PLAIN, 15);
        table.setFont(font);
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(true);
        }

        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        JButton findAllButton = new JButton("find all");
        JPanel buttons = new JPanel(new GridLayout(0, 3));
        buttons.setMaximumSize(new Dimension(300, 100));
        buttons.add(findAllButton);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
        controls.add(buttons);
        controls.add(Box.createVerticalGlue());

        this.add(controls, BorderLayout.EAST);
        this.setSize(900, 400);
    }
}
