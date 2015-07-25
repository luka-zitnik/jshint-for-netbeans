package lukazitnik.jshint.options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JTextField;

class JSFileVersionDeducer {

    String deduce(JTextField jSFileTextField) {
        File file = new File(jSFileTextField.getText());

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String firstLine = in.readLine();

            if (firstLine.matches("^\\/\\*!\\s+[\\d.]+\\s+\\*\\/$")) {
                return firstLine.substring(3, firstLine.length() - 2).trim();
            }

        } catch (Exception ex) {
            return "Unknown version";
        }

        return "Unknown version";
    }

}
