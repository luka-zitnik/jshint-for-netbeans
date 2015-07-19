package lukazitnik.jshint.annotations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import lukazitnik.jshint.JSHintError;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorDocument;

public class Annotator implements Runnable {

    private final NbEditorDocument d;
    List<JSHintError> errors;

    public Annotator(NbEditorDocument d, List<JSHintError> errors) {
        this.d = d;
        this.errors = errors;
    }

    @Override
    public void run() {
        removeAnnotations();
        addAnnotations(errors);
    }

    private void removeAnnotations() {
        Annotations annotations = d.getAnnotations();

        // getNextLineWithAnnotation seems to return its argument
        // as long as there are annotations on that line.
        for (int line = annotations.getNextLineWithAnnotation(0); line != -1;) {
            AnnotationDesc annotationDesc = annotations.getAnnotation(line, "lukazitnik-jshint-jshintannotation");

            if (annotationDesc == null) {
                // Increment line index. We are done with this line.
                line = annotations.getNextLineWithAnnotation(++line);
                continue;
            }

            annotations.removeAnnotation(annotationDesc);
        }
    }

    private void addAnnotations(List<JSHintError> errors) {
        Map<Integer, List<JSHintError>> chunks = splitIntoChunks(errors);

        for (Integer line : chunks.keySet()) {

            // Line indexes start from 0, while line numbers start from 1
            Integer offset = Utilities.getRowStartFromLineOffset(d, line - 1);

            JSHintAnnotation annotation = new JSHintAnnotation(chunks.get(line));

            try {
                d.addAnnotation(d.createPosition(offset), 0, annotation);
            } catch (BadLocationException ex) {
                // Document changed in the meantime. Do nothing.
                // Another update to annotations should follow.
            }
        }
    }

    private Map<Integer, List<JSHintError>> splitIntoChunks(List<JSHintError> errors) {
        Map<Integer, List<JSHintError>> chunks = new HashMap<>();

        for (JSHintError error : errors) {

            if (chunks.containsKey(error.getLine())) {
                chunks.get(error.getLine()).add(error);
            } else {
                List<JSHintError> newChunk = new LinkedList<>();
                newChunk.add(error);
                chunks.put(error.getLine(), newChunk);
            }
        }

        return chunks;
    }
}
