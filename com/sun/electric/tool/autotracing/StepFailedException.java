/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.autotracing;

/**
 *
 * @author Dmitrii
 */
public class StepFailedException extends Exception {

    public StepFailedException() {
        super();
    }

    public StepFailedException(String message) {
        super(message);
    }

    public StepFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StepFailedException(Throwable cause) {
        super(cause);
    }
}
