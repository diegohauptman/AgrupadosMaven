package es.agrupados.jsf;

import es.agrupados.beans.OffersFacade;
import es.agrupados.persistence.ApplicationUsers;
import es.agrupados.persistence.Offers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@Named("loggedBusinessOfferController")
@SessionScoped
public class LoggedBusinessOfferController implements Serializable {

    @EJB private OffersFacade offersFacade;
    private ApplicationUsers loggedUser;
    private Offers offer;
    Collection<Offers> offersList;
    

    public LoggedBusinessOfferController() {
    }

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loggedUser = (ApplicationUsers) session.getAttribute("business");
        System.out.println("User in session: " + loggedUser.getUsername());
        offersList = loggedUser.getOffersCollection();
    }
    
    public String save(){
        offer.setApplicationUsersId(loggedUser);
        offersFacade.create(offer);
        FacesMessage msg = new FacesMessage("Oferta " + offer.getTitle() + "creada!");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
        System.out.println("Save");
        return view.getViewId() + "?faces-redirect=true";
    }
    
    public String cancel() {
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
        System.out.println("Cancel Method");
        FacesMessage msg = new FacesMessage("Cancelada.");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        return view.getViewId() + "?faces-redirect=true";
    }
    
    public void update(){
        offersFacade.edit(offer);
    }
    
    public Offers getOffer(){
        if(offer == null)
            offer = new Offers();
        return offer; 
    }

    public void setOffer(Offers offer) {
        this.offer = offer;
    }
    

    public List<Offers> getOffersList() {
        List<Offers> list = new ArrayList<>();
        list.addAll(offersList);
        return list;
    }
    
}