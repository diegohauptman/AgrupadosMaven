/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.persistence.ApplicationUsers;
import es.agrupados.persistence.Offers;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Facade for Offers
 * @author Diego
 */
@Stateless
public class OffersFacade extends AbstractFacade<Offers> {

    @PersistenceContext(unitName = "AgrupadosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OffersFacade() {
        super(Offers.class);
    }
    
     public List<Offers> getOffersByUsers(ApplicationUsers user){
        TypedQuery<Offers> query = em.createNamedQuery(
                "Offers.findByApplicationUsers", Offers.class);
        query.setParameter("applicationUsers", user);
        List<Offers> offersList = query.getResultList();
         System.out.println("List of offers by users: " + offersList);
        return offersList;
        
    }
     
     
    
}
