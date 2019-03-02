package es.agrupados.jsf;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import es.agrupados.persistence.Offers;
import es.agrupados.jsf.util.JsfUtil;
import es.agrupados.jsf.util.JsfUtil.PersistAction;
import es.agrupados.beans.OffersFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Class with business logic to managed the Offers entity.
 * @author mundakamacbook
 */
@Named("offersController")
@SessionScoped
public class OffersController implements Serializable {

    @EJB private es.agrupados.beans.OffersFacade ejbFacade;
    private List<Offers> items = null;
    private Offers selected;
    private String filterValue;
    private List<Offers> filteredItems = null;

    public OffersController() {
    }
    
    @PostConstruct
    public void init(){
        //filteredItems = getFacade().findAll();
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
    

    public Offers getSelected() {
        return selected;
    }
    
    public void setSelected(Offers selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private OffersFacade getFacade() {
        return ejbFacade;
    }

    public Offers prepareCreate() {
        selected = new Offers();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("OffersCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("OffersUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("OffersDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public List<Offers> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public List<Offers> getFilteredItems() {
         if (filteredItems == null) {
            filteredItems = getFacade().findAll().stream()
                    .filter(item -> item.getActive() == true)
                    .collect(Collectors.toList());
        }
        return filteredItems;
    }

    public void setFilteredItems(List<Offers> filteredItems) {
        this.filteredItems = filteredItems;
    }
    
    
    /**
     * Returns a list of active offers
     * @return activeItems
     */
    public List<Offers> getActiveItems() {
        List<Offers> activeItems;
        items = getFacade().findAll();
        activeItems = items.stream()
                .filter(offer -> (offer.getActive() == true))
                .collect(Collectors.toList());

        return activeItems;
    }
    
    /**
     * Search for offers by any string contained in the title or description of the offer.
     */
    public void filterOffers(){
        items = getFacade().findAll();
        List<Offers> filteredList = items.stream()
                .filter(offer -> (offer.getTitle().toLowerCase().contains(filterValue.toLowerCase()) 
                        || offer.getDescription().toLowerCase().contains(filterValue.toLowerCase())
                        || offer.getApplicationUsersId().getUsername().contains(filterValue.toLowerCase())) 
                        && (offer.getActive() == true))
                .collect(Collectors.toList());
        setFilteredItems(filteredList);
    }
    
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Offers getOffers(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Offers> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Offers> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Offers.class)
    public static class OffersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            OffersController controller = (OffersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "offersController");
            return controller.getOffers(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Offers) {
                Offers o = (Offers) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Offers.class.getName()});
                return null;
            }
        }

    }

}
