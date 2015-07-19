package annotations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;

public class EditorRegistryListener implements PropertyChangeListener {

    List<NbEditorDocument> history = new ArrayList<>();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        JTextComponent jtc = EditorRegistry.lastFocusedComponent();

        if (jtc == null) {
            return;
        }

        final NbEditorDocument focusedDocument = (NbEditorDocument) jtc.getDocument();

        if (!NbEditorUtilities.getFileObject(focusedDocument).getMIMEType().equals("text/javascript")) {
            return;
        }

        switch (evt.getPropertyName()) {
            case EditorRegistry.FOCUS_GAINED_PROPERTY:
                if (!history.contains(focusedDocument)) {
                    history.add(focusedDocument);

                    // The file has just been opened, so ...
                    JSHintDocumentListener dl = new JSHintDocumentListener();
                    dl.updateAnnotations(focusedDocument);
                    focusedDocument.addDocumentListener(dl);
                }
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
                        it.remove();
                    }
                }
                break;
        }
    }
}
