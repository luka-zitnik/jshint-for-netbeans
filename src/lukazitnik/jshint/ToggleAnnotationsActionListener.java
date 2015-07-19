package lukazitnik.jshint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import lukazitnik.jshint.options.JSHintPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbPreferences;

@ActionID(category = "Edit", id = "lukazitnik.jshint.ToggleAnnotationsActionListener")
@ActionRegistration(displayName = "Toggle JSHint Annotations")
@ActionReference(path = "Editors/text/javascript/Popup")
public class ToggleAnnotationsActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Preferences p = NbPreferences.forModule(JSHintPanel.class);
        boolean annotationsOn = p.getBoolean("show.annotations", true);
        p.putBoolean("show.annotations", !annotationsOn);
    }

}
