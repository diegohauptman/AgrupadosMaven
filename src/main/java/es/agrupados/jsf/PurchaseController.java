/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf;

import es.agrupados.beans.CouponsFacade;
import es.agrupados.beans.OffersFacade;
import es.agrupados.persistence.ApplicationUsers;
import es.agrupados.persistence.Coupons;
import es.agrupados.persistence.Offers;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author mundakamacbook
 */
@Named("purchaseController")
@SessionScoped
public class PurchaseController implements Serializable {
    
    @EJB
    private OffersFacade offersFacade;
    @EJB
    private CouponsFacade couponsFacade;
    private ApplicationUsers loggedUser;
    //private Coupons coupon;
    List<Coupons> purchasedCoupons;
    private LineChartModel areaModel;
    private int totalCoupons = 0;
    
    
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loggedUser = (ApplicationUsers) session.getAttribute("business");
        totalCoupons();
        createAreaModel();
        
    }

    public CartesianChartModel getAreaModel() {
        return areaModel;
    }
    
    public void createAreaModel(){
        
        areaModel = new LineChartModel();
        
        LineChartSeries couponsSeries1 = new LineChartSeries();
        LineChartSeries couponsSeries2 = new LineChartSeries();
        
        offersFacade.findAll();
        
        List<Offers> offersByUsers = offersFacade.getOffersByUsers(loggedUser);
        
        for (Offers offer : offersByUsers) {
             List<Coupons> couponsList = couponsFacade.findCouponsbyOffers(offer);
             couponsSeries1.set(offer, couponsList.size());
        }
        
        couponsSeries1.setFill(true);
        couponsSeries1.setLabel("Coupons");
        
        areaModel.addSeries(couponsSeries1);
 
        areaModel.setTitle("Area Chart");
        areaModel.setLegendPosition("ne");
        areaModel.setStacked(true);
        areaModel.setShowPointLabels(true);
 
        Axis xAxis = new CategoryAxis("Offertas");
        areaModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        yAxis.setLabel("Vendidos");
        yAxis.setMin(0);
        yAxis.setMax(100);
        
    }
    
    public void totalCoupons(){
        
        List<Offers> allOffers = offersFacade.findAll();
        
        List<Offers> businessOffers = allOffers.stream()
                .filter(offer -> offer.getApplicationUsersId().equals(loggedUser))
                .collect(Collectors.toList());
        
        int coupons = 0;
        
        for (Offers offer : businessOffers) {
            List<Coupons> couponsList = couponsFacade.findCouponsbyOffers(offer);
            System.out.println("inside for loop");
            System.out.println("Coupons: " + couponsList.size());
            coupons += couponsList.size();
        }
        
        
        totalCoupons = coupons;
        
        
        //functional version
        
//         coupons = businessOffers.stream().map((offer) -> couponsFacade.findCouponsbyOffers(offer)).map((couponsList) -> {
//            System.out.println("Coupons: " + couponsList.size());
//            return couponsList;
//        }).map((couponsList) -> couponsList.size()).reduce(coupons, Integer::sum);
//        
        System.out.println("How many coupons: " + coupons);

        
        
    }
    
    public int totalCouponsByOffer(){
        List<Offers> offersByUsers = offersFacade.getOffersByUsers(loggedUser);
        
        for (Offers offer : offersByUsers) {
            
            Offers found = offersFacade.find(offer);
            
        }
        
        return 0;
    }
}
