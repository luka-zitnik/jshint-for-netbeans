package lukazitnik.jshint;

import annotations.EditorRegistryListener;
import java.beans.PropertyChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import lukazitnik.jshint.options.JSHintPanel;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    final PropertyChangeListener pcl = new EditorRegistryListener();
    final Preferences p = NbPreferences.forModule(JSHintPanel.class);

    @Override
    public void restored() {
        JSHint jshint = JSHint.instance; // Prevent possible later interruption
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
            EditorRegistry.addPropertyChangeListener(pcl);
        } else {
            EditorRegistry.removePropertyChangeListener(pcl);
        }
    }

}
