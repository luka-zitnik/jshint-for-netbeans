package lukazitnik.jshint;

import annotations.EditorListener;
import java.beans.PropertyChangeListener;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        JSHint jshint = JSHint.instance; // Prevent possible later interruption
        PropertyChangeListener pcl = new EditorListener();
        EditorRegistry.addPropertyChangeListener(pcl);
    }

}
