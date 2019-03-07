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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author mundakamacbook
 */
@Named("purchaseController")
public class PurchaseController implements Serializable {
    
    @EJB
    private OffersFacade offersFacade;
    @EJB
    private CouponsFacade couponsFacade;
    private ApplicationUsers loggedUser;
    //private Coupons coupon;
    List<Coupons> purchasedCoupons;
    private BarChartModel barModel;
    private LineChartModel dateModel;
    private int totalCoupons = 0;
    
    
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loggedUser = (ApplicationUsers) session.getAttribute("business");
        totalCoupons();
        createAreaModel();
        totalCouponsByMonth();
        
    }

    public LineChartModel getDateModel() {
        return dateModel;
    }
    
    public BarChartModel getAreaModel() {
        return barModel;
    }
    
    public void createAreaModel(){
        
        barModel = new BarChartModel();
        
        ChartSeries couponsSeries1 = new ChartSeries();
        ChartSeries couponsSeries2 = new ChartSeries();
        
        //offersFacade.findAll();
        
        List<Offers> offersByUsers = offersFacade.getOffersByUsers(loggedUser);
        
        for (Offers offer : offersByUsers) {
             List<Coupons> couponsList = couponsFacade.findCouponsbyOffers(offer);
             couponsSeries1.set(offer.getTitle(), couponsList.size());
        }
        
        couponsSeries1.setLabel("Coupons");
        
        barModel.addSeries(couponsSeries1);
 
        barModel.setTitle("Sales");
        barModel.setLegendPosition("ne");
        barModel.setStacked(true);
        barModel.setShowPointLabels(true);
 
        Axis xAxis = new CategoryAxis("Sales by Offer");
        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Sold Coupons");
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
    
    public void totalCouponsByMonth(){
        
        dateModel = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");
        
        //Get offers from the logged user/business
        List<Offers> offersByUsers = offersFacade.getOffersByUsers(loggedUser);
        
        //Colllects all coupons in one sigle list using flatMap
        List<Coupons> coupons = offersByUsers.stream()
                .map(offer -> couponsFacade.findCouponsbyOffers(offer)
                        .stream()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList()).stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
       
        //Converts all purchaseDateTime of Coupons from Date to Localdate 
        List<CouponsWithLocalDate> couponsWithLocalDate = coupons.stream()
                .map(c -> new CouponsWithLocalDate(c.getId()
                        , c.getGeneratedCode()
                        , c.getPurchaseDatetime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        , c.getUsed())).collect(Collectors.toList());
       
        //Groups and counts coupons by date
        Map<LocalDate, Long> collect = couponsWithLocalDate
                .stream()
                .collect(Collectors.groupingBy(CouponsWithLocalDate::getPurchaseDatetime, Collectors.counting()));
        
        System.out.println(collect);
        
        //Fills chart series with data 
        collect.forEach((k,v) -> {
            System.out.println("Key: " + k + ", Value: " + v);
            series1.set(k, v);
        });
        
        dateModel.addSeries(series1);
        dateModel.setTitle("Quarterly Total Coupons");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("Sold Coupons");
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMin(LocalDate.now().minusMonths(2).toString());
        axis.setMax(LocalDate.now().toString());
        axis.setTickFormat("%b %#d, %y");
 
        dateModel.getAxes().put(AxisType.X, axis);
        
    }
    
    /**
     * Internal Class to convert Coupon's purchaseDateTime type field from Date to LocalDate.
     */
    class CouponsWithLocalDate{
        
        private Integer id;
        private String generatedCode;
        private LocalDate purchaseDatetime;
        private boolean used;

        public CouponsWithLocalDate(Integer id, String generatedCode, LocalDate purchaseDatetime, boolean used) {
            this.id = id;
            this.generatedCode = generatedCode;
            this.purchaseDatetime = purchaseDatetime;
            this.used = used;
        }

        public Integer getId() {
            return id;
        }

        public String getGeneratedCode() {
            return generatedCode;
        }

        public LocalDate getPurchaseDatetime() {
            return purchaseDatetime;
        }

        public boolean isUsed() {
            return used;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setGeneratedCode(String generatedCode) {
            this.generatedCode = generatedCode;
        }

        public void setPurchaseDatetime(LocalDate purchaseDatetime) {
            this.purchaseDatetime = purchaseDatetime;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }
        
    }
}
