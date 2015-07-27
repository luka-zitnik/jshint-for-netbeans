package lukazitnik.jshint;

import java.io.IOException;
import lukazitnik.jshint.annotations.EditorRegistryListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import lukazitnik.jshint.options.JSHintPanel;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    final EditorRegistryListener ecl = new EditorRegistryListener();
    final Preferences p = NbPreferences.forModule(JSHintPanel.class);

    @Override
    public void restored() {

        // If an instance of JSHint can't be created, adding listeners makes no sense.
        try {
            JSHint jshint = JSHint.getInstance(); // Also prevents possible later interruption
        } catch (IOException ex) {

            // JSHintPanel should never let this happen.
            return;
        }

        p.addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent pce) {
                if (pce.getKey().equals("show.annotations")) {
                    updateChangeListenersOnEditorRegistry();
                }
            }
        });
        updateChangeListenersOnEditorRegistry();
    }

    private void updateChangeListenersOnEditorRegistry() {
        boolean annotationsOn = p.getBoolean("show.annotations", true);
        if (annotationsOn) {
            EditorRegistry.addPropertyChangeListener(ecl);
            ecl.updateOpenDocuments();
        } else {
            EditorRegistry.removePropertyChangeListener(ecl);
            ecl.updateHistoryDocuments();
        }
    }

}
