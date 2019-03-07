/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.persistence.ApplicationUsers;
import es.agrupados.persistence.Coupons;
import es.agrupados.persistence.Offers;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * Facade for Coupons
 * @author Diego
 */
@Stateless
public class CouponsFacade extends AbstractFacade<Coupons> {

    @PersistenceContext(unitName = "AgrupadosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CouponsFacade() {
        super(Coupons.class);
    }
    
     public List<Coupons> getCouponsByUsers(ApplicationUsers user){
        TypedQuery<Coupons> query = em.createNamedQuery(
                "Coupons.findByApplicationUsers", Coupons.class);
        query.setParameter("applicationUsers", user);
        List<Coupons> couponsList = query.getResultList();
        return couponsList;
        
    }
     
     public List<Coupons> findCouponsbyOffers(Offers offer){
         TypedQuery<Coupons> query = em.createNamedQuery(
                 "Coupons.findByOffers", Coupons.class);
         query.setParameter("offer", offer);
         List<Coupons> couponsList = query.getResultList();
         return couponsList;
     }
     
     public List<Coupons> findCouponsbyPurchaseDate(Date date){
         TypedQuery<Coupons> query = em.createNamedQuery(
                 "Coupons.findByPurchaseDatetime", Coupons.class);
         query.setParameter("purchaseDatetime", date);
         List<Coupons> couponsList = query.getResultList();
         return couponsList;
     }
     
     
    
}
