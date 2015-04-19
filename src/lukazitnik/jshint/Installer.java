package lukazitnik.jshint;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
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

            private final DocumentListener dl = new DocumentListener() {

                private final LinkedList<JSHintAnnotation> attachedAnnotations = new LinkedList<>();

                @Override
                public void insertUpdate(DocumentEvent de) {
                    handleUpdate(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    handleUpdate(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    handleUpdate(de);
                }

                private void handleUpdate(DocumentEvent de) {
                    NbEditorDocument d = (NbEditorDocument) de.getDocument();
                    detachAnnotations(d);
                    attachAnnotations(d);
                }

                private void detachAnnotations(NbEditorDocument d) {
                    for (JSHintAnnotation annotation : attachedAnnotations) {
                        annotation.detach();
                    }
                }

                private void attachAnnotations(NbEditorDocument d) {
                    JSHint jshint = JSHint.instance;

                    for (JSHintError error : jshint.lint(d)) {

                        // Line indexes start from 0, while line numbers start from 1
                        Integer offset = Utilities.getRowStartFromLineOffset(d, error.getLine() - 1);

                        Line line = NbEditorUtilities.getLine(d, offset, false);
                        JSHintAnnotation annotation = new JSHintAnnotation(error);

                        annotation.attach(line);
                        attachedAnnotations.add(annotation);
                    }
                }
            };

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                JTextComponent jtc = EditorRegistry.lastFocusedComponent();

                if (jtc == null) {
                    return;
                }

                Document d = jtc.getDocument();

                switch (evt.getPropertyName()) {
                    case EditorRegistry.FOCUS_GAINED_PROPERTY:
                        d.addDocumentListener(dl);
                        break;
                    case EditorRegistry.FOCUS_LOST_PROPERTY:
                        d.removeDocumentListener(dl);
                        break;
                }
            }
        };

        EditorRegistry.addPropertyChangeListener(pcl);
    }

}
