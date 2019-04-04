package UI;


import WebServiceApplication.XMLresponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class TableFrame extends JFrame {
    private Map<String, List<String>> dataMap = new LinkedHashMap<>();
    private List<String> columnLeng = new LinkedList<>();

    TableFrame(String title) {

        //set title to jframe
        super(title);
        JTable table = new JTable();
        table.setAutoCreateRowSorter(true);

        // params of request for Web Service
        String arg1 = "T001";
        String arg2 = "100";
        String arg3 = "R";

        //get SOAP response, by dilling params
        XMLresponse xmLresponse = new XMLresponse(arg1, arg2, arg3);
        try {
            getResponse(xmLresponse.getXMLresponse());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // model of filling jtable
        DefaultTableModel tableModel = new DefaultTableModel();

        // filling the jtable by values
        for (String key : dataMap.keySet()) {
            Object[] arrTemp = dataMap.get(key).toArray();
            tableModel.addColumn(key, arrTemp);
        }

        // interface settings for jtable
        table.setModel(tableModel);
        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.BLACK);
        Font font = new Font("", Font.PLAIN, 15);
        table.setFont(font);
        table.setRowHeight(20);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


        // set width of columns
        int columnNumber = 0;
        for (String leng : columnLeng) {
            int length = Integer.parseInt(leng) * 10;
            if (length > table.getColumnModel().getColumn(columnNumber).getWidth()) {
                table.getColumnModel().getColumn(columnNumber).setMinWidth(length);
            } else
                table.getColumnModel().getColumn(columnNumber).setMinWidth(table.getColumnModel()
                        .getColumn(columnNumber).getWidth());
            columnNumber++;
        }

        // enable scrolls (horizontal and vertical) for jtable. It possible only when "JTable.AUTO_RESIZE_OFF"!!!!!!
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

    private static Document loadXMLString(String XMLresponse) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(XMLresponse));

        return db.parse(is);
    }

    @SuppressWarnings({"all"})
    //get Map with name of fields as key and List of values as val by xml response.
    private Map<String, List<String>> getResponse(String XMLresponse) throws Exception {
        Document xmlDoc = loadXMLString(XMLresponse);
        NodeList nodeList = xmlDoc.getElementsByTagName("*");
        LinkedList<String> fieldName = new LinkedList<>();
        List<String> dataType = new LinkedList<>();
        List<String> repText = new LinkedList<>();
        List<String> domName = new LinkedList<>();
        List<String> leng = new LinkedList<>();
        List<String> outputLen = new LinkedList<>();
        List<String> decimals = new LinkedList<>();
        StringBuilder zdata;
        String zdataTemp;
        String value;
        boolean flag = false;
        boolean flagFE = false;
        int lengthTab = 0;

        // loop of all dataMap (tags) in the XML response
        for (int tag = 0; tag < nodeList.getLength(); tag++) {
            Element element = (Element) nodeList.item(tag);

            //condition: when tag equals "item" and it's closing tag then....
            if (element.getTagName().equals("item") && flag) {
                flag = false;
            }

            // condition: when tag equals "item" and it's opening tag then....
            if (flag) {
                if (element.getTagName().equals("WA") || element.getTagName().equals("ZDATA")) {
                    if (element.getFirstChild().getNodeValue() != null) {
                        if (!flagFE) {
                            flagFE = true;
                            for (String name : fieldName) {
                                dataMap.put(name, new LinkedList<>());
                            }
                        }
                        // the line, which contains values must have the same length as sum of all chars of fileds in
                        // table.
                        zdata = new StringBuilder(element.getFirstChild().getNodeValue());
                        if (zdata.length() < lengthTab) {
                            int dif = lengthTab - zdata.length();
                            for (int i = 0; i < dif; i++) {
                                zdata.append(" ");
                            }
                        }
                        for (String val : fieldName) {
                            zdataTemp = zdata.substring(0, Integer.parseInt(leng.get(fieldName.indexOf(val))));
                            zdata = new StringBuilder(zdata.substring(Integer.parseInt
                                    (leng.get(fieldName.indexOf(val)))));
                            dataMap.get(val).add(zdataTemp);
                        }
                    }
                } else {
                    // a little treak in ABAP and JAVA with null values of attributes inside the xml response
                    value = element.getFirstChild().getNodeValue();
                    if (value.equals("!@#$%^&")) {
                        value = " ";
                    }

                    //add values of tag in tag lists
                    switch (element.getTagName()) {
                        case "FIELDNAME":
                            fieldName.addLast(value);
                            break;
                        case "DATATYPE":
                            dataType.add(value);
                            break;
                        case "REPTEXT":
                            repText.add(value);
                            break;
                        case "DOMNAME":
                            domName.add(value);
                            break;
                        case "LENG":
                            leng.add(value);
                            // get sum of all fileds length in table
                            lengthTab += Integer.parseInt(value);
                            break;
                        case "OUTPUTLEN":
                            outputLen.add(value);
                            break;
                        case "DECIMALS":
                            decimals.add(value);
                            break;
                        default:
                            break;
                    }
                }
            }
            // set flag=true  (each iteration of output string splitted by tag "item") for splitting strings.
            // the are exist open tag and close tag. This one is open tag
            if (element.getTagName().equals("item")) {
                flag = true;
            }
        }
        columnLeng = leng;
        return dataMap;
    }

    @SuppressWarnings({"all"})
    //get Map with name of fields as key and List of values as val by xml response and field butxt.
    private Map<String, List<String>> getResponse(String xmlresponse, String butxt) throws Exception {
        Document xmlDoc = loadXMLString(xmlresponse);
        NodeList nodeList = xmlDoc.getElementsByTagName("*");
        LinkedList<String> fieldName = new LinkedList<>();
        List<String> dataType = new LinkedList<>();
        List<String> repText = new LinkedList<>();
        List<String> domName = new LinkedList<>();
        List<String> leng = new LinkedList<>();
        List<String> outputLen = new LinkedList<>();
        List<String> decimals = new LinkedList<>();
        StringBuilder zdata;
        String zdataTemp;
        String value;
        boolean flag = false;
        boolean flagFE = false;
        int lengthTab = 0;

        // loop of all dataMap (tags) in the XML response
        for (int tag = 0; tag < nodeList.getLength(); tag++) {
            Element element = (Element) nodeList.item(tag);

            //condition: when tag equals "item" and it's closing tag then....
            if (element.getTagName().equals("item") && flag) {
                flag = false;
            }

            // condition: when tag equals "item" and it's opening tag then....
            if (flag) {
                if (element.getTagName().equals("WA") || element.getTagName().equals("ZDATA")) {
                    if (element.getFirstChild().getNodeValue() != null) {
                        if (!flagFE) {
                            flagFE = true;
                            for (String name : fieldName) {
                                dataMap.put(name, new LinkedList<>());
                            }
                        }
                        // the line, which contains values must have the same length as sum of all chars of fileds in
                        // table.
                        zdata = new StringBuilder(element.getFirstChild().getNodeValue());
                        if (zdata.toString().contains(butxt)) {
                            if (zdata.length() < lengthTab) {
                                int dif = lengthTab - zdata.length();
                                for (int i = 0; i < dif; i++) {
                                    zdata.append(" ");
                                }
                            }
                            for (String val : fieldName) {
                                zdataTemp = zdata.substring(0, Integer.parseInt(leng.get(fieldName.indexOf(val))));
                                zdata = new StringBuilder(zdata.substring(Integer.parseInt
                                        (leng.get(fieldName.indexOf(val)))));
                                dataMap.get(val).add(zdataTemp);
                            }
                        }
                    }
                } else {
                    // a little treak in ABAP and JAVA with null values of attributes inside the xml response
                    value = element.getFirstChild().getNodeValue();
                    if (value.equals("!@#$%^&")) {
                        value = " ";
                    }

                    //add values of tag in tag lists
                    switch (element.getTagName()) {
                        case "FIELDNAME":
                            fieldName.addLast(value);
                            break;
                        case "DATATYPE":
                            dataType.add(value);
                            break;
                        case "REPTEXT":
                            repText.add(value);
                            break;
                        case "DOMNAME":
                            domName.add(value);
                            break;
                        case "LENG":
                            leng.add(value);
                            // get sum of all fileds length in table
                            lengthTab += Integer.parseInt(value);
                            break;
                        case "OUTPUTLEN":
                            outputLen.add(value);
                            break;
                        case "DECIMALS":
                            decimals.add(value);
                            break;
                        default:
                            break;
                    }
                }
            }
            // set flag=true  (each iteration of output string splitted by tag "item") for splitting strings.
            // the are exist open tag and close tag. This one is open tag
            if (element.getTagName().equals("item")) {
                flag = true;
            }
        }
        columnLeng = leng;
        return dataMap;
    }
}
