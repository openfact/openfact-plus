package org.openfact.models.jpa;

import org.openfact.models.PersonaModel;

import javax.persistence.EntityManager;

public class PersonaAdapter implements PersonaModel {

    private final EntityManager em;

    public PersonaAdapter(EntityManager em) {
        this.em = em;
    }

}
