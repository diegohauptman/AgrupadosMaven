/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.persistence.ApplicationUserDetails;
import es.agrupados.persistence.ApplicationUsers;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
    
    public ApplicationUserDetails findByApplicationUsers(ApplicationUsers applicationUser){
        ApplicationUserDetails userDetails;
        TypedQuery<ApplicationUserDetails> query = em.createNamedQuery(
                "ApplicationUserDetails.findByApplicationUsersId", ApplicationUserDetails.class);
        query.setParameter("applicationUsersId", applicationUser);
        userDetails = query.getSingleResult();
        System.out.println("User Details: " + userDetails);
        return userDetails;
    }
    
}
