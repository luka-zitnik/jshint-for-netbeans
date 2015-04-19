package lukazitnik.jshint;

import org.openide.text.Annotation;

public final class JSHintAnnotation extends Annotation {

    private final String reason;
    static String TYPE = "lukazitnik-jshint-jshintannotation";

    JSHintAnnotation(JSHintError error) {
        reason = error.getReason();
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
