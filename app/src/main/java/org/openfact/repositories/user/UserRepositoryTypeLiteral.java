package org.openfact.repositories.user;

import org.openfact.models.BrokerType;

import javax.enterprise.util.AnnotationLiteral;

public class UserRepositoryTypeLiteral extends AnnotationLiteral<UserRepositoryType> implements UserRepositoryType {

    private final BrokerType value;

    public UserRepositoryTypeLiteral(BrokerType value) {
        this.value = value;
    }

    @Override
    public BrokerType value() {
        return value;
    }

}
