package lukazitnik.jshint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.modules.ModuleInstall;
import org.openide.text.Line;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        PropertyChangeListener pcl = new PropertyChangeListener() {

            class JSHintAnnotator implements DocumentListener {

                private final LinkedList<JSHintAnnotation> attachedAnnotations = new LinkedList<>();
                private final NbEditorDocument d;

                public JSHintAnnotator(NbEditorDocument d) {
                    this.d = d;
                }

                @Override
                public void insertUpdate(DocumentEvent de) {
                    updateAnnotations();
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    updateAnnotations();
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    updateAnnotations();
                }

                private void updateAnnotations() {
                    Thread thread = new Thread() {

                        @Override
                        public void run() {
                            JSHint jshint = JSHint.instance;
                            final List<JSHintError> errors = jshint.lint(d);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    detachAnnotations();
                                    attachAnnotations(errors);
                                }
                            });
                        }

                        private void detachAnnotations() {
                            for (JSHintAnnotation annotation : attachedAnnotations) {
                                annotation.detach();
                            }
                            attachedAnnotations.clear();
                        }

                        private void attachAnnotations(List<JSHintError> errors) {
                            for (JSHintError error : errors) {

                                // Line indexes start from 0, while line numbers start from 1
                                Integer offset = Utilities.getRowStartFromLineOffset(d, error.getLine() - 1);

                                Line line = NbEditorUtilities.getLine(d, offset, false);
                                JSHintAnnotation annotation = new JSHintAnnotation(error);

                                annotation.attach(line);
                                attachedAnnotations.add(annotation);
                            }
                        }
                    };

                    thread.start();
                }
            }

            HashMap<NbEditorDocument, JSHintAnnotator> history = new HashMap<>();

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

                JSHintAnnotator annotator;

                switch (evt.getPropertyName()) {
                    case EditorRegistry.FOCUS_GAINED_PROPERTY:
                        annotator = new JSHintAnnotator(focusedDocument);
                        if (!history.containsKey(focusedDocument)) {
                            history.put(focusedDocument, annotator);
                            annotator.updateAnnotations();
                        }
                        focusedDocument.addDocumentListener(annotator);
                        break;
                    case EditorRegistry.FOCUS_LOST_PROPERTY:
                        annotator = history.get(focusedDocument);
                        focusedDocument.removeDocumentListener(annotator);
                        break;
                    case EditorRegistry.COMPONENT_REMOVED_PROPERTY:
                        List<NbEditorDocument> openedDocuments = new ArrayList<>();
                        for (JTextComponent component : EditorRegistry.componentList()) {
                            openedDocuments.add((NbEditorDocument) component.getDocument());
                        }
                        Iterator<NbEditorDocument> it = history.keySet().iterator();
                        while (it.hasNext()) {
                            NbEditorDocument historicalDocument = it.next();
                            if (!openedDocuments.contains(historicalDocument)) {
                                annotator = history.get(historicalDocument);
                                annotator.updateAnnotations();
                                historicalDocument.removeDocumentListener(annotator);
                                it.remove();
                            }
                        }
                        break;
                }
            }
        };

        EditorRegistry.addPropertyChangeListener(pcl);
    }

}
