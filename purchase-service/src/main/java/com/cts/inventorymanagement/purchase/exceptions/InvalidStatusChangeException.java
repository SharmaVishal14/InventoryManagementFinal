package com.cts.inventorymanagement.purchase.exceptions;


public class InvalidStatusChangeException extends RuntimeException {
    public InvalidStatusChangeException(String message) { super(message); }
}