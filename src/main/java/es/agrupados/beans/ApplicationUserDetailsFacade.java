/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.persistence.ApplicationUserDetails;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Diego
 */
@Stateless
public class ApplicationUserDetailsFacade extends AbstractFacade<ApplicationUserDetails> {

    @PersistenceContext(unitName = "AgrupadosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ApplicationUserDetailsFacade() {
        super(ApplicationUserDetails.class);
    }
    
}
