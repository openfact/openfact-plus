package org.openfact.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface DocumentLineModel {

    String getId();

    void setAttribute(String name, String value);
    void setAttribute(String name, Integer value);
    void setAttribute(String name, Long value);
    void setAttribute(String name, BigDecimal value);
    void setAttribute(String name, Boolean value);

    void removeAttribute(String name);

    String getAttribute(String name, String defaultValue);
    Long getAttribute(String name, Long defaultValue);
    Integer getAttribute(String name, Integer defaultValue);
    BigDecimal getAttribute(String name, BigDecimal defaultValue);
    Boolean getAttribute(String name, Boolean defaultValue);

    Map<String, String> getAttributes();

}
