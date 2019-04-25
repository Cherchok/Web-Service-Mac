package UI;

import javax.swing.*;

public class TableGUI {
    // Наименование таблицы
    static String p1    = "MARA";
    // Количество строк "200"
    static String p2    = "10" ;
    // Язык "R"
    static String p3    = "" ;
    // Условие выборки "MATNR >= '000000000100000029' AND MATNR <= '000000000300000042'"
    static String p4    = "MATNR >= '000000000100000029' AND MATNR <= '000000000300000042'";
    // Сортировка "<AENAM MATKL ERNAM BISMT bbbbb LAEDA VPSTA LVORM MATNR MTART"
    static String p5    = "<AENAM MATKL ERNAM BISMT bbbbb LAEDA VPSTA LVORM MATNR MTART";
    // Группировка-агрегирование
    static String p6    = "" ;
    // Список полей "MATKL ERNAM BISMT KKKKK LAEDA AENAM VPSTA GGGG LVORM MATNR MTART"
    static String p7    = "" ;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TableFrame frame = new TableFrame(p1,p2,p3,p4,p5,p6,p7);
                // returns GUI table with SAP data
                frame.getFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // returns Map with SAP data
            TableFrame jFrame = new TableFrame(p1,p2,p3,p4,p5,p6,p7);
            System.out.println(jFrame.getData());

        });
    }
}
