package lukazitnik.jshint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;

@ActionID(category = "Edit", id = "lukazitnik.jshint.ToggleAnnotationsActionListener")
@ActionRegistration(displayName = "Toggle JSHint Annotations")
@ActionReference(path = "Editors/text/javascript/Popup")
public class ToggleAnnotationsActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "This will toggle on/off jshint annotations");
    }
    
}
