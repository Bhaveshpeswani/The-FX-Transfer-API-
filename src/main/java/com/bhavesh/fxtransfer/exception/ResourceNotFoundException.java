package com.bhavesh.fxtransfer.exception;

import javax.management.RuntimeMBeanException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
