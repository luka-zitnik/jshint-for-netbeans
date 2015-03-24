package lukazitnik.jshint;

import org.mozilla.javascript.NativeObject;

public class JSHintError {

    private final String reason;
    private final Integer line;

    public JSHintError(NativeObject error) {
        reason = error.get("reason", error).toString();
        line = ((Number) error.get("line", error)).intValue();
    }

    public String getReason() {
        return reason;
    }

    public Integer getLine() {
        return line;
    }
}
