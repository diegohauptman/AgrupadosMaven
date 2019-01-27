/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf.util;

import es.agrupados.beans.OffersFacade;
import es.agrupados.persistence.Offers;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Utility singleton class with a background scheduler method to invalidate expired offers.
 * @author mundakamacbook
 */
@Singleton
@Startup
public class OffersTimer {
    @EJB OffersFacade offersFacade;
    
    @PostConstruct
    public void init(){
        expireOffers();
    }

    /**
     * Scheduler that deactivates expired offers.
     */
    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void timer() {
        expireOffers();
    }
    
    private void expireOffers(){
        Date now = new Date();
        List<Offers> offersList = offersFacade.findAll();
        offersList.stream().filter((offer) -> (offer.getEndDate().before(now))).forEachOrdered((offer) -> {
            offer.setActive(false);
        });
        System.out.println("Timer event: " + new Date());
    }
}   

