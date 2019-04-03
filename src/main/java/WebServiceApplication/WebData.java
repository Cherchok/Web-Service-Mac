package WebServiceApplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WebData {
    public List<String> fields;

    private static Document loadXMLString(String XMLresponse) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(XMLresponse));

        return db.parse(is);
    }

    public Map<String, List<String>> getResponse(String XMLresponse) throws Exception {
        Map<String, List<String>> nodes = new HashMap<>();
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

        // loop of all nodes(tags) in the XML response
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element x = (Element) nodeList.item(i);
            if (x.getTagName().equals("item") && flag) {
                flag = false;
            }
            // condition, when flag=true
            if (flag) {
                if (x.getTagName().equals("WA") || x.getTagName().equals("ZDATA")) {
                    if (x.getFirstChild().getNodeValue() != null) {
                        if (!flagFE) {
                            flagFE = true;
                            for (String name : fieldName) {
                                nodes.put(name, new LinkedList<>());
                            }
                        }
                        zdata = new StringBuilder(x.getFirstChild().getNodeValue());
                        if (zdata.length() < lengthTab) {
                            int dif = lengthTab - zdata.length();
                            for (int j = 0; j < dif; j++) {
                                zdata.append(" ");
                            }
                        }
                        for (String val : fieldName) {
                            zdataTemp = zdata.substring(0, Integer.parseInt(leng.get(fieldName.indexOf(val))));
                            zdata = new StringBuilder(zdata.substring(Integer.parseInt(leng.get(fieldName.indexOf(val)))));
                            nodes.get(val).add(zdataTemp);
                        }
                    }
                } else {

                    // a little treak in ABAP and JAVA with null values of attributes inside the xml response
                    value = x.getFirstChild().getNodeValue();
                    if (value.equals("!@#$%^&")) {
                        value = " ";
                    }

                    //add values of tag in tag lists
                    switch (x.getTagName()) {
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
            if (x.getTagName().equals("item")) {
                flag = true;
            }
        }
        fields = fieldName;
        // output of result(map) in console
//        for (String key : nodes.keySet()) {
//            System.out.println(key + "=" + nodes.get(key));
//        }
        return nodes;
    }
}
