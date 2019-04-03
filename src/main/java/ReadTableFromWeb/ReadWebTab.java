package ReadTableFromWeb;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;


public class ReadWebTab {


    public static void main(String[] args) {

        // enter params for connection
        String arg1 = "T001";
        String arg2 = "200";
        String arg3 = "R";


        String urn = "ZTABLEREAD";
        String urnName = "urn";
        String uri = "urn:sap-com:document:sap:rfc:functions";
        String destination = "http://support.alpeconsulting.com:8201/sap/bc/srt/rfc/sap/z_table_read_web_service/100/z_table_read_web_service/z_table_read_web_service";

        // create XML request , get connection, get XML response as a string/
        try {
            // First create the connection
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();

            // Next, create the actual message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();


            //begin authorization
//            String username = "K.GACHECHILA";
//            String password = "Welcome1!";
//            String authorization = new sun.misc.BASE64Encoder().encode((username + ":" + password).getBytes());
//            MimeHeaders hd = message.getMimeHeaders();
//            hd.addHeader("Authorization", "Basic " + authorization);
            //end authorization

            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(urnName, uri);

            // Create and populate the body
            SOAPBody body = envelope.getBody();
            // Create the main element and namespace
            SOAPElement bodyElement = body.addChildElement(envelope.createName("urn:" + urn));
            // Add parameters
            bodyElement.addChildElement("LANG").addTextNode(arg3);
            bodyElement.addChildElement("FIELDSQUAN").addTextNode(arg2);
            bodyElement.addChildElement("TABLENAME").addTextNode(arg1);

            // Save the message
            message.saveChanges();

            // Check the input
//            System.out.println("\nRequest:\n");
//            message.writeTo(System.out);
//            System.out.println();

            // Send the message and get the reply
            SOAPMessage reply = connection.call(message, destination);

            //get response as a string
            final StringWriter sw = new StringWriter();
            try {
                TransformerFactory.newInstance().newTransformer().transform(
                        new DOMSource(reply.getSOAPPart()),
                        new StreamResult(sw));
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }

            // Now you have the XML as a String:
            String XMLresponse = sw.toString();
            System.out.println("XML response:");
            System.out.println(XMLresponse);
            System.out.println(XMLresponse.length());


            //returns response all params
            System.out.println("response by all params: ");
            getResponse(XMLresponse);


            // Close the connection
            connection.close();
        } catch (Exception e) {
            System.out.println("Argument line, zdataLine = " + e.getMessage());
        }
    }

    private static Document loadXMLString(String XMLresponse) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(XMLresponse));

        return db.parse(is);
    }

    private static Map<String, List<String>> getResponse(String XMLresponse) throws Exception {
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
        for (int j = 0; j < nodeList.getLength(); j++) {
            Element element = (Element) nodeList.item(j);
            if (element.getTagName().equals("item") && flag) {
                flag = false;
            }
            // condition, when flag=true
            if (flag) {
                if (element.getTagName().equals("WA") || element.getTagName().equals("ZDATA")) {
                    if (element.getFirstChild().getNodeValue() != null) {
                        if (!flagFE) {
                            flagFE = true;
                            for (String name : fieldName) {
                                nodes.put(name, new LinkedList<>());
                                System.out.println(nodes);
                            }
                        }
                        zdata = new StringBuilder(element.getFirstChild().getNodeValue());
                        if (zdata.length() < lengthTab) {
                            int dif = lengthTab - zdata.length();
                            for (int i = 0; i < dif; i++) {
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
                    value = element.getFirstChild().getNodeValue();
                    if (value.equals("!@#$%^&")) {
                        value = " ";
                    }

                    //add values of tag in tag lists
                    switch (element.getTagName()) {
                        case "FIELDNAME":
                            fieldName.addLast(value);
                            System.out.println(fieldName);
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
            if (element.getTagName().equals("item")) {
                flag = true;
            }
        }
        // output of result(map) in console
        for (String key : nodes.keySet()) {
            System.out.println(key + "=" + nodes.get(key));
        }
        return nodes;
    }
}
