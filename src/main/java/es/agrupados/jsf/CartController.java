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
 * Class with business logic to manage the Cart feature.
 * @author Diego Hauptman
 */
@Named("cartController")
@SessionScoped
public class CartController implements Serializable {

    @EJB
    CouponsFacade couponsFacade;
    private ApplicationUsers applicationUser;
    private ApplicationUsers businessUser;
    private ApplicationUsers admin;
    private List<Offers> cartList;
    private List<Coupons> couponsList;
    private HttpSession session;

    public CartController() {
        this.cartList = new ArrayList<>();
        this.couponsList = new ArrayList<>();
    }
    
    /**
     * PostConstruct method to initialize the objects FacesContext and HttpSession,
     * 
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        session = (HttpSession) context.getExternalContext().getSession(false);
    }

    public ApplicationUsers getApplicationUser() {
        return applicationUser;
    }
    
    /**
     * Evaluates if the user is logged as a Client.
     * @return boolean
     */
    public boolean isClient() {
        applicationUser = (ApplicationUsers) session.getAttribute("client");
        return applicationUser != null;
    }
    
    /**
     * Evaluates if the user is logged as Business.
     * @return boolean
     */
    public boolean isBusiness() {
        businessUser = (ApplicationUsers) session.getAttribute("business");
        return businessUser != null;
    }
    
    /**
     * Evaluates if the user is logged as Admin.
     * @return boolean
     */
    public boolean isAdmin() {
        admin = (ApplicationUsers) session.getAttribute("admin");
        return admin != null;
    }

    /**
     * Method to persist coupons in database. Called by "Comprar" button.
     */
    public void save() {
        System.out.println("Is Client? " + isClient());
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Compra realizada!", "Total: €" + total));
            
        } else{
            //Si usuario no esta en la sesión se pide para que inicie la sesion para finalizar la compra.
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No se puede realizar la compra.", "Inicia la sesión como Cliente para finalizar la compra.") );
        }
    }
    
    /**
     * Add offers to cartList if logged as Client or not logged yet.
     * @param offer
     */
    public void addToCart(Offers offer) {
        if(!isBusiness() && !isAdmin()){
        cartList.add(offer);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(offer.getTitle(), "Añadida al carrito."));
        //return "ShoppingCartPage?faces-redirect=true";
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No se ha añadido al carrito.", "Inicia la sesión como Cliente para comprar.") );
        }
    }

    /**
     * Removes an offer from the cartList
     * @param offer
     */
    public void removeFromCart(Offers offer) {
        cartList.remove(offer);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(offer.getTitle(), "Eliminada del carrito."));
    }
    
    /**
     * Clears the cartList
     */
    public void clearCartList(){
        cartList.clear();
    }

    /**
     * Getter of cartList
     * @return cartList
     */
    public List<Offers> getCartList() {
        return cartList;
    }
    
    /**
     * Sums the prices of added offers in cartList
     * @return sum
     */
    public double getCartTotal(){
        double sum = cartList.stream()
                .mapToDouble(item -> item.getOfferPrice())
                .sum();
        return sum;
    }
    
}
