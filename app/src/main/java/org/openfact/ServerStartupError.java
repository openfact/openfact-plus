package org.openfact;

public class ServerStartupError extends Error {

    private final boolean fillStackTrace;

    public ServerStartupError(String message) {
        super(message);
        fillStackTrace = true;
    }

    public ServerStartupError(String message, boolean fillStackTrace) {
        super(message);
        this.fillStackTrace = fillStackTrace;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (fillStackTrace) {
            return super.fillInStackTrace();
        } else {
            return this;
        }
    }

}
