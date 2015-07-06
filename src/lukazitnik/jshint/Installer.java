package lukazitnik.jshint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        PropertyChangeListener pcl = new PropertyChangeListener() {

            List<NbEditorDocument> history = new ArrayList<>();
            JSHintAnnotator annotator = new JSHintAnnotator();

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JTextComponent jtc = EditorRegistry.lastFocusedComponent();

                if (jtc == null) {
                    return;
                }

                NbEditorDocument focusedDocument = (NbEditorDocument) jtc.getDocument();

                if (!NbEditorUtilities.getFileObject(focusedDocument).getMIMEType().equals("text/javascript")) {
                    return;
                }

                switch (evt.getPropertyName()) {
                    case EditorRegistry.FOCUS_GAINED_PROPERTY:
                        if (!history.contains(focusedDocument)) {
                            history.add(focusedDocument);

                            // The file has just been opened, so ...
                            annotator.updateAnnotations(focusedDocument);
                        }
                        focusedDocument.addDocumentListener(annotator);
                        break;
                    case EditorRegistry.FOCUS_LOST_PROPERTY:
                        focusedDocument.removeDocumentListener(annotator);
                        break;
                    case EditorRegistry.COMPONENT_REMOVED_PROPERTY:

                        // Clean up history
                        List<NbEditorDocument> openedDocuments = new ArrayList<>();
                        for (JTextComponent component : EditorRegistry.componentList()) {
                            openedDocuments.add((NbEditorDocument) component.getDocument());
                        }
                        Iterator<NbEditorDocument> it = history.iterator();
                        while (it.hasNext()) {
                            NbEditorDocument historicalDocument = it.next();
                            if (!openedDocuments.contains(historicalDocument)) {
                                historicalDocument.removeDocumentListener(annotator);
                                it.remove();
                            }
                        }
                        break;
                }
            }
        };

        JSHint jshint = JSHint.instance; // Prevent possible later interruption
        EditorRegistry.addPropertyChangeListener(pcl);
    }

}
