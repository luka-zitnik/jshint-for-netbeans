package lukazitnik.jshint.annotations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import lukazitnik.jshint.JSHintError;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.editor.NbEditorDocument;

public class EditorRegistryListener implements PropertyChangeListener {

    Map<NbEditorDocument, JSHintDocumentListener> history = new HashMap<>();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        JTextComponent jtc = EditorRegistry.lastFocusedComponent();

        if (jtc == null) {
            return;
        }

        final NbEditorDocument focusedDocument = (NbEditorDocument) jtc.getDocument();
        String mimeType = (String) focusedDocument.getProperty(NbEditorDocument.MIME_TYPE_PROP);

        if (!mimeType.equals("text/javascript")) {
            return;
        }

        switch (evt.getPropertyName()) {
            case EditorRegistry.FOCUS_GAINED_PROPERTY:
                if (!history.keySet().contains(focusedDocument)) {

                    // The file has just been opened, so ...
                    addDocumentListenerAndAnnotations(focusedDocument);
                }
                break;
            case EditorRegistry.COMPONENT_REMOVED_PROPERTY:

                // Clean up history
                List<NbEditorDocument> openDocuments = getOpenJSDocuments();
                Iterator<NbEditorDocument> it = history.keySet().iterator();
                while (it.hasNext()) {
                    NbEditorDocument historicalDocument = it.next();
                    if (!openDocuments.contains(historicalDocument)) {

                        // Don't bother calling removeDocumentListenerAndAnnotations()
                        it.remove();
                    }
                }
                break;
        }
    }

    public void updateOpenDocuments() {
        List<NbEditorDocument> openDocuments = getOpenJSDocuments();
        for (NbEditorDocument d : openDocuments) {
            addDocumentListenerAndAnnotations(d);
        }
    }

    public void updateHistoryDocuments() {
        Iterator<NbEditorDocument> it = history.keySet().iterator();
        while (it.hasNext()) {
            NbEditorDocument d = it.next();
            removeDocumentListenerAndAnnotations(d);
            it.remove();
        }
    }

    private List<NbEditorDocument> getOpenJSDocuments() {
        List<NbEditorDocument> openDocuments = new ArrayList<>();
        for (JTextComponent component : EditorRegistry.componentList()) {
            NbEditorDocument d = (NbEditorDocument) component.getDocument();
            String mimeType = (String) d.getProperty(NbEditorDocument.MIME_TYPE_PROP);
            if (mimeType.equals("text/javascript")) {
                openDocuments.add(d);
            }
        }
        return openDocuments;
    }

    private void addDocumentListenerAndAnnotations(NbEditorDocument d) {
        JSHintDocumentListener dl = new JSHintDocumentListener();
        dl.updateAnnotations(d);
        d.addDocumentListener(dl);
        history.put(d, dl);
    }

    private void removeDocumentListenerAndAnnotations(NbEditorDocument d) {
        SwingUtilities.invokeLater(new Annotator(d, new LinkedList<JSHintError>()));
        d.removeDocumentListener(history.get(d));
    }

}
