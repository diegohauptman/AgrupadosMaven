package es.agrupados.jsf;

import es.agrupados.persistence.Coupons;
import es.agrupados.jsf.util.JsfUtil;
import es.agrupados.jsf.util.JsfUtil.PersistAction;
import es.agrupados.beans.CouponsFacade;
import es.agrupados.persistence.ApplicationUsers;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpSession;

/**
 * Class that manages the persistence of Coupons in database.
 * Contains CRUD methods.
 * @author mundakamacbook
 */
@Named("couponsController")
@SessionScoped
public class CouponsController implements Serializable {

    @EJB
    private es.agrupados.beans.CouponsFacade ejbFacade;
    private List<Coupons> items = null;
    private Coupons selected;

    /**
     * Default constructor.
     */
    public CouponsController() {
    }

    /**
     * Getter of coupons.
     * @return
     */
    public Coupons getSelected() {
        return selected;
    }

    /**
     * Setter of coupons.
     * @param selected
     */
    public void setSelected(Coupons selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CouponsFacade getFacade() {
        return ejbFacade;
    }

    /**
     * Initializes coupons instance.
     * @return
     */
    public Coupons prepareCreate() {
        selected = new Coupons();
        initializeEmbeddableKey();
        return selected;
    }

    /**
     * Creates a coupon
     */
    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CouponsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    /**
     * Updates a coupon
     */
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CouponsUpdated"));
    }

    /**
     * Deletes a coupon
     */
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CouponsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    /**
     * Retrieves a list with all coupons
     * @return List od coupons
     */
    public List<Coupons> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    /**
     * Retrieves the coupons list for the logged user in session.
     * @return List of coupons
     */
    public List<Coupons> getCouponsByUsers(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        ApplicationUsers user = (ApplicationUsers) session.getAttribute("client");
        return getFacade().getCouponsByUsers(user);
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

    public Coupons getCoupons(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Coupons> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Coupons> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Coupons.class)
    public static class CouponsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CouponsController controller = (CouponsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "couponsController");
            return controller.getCoupons(getKey(value));
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
            if (object instanceof Coupons) {
                Coupons o = (Coupons) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Coupons.class.getName()});
                return null;
            }
        }

    }

}
