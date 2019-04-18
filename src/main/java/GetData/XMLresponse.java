package GetData;

import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XMLresponse {
    private String arg1;
    private String arg2;
    private String arg3;

    public XMLresponse(String table, String fieldsQuan, String language) {
        this.arg1 = table;
        this.arg2 = fieldsQuan;
        this.arg3 = language;
    }

    public String getXMLresponse() {
        String xmlResponse = null;
        String username = "K.GACHECHILA";
        String password = "Welcome1!";
        String urnName = "urn";
        String urn = "ZTABLEREAD";
        String uri = "urn:sap-com:document:sap:rfc:functions";
        String destination = "http://support.alpeconsulting.com:8201/sap/bc/srt/rfc/sap/z_table_read_web_service/100/" +
                "z_table_read_web_service/z_table_read_web_service";
        // create XML request , get connection, get XML response as a string/
        try {
            // First create the connection
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();

            // Next, create the actual message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();

            //begin authorization
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
            // enter params for connection
            SOAPElement bodyElement = body.addChildElement(envelope.createName("urn:" + urn));
            // Add parameters
            bodyElement.addChildElement("LANG").addTextNode(arg3);
            bodyElement.addChildElement("FIELDSQUAN").addTextNode(arg2);
            bodyElement.addChildElement("TABLENAME").addTextNode(arg1);

            // Save the message
            message.saveChanges();

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
            xmlResponse = sw.toString();

            // Close the connection
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return xmlResponse;
    }
}
