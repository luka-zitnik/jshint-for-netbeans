package lukazitnik.jshint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        PropertyChangeListener pcl = new PropertyChangeListener() {

            class JSHintAnnotator implements DocumentListener {

                @Override
                public void insertUpdate(DocumentEvent de) {
                    updateAnnotations((NbEditorDocument) de.getDocument());
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    updateAnnotations((NbEditorDocument) de.getDocument());
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    updateAnnotations((NbEditorDocument) de.getDocument());
                }

                private void updateAnnotations(final NbEditorDocument d) {
                    Thread thread = new Thread() {

                        @Override
                        public void run() {
                            String fileName = NbEditorUtilities.getFileObject(d).getNameExt();
                            ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Linting " + fileName);
                            progressHandle.start();

                            JSHint jshint = JSHint.instance;
                            final List<JSHintError> errors = jshint.lint(d);

                            progressHandle.finish();

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    removeAnnotations();
                                    addAnnotations(errors);
                                }
                            });
                        }

                        private void removeAnnotations() {
                            Annotations annotations = d.getAnnotations();

                            // getNextLineWithAnnotation seems to return its argument
                            // as long as there are annotations on that line
                            for (int line = 0; annotations.getNextLineWithAnnotation(line) != -1;) {
                                AnnotationDesc annotationDesc = annotations.getAnnotation(line, "lukazitnik-jshint-jshintannotation");
                                if (annotationDesc == null) {
                                    // The only way to get to the next line with annotations
                                    // while leaving some annotations on the current line
                                    // is to go to the next line, with or without annotations
                                    ++line;
                                    continue;
                                }

                                annotations.removeAnnotation(annotationDesc);
                            }
                        }

                        private void addAnnotations(List<JSHintError> errors) {
                            for (JSHintError error : errors) {

                                // Line indexes start from 0, while line numbers start from 1
                                Integer offset = Utilities.getRowStartFromLineOffset(d, error.getLine() - 1);

                                JSHintAnnotation annotation = new JSHintAnnotation(error);

                                try {
                                    d.addAnnotation(d.createPosition(offset), 0, annotation);
                                } catch (BadLocationException ex) {
                                    // Document changed in the meantime. Do nothing.
                                    // Another update to annotations should follow.
                                }
                            }
                        }
                    };

                    thread.start();
                }
            }

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
                            annotator.updateAnnotations(focusedDocument);
                        }
                        focusedDocument.addDocumentListener(annotator);
                        break;
                    case EditorRegistry.FOCUS_LOST_PROPERTY:
                        focusedDocument.removeDocumentListener(annotator);
                        break;
                    case EditorRegistry.COMPONENT_REMOVED_PROPERTY:
                        List<NbEditorDocument> openedDocuments = new ArrayList<>();
                        for (JTextComponent component : EditorRegistry.componentList()) {
                            openedDocuments.add((NbEditorDocument) component.getDocument());
                        }
                        Iterator<NbEditorDocument> it = history.iterator();
                        while (it.hasNext()) {
                            NbEditorDocument historicalDocument = it.next();
                            if (!openedDocuments.contains(historicalDocument)) {
                                annotator.updateAnnotations(historicalDocument);
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
