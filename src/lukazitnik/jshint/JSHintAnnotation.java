package lukazitnik.jshint;

import java.util.List;
import org.openide.text.Annotation;

public final class JSHintAnnotation extends Annotation {

    private String reason;
    static String TYPE = "lukazitnik-jshint-jshintannotation";

    JSHintAnnotation(JSHintError error) {
        reason = error.getReason();
    }

    JSHintAnnotation(List<JSHintError> errors) {
        String eol = System.getProperty("line.separator");
        for (JSHintError error : errors) {
            if (reason == null) {
                reason = error.getReason();
            } else {
                reason += eol + eol + error.getReason();
            }
        }
    }

    @Override
    public String getAnnotationType() {
        return TYPE;
    }

    @Override
    public String getShortDescription() {
        return reason;
    }

}
