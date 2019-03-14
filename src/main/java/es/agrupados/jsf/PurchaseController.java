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
import org.primefaces.model.chart.CartesianChartModel;
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
    private CartesianChartModel barModel;
    private CartesianChartModel profitModel;
    private LineChartModel dateModel;
    private int totalCoupons = 0;
    
    
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loggedUser = (ApplicationUsers) session.getAttribute("business");
        totalCoupons();
        numberOfCouponsByOffer();
        totalCouponsByMonth();
        getProfit();
        
    }

    public LineChartModel getDateModel() {
        return dateModel;
    }
    
    public CartesianChartModel getCouponsByOffer() {
        return barModel;
    }

    public CartesianChartModel getProfitModel() {
        return profitModel;
    }
    
    public void numberOfCouponsByOffer(){
        
        barModel = new BarChartModel();
        ChartSeries count = new ChartSeries();
       
        List<Offers> offersByUsers = offersFacade.getOffersByUsers(loggedUser);
        
        offersByUsers.forEach((offer) -> {
            List<Coupons> couponsList = couponsFacade.findCouponsbyOffers(offer);
            float total = couponsList.size() * offer.getOfferPrice();
            count.set(offer.getTitle(), couponsList.size());
        });
        
        count.setLabel("Cantidad");
        
        barModel.addSeries(count);
 
        barModel.setTitle("Total de Ofertas Vendidas");
        barModel.setLegendPosition("ne");
        //barModel.setStacked(true);
        barModel.setShowPointLabels(true);
        barModel.setZoom(true);
        barModel.setAnimate(true);
               
 
        Axis xAxis = new CategoryAxis("Ventas por Oferta");
        barModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Cupons Vendidos");
        yAxis.setMin(0);
        yAxis.setMax(200);
        
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
        dateModel.setTitle("Cupons vendidos en 3 meses");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("Cupons Vendidos");
        dateModel.setAnimate(true);
        DateAxis axis = new DateAxis("Fechas");
        axis.setTickAngle(-50);
        axis.setMin(LocalDate.now().minusMonths(2).toString());
        axis.setMax(LocalDate.now().toString());
        axis.setTickFormat("%b %#d, %y");
 
        dateModel.getAxes().put(AxisType.X, axis);
        
    }
    
    public void getProfit(){
        List<Offers> offersByUsers = offersFacade.getOffersByUsers(loggedUser);
        profitModel = new BarChartModel();
        ChartSeries totalMoney = new ChartSeries();
       
        offersByUsers.forEach((offer) -> {
            List<Coupons> couponsList = couponsFacade.findCouponsbyOffers(offer);
            float total = couponsList.size() * offer.getOfferPrice();
            totalMoney.set(offer.getTitle(), total);
            System.out.println("Offer: " + offer.getTitle() + " Total: €" + total);
        });
        
        totalMoney.setLabel("Euros");
        
        profitModel.addSeries(totalMoney);
 
        profitModel.setTitle("Renta por Oferta");
        profitModel.setLegendPosition("ne");
        //barModel.setStacked(true);
        profitModel.setShowPointLabels(true);
        profitModel.setZoom(true);
        profitModel.setAnimate(true);
               
 
        Axis xAxis = new CategoryAxis("Ventas por Oferta");
        profitModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = profitModel.getAxis(AxisType.Y);
        yAxis.setLabel("€");
        yAxis.setMin(0);
        yAxis.setMax(5000);
         
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
