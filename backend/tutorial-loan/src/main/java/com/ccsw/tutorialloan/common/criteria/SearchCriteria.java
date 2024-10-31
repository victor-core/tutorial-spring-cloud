package com.ccsw.tutorialloan.common.criteria;

import java.time.LocalDate;

public class SearchCriteria {

    private String key;
    private String operation;
    private Object value;

    public SearchCriteria(String key, String operation, Object value) {

        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SearchCriteria(Long gameId, String operation, LocalDate[] dates) {
        this.key = gameId != null ? gameId.toString() : null;
        this.operation = operation;
        this.value = dates;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
