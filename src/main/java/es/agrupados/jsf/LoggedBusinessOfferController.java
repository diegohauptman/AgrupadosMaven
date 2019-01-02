package es.agrupados.jsf;

import es.agrupados.beans.OffersFacade;
import es.agrupados.persistence.ApplicationUsers;
import es.agrupados.persistence.Offers;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.omnifaces.cdi.ViewScoped;

@Named("loggedBusinessOfferController")
@ViewScoped
public class LoggedBusinessOfferController implements Serializable {

    @EJB private OffersFacade offersFacade;
    private ApplicationUsers loggedUser;
    private Offers offer;
    private Offers createOffer;
    List<Offers> offersList;
    

    public LoggedBusinessOfferController() {
    }

    /**
     * PostConstruct to initialize the list of offers with only the offers of the logged business.
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        loggedUser = (ApplicationUsers) session.getAttribute("business");
        offersList = offersFacade.getOffersByUsers(loggedUser);
    }
    
    public String save(){
        createOffer.setApplicationUsersId(loggedUser);
        offersFacade.create(createOffer);
        FacesMessage msg = new FacesMessage("Oferta " + createOffer.getTitle() + "creada!");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
        return view.getViewId() + "?faces-redirect=true";
    }
    
    public String cancel() {
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
        FacesMessage msg = new FacesMessage("Cancelada.");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
        return view.getViewId() + "?faces-redirect=true";
    }
    
    public void update(){
        offersFacade.edit(offer);
    }
    
     public void destroy() {
        offersFacade.remove(offer);
        offer = null; // Remove selection
        offersList = null;    // Invalidate list of items to trigger re-query.
    }
    
    public Offers getOffer(){
        return offer; 
    }
    
    public Offers getCreateOffers(){
         if(createOffer == null)
            createOffer = new Offers();
         return createOffer;
    }

    public void setOffer(Offers offer) {
        this.offer = offer;
    }
    
    public List<Offers> getOffersList() {
        return offersList;
    }
    
}