package org.messtin.sequence.exception;

public class SequenceException extends RuntimeException {

    public SequenceException() {

    }

    public SequenceException(String message) {
        super(message);
    }

    public SequenceException(Throwable throwable) {
        super(throwable);
    }

}
