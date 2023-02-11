package ch.loewe.normal_use_client.fabricclient;

import javax.swing.*;
import java.io.File;

public class test {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null,"Please Select the File");
        jfc.setVisible(true);
        File filename = jfc.getSelectedFile();
        System.out.println("File name "+filename.getName());
    }
}
