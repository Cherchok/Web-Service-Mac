package UI;

import WebServiceApplication.WebData;
import WebServiceApplication.XMLresponse;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class TableFrame extends JFrame {

    // constructor
    TableFrame(String tableName, String rowQuantity, String language) {

        //set title to frame
        super(tableName);
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);

        //get SOAP response, by dilling params
        XMLresponse xmlResponse = new XMLresponse(tableName, rowQuantity, language);

        // model of filling table
        DefaultTableModel tableModel = new DefaultTableModel();

        // filling the table by values
        Map<String, List<String>> data = new LinkedHashMap<>();
        WebData wd = new WebData();
        try {
            data = wd.getResponse(xmlResponse.getXMLresponse());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String k : data.keySet()) {
            Object[] obj = data.get(k).toArray();
            tableModel.addColumn(k, obj);
        }

        // interface settings for table
        table.setModel(tableModel);
        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.BLACK);
        Font font = new Font("", Font.PLAIN, 15);
        table.setFont(font);
        table.setRowHeight(20);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // !!!! to enable scrolls

        // set width of columns
        int columnNumber = 0;
        for (String leng : wd.getColumnLeng()) {
            int length = Integer.parseInt(leng) * 10;
            if (length > table.getColumnModel().getColumn(columnNumber).getWidth()) {
                table.getColumnModel().getColumn(columnNumber).setMinWidth(length);
            } else
                table.getColumnModel().getColumn(columnNumber).setMinWidth(table.getColumnModel()
                        .getColumn(columnNumber).getWidth());
            columnNumber++;

        }

        // enable scrolls (horizontal and vertical) for table. It possible only when "JTable.AUTO_RESIZE_OFF"!!!!!!
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
