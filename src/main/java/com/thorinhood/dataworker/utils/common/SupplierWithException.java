package com.thorinhood.dataworker.utils.common;

public interface SupplierWithException<T> {

    T get() throws Exception;

}
