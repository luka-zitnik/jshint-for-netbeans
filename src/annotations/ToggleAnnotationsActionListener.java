package annotations;

import java.awt.event.ActionEvent;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import lukazitnik.jshint.options.JSHintPanel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbPreferences;

@ActionID(category = "Edit", id = "lukazitnik.jshint.ToggleAnnotationsActionListener")
@ActionRegistration(lazy = false, displayName = "NOT-USED")
@ActionReference(path = "Editors/text/javascript/Popup")
public class ToggleAnnotationsActionListener extends AbstractAction implements PreferenceChangeListener {

    private final Preferences p = NbPreferences.forModule(JSHintPanel.class);

    ToggleAnnotationsActionListener() {
        p.addPreferenceChangeListener(this);
        refresh();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean annotationsOn = p.getBoolean("show.annotations", true);
        p.putBoolean("show.annotations", !annotationsOn);
    }

    private void refresh() {
        boolean annotationsOn = p.getBoolean("show.annotations", true);
        String name = annotationsOn ? "Hide JSHint Annotations" : "Show JSHint Annotations";
        putValue(javax.swing.Action.NAME, name);
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent pce) {
        if (pce.getKey().equals("show.annotations")) {
            refresh();
        }
    }

}
