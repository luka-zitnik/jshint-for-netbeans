package lukazitnik.jshint.annotations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import lukazitnik.jshint.JSHintError;
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
                    addDocumentListenerAndAnnotations(focusedDocument);
                }
                break;
            case EditorRegistry.COMPONENT_REMOVED_PROPERTY:

                // Clean up history
                List<NbEditorDocument> openDocuments = getOpenDocuments();
                Iterator<NbEditorDocument> it = history.iterator();
                while (it.hasNext()) {
                    NbEditorDocument historicalDocument = it.next();
                    if (!openDocuments.contains(historicalDocument)) {
                        it.remove();
                    }
                }
                break;
        }
    }

    public void addDocumentListeners() {
        List<NbEditorDocument> openDocuments = getOpenDocuments();
        for (NbEditorDocument d : openDocuments) {
            addDocumentListenerAndAnnotations(d);
        }
    }

    public void removeDocumentListeners() {
        Iterator<NbEditorDocument> it = history.iterator();
        while (it.hasNext()) {
            NbEditorDocument historicalDocument = it.next();
            removeDocumentListenerAndAnnotations(historicalDocument);
            it.remove();
        }
    }

    private List<NbEditorDocument> getOpenDocuments() {
        List<NbEditorDocument> openDocuments = new ArrayList<>();
        for (JTextComponent component : EditorRegistry.componentList()) {
            openDocuments.add((NbEditorDocument) component.getDocument());
        }
        return openDocuments;
    }

    private void addDocumentListenerAndAnnotations(NbEditorDocument d) {
        JSHintDocumentListener dl = new JSHintDocumentListener();
        dl.updateAnnotations(d);
        d.addDocumentListener(dl);
    }

    private void removeDocumentListenerAndAnnotations(NbEditorDocument d) {
        JSHintDocumentListener[] listeners = d.getListeners(JSHintDocumentListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            d.removeDocumentListener(listeners[i]); // Doesn't work!
        }
        SwingUtilities.invokeLater(new Annotator(d, new LinkedList<JSHintError>()));
    }

}
