package WebServiceApplication;

public class WebServiceUI {

    public static void main(String[] args) {
        XMLresponse xml = new XMLresponse("T001","2","R");
        WebData webData = new WebData();
        try {
            webData.getResponse(xml.getXMLresponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
