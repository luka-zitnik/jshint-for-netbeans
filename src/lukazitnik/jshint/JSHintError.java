package lukazitnik.jshint;

import sun.org.mozilla.javascript.NativeObject;

/**
 *
 * @author luka
 */
public class JSHintError {

    private final String reason;
    private final Integer line;

    public JSHintError(NativeObject error) {
        reason = (String) error.get("reason", error);
        line = ((Number) error.get("line", error)).intValue();
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return the line
     */
    public Integer getLine() {
        return line;
    }
}