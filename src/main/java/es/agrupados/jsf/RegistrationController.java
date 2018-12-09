/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf;

import es.agrupados.beans.ApplicationUsersFacade;
import es.agrupados.jsf.util.JsfUtil;
import es.agrupados.persistence.ApplicationUsers;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author mundakamacbook
 */
@Named("registrationController")
@ViewScoped
public class RegistrationController implements Serializable{
    
     @EJB
    private es.agrupados.beans.ApplicationUsersFacade ejbFacade;
    private List<ApplicationUsers> items = null;
    private ApplicationUsers users;

    public RegistrationController() {
    }

    public ApplicationUsers getUsers() {
        return users;
    }

    public void setUsers(ApplicationUsers users) {
        this.users = users;
    }

    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ApplicationUsersFacade getFacade() {
        return ejbFacade;
    }

    public ApplicationUsers prepareCreate() {
        users = new ApplicationUsers();
        initializeEmbeddableKey();
        return users;
    }

    public void create() {
        persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ApplicationUsersCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(JsfUtil.PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ApplicationUsersUpdated"));
    }

    public void destroy() {
        persist(JsfUtil.PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ApplicationUsersDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            users = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ApplicationUsers> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (users != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    getFacade().edit(users);
                } else {
                    getFacade().remove(users);
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

    public ApplicationUsers getApplicationUsers(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ApplicationUsers> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ApplicationUsers> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ApplicationUsers.class)
    public static class ApplicationUsersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ApplicationUsersController controller = (ApplicationUsersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "applicationUsersController");
            return controller.getApplicationUsers(getKey(value));
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
            if (object instanceof ApplicationUsers) {
                ApplicationUsers o = (ApplicationUsers) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ApplicationUsers.class.getName()});
                return null;
            }
        }

    }
    
}
