package org.openfact.models.jpa;

import org.openfact.models.PersonaModel;
import org.openfact.models.PersonaProvider;
import org.openfact.models.jpa.entity.PersonaEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class JpaPersonaProvider implements PersonaProvider {

    @PersistenceContext
    private EntityManager em;

    public PersonaModel create() {
        PersonaEntity personaEntity = new PersonaEntity();
        em.persist(personaEntity);
        return new PersonaAdapter(em);
    }

}
