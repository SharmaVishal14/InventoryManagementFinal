package com.cts.inventorymanagement.purchase.exceptions;
public class PurchaseOrderNotFoundException extends RuntimeException {
    public PurchaseOrderNotFoundException(String message) { super(message); }
}
