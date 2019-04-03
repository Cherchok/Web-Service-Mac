package UI;

import WebServiceApplication.WebData;
import WebServiceApplication.XMLresponse;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataTableModel extends AbstractTableModel {
    private Map<String, List<String>> values;
    private List<String> fields;
    String[] columns;


    DataTableModel(String table, String fieldsQuan, String language) {
        XMLresponse xml = new XMLresponse(table, fieldsQuan, language);
        WebData webData = new WebData();
        try {
            this.values = webData.getResponse(xml.getXMLresponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        fields = webData.fields;

        columns = new String[fields.size()];
        columns = fields.toArray(new String[0]);
    }

    public Map<String, List<String>> getValues() {
        return values;
    }

    public List<String> getFields() {
        return fields;
    }

    @Override
    public String getColumnName(int index) {
        return columns[index];
    }

    @Override
    public int getRowCount() {
        int counter = 0;
        for (String key : values.keySet()) {
            for (int i = 0; i < values.get(key).size(); i++) {
                counter++;
            }
            if (key != null) {
                break;
            }
        }
        return counter;
    }

    @Override
    public int getColumnCount() {
        return fields.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
}
