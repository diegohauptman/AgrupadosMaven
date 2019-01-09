/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf;

import es.agrupados.beans.CouponsFacade;
import es.agrupados.persistence.ApplicationUsers;
import es.agrupados.persistence.Coupons;
import es.agrupados.persistence.Offers;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author mundakamacbook
 */
@Named("cartController")
@SessionScoped
public class CartController implements Serializable {

    @EJB
    CouponsFacade couponsFacade;
    private ApplicationUsers applicationUser;
    private List<Offers> cartList;
    private List<Coupons> couponsList;
    private FacesContext context;

    @PostConstruct
    public void init() {
        context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        applicationUser = (ApplicationUsers) session.getAttribute("client");
        couponsList = new ArrayList<>();
        cartList = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public boolean isClient() {
        return applicationUser != null;
    }

    /**
     * Method to persist coupons in database. Called by "Comprar" button.
     */
    public void save() {
        if (isClient()) {
            Date purchaseDatetime = new Date();
            
            for (Offers item : cartList) {
                Coupons coupon = new Coupons();
                String code = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
                coupon.setOffersId(item);
                coupon.setPurchaseDatetime(purchaseDatetime);
                coupon.setGeneratedCode(code);
                coupon.setUsed(false);
                coupon.setApplicationUsersId(applicationUser);
                couponsList.add(coupon);
            }
            //Guarda el la base de datos.
            couponsList.forEach(couponsFacade::create);
            //Recupera el total de la compra.
            double total = getCartTotal();
            //Vacia la lista de cupones.
            couponsList.clear();
            //Vacia la lista de compras.
            cartList.clear();
            //Anade mensaje de successo de la compra.
            context.addMessage(null, new FacesMessage("Compra realizada. Total: " + total));
            
        } else{
            //Si usuario no esta en la sesión se pide para que inicie la sesion para finalizar la compra.
            context.addMessage(null, new FacesMessage("Please Login to complete") );
        }
    }
    
     public String addToCart(Offers offer) {
        cartList.add(offer);
        return "ShoppingCartPage?faces-redirect=true";
    }

    public void removeFromCart(Offers offer) {
        cartList.remove(offer);
    }
    
    public void clearCartList(){
        cartList.clear();
    }

    public List<Offers> getCartList() {
        return cartList;
    }
    
    public double getCartTotal(){
        double sum = cartList.stream()
                .mapToDouble(item -> item.getOfferPrice())
                .sum();
        return sum;
    }
    
}
