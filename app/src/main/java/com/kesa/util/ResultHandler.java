package com.kesa.util;

/**
 * An interface responsible for handling an asynchronous task.
 *
 * @author hongil
 */
public interface ResultHandler {
    void onComplete();
    void onError(Exception exception);
}
