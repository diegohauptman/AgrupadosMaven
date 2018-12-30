package es.agrupados.jsf;

import es.agrupados.persistence.ApplicationUserDetails;
import es.agrupados.jsf.util.JsfUtil;
import es.agrupados.jsf.util.JsfUtil.PersistAction;
import es.agrupados.beans.ApplicationUserDetailsFacade;
import es.agrupados.beans.OffersFacade;
import es.agrupados.gmap.CoordinatesService;
import es.agrupados.persistence.ApplicationUsers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.servlet.http.HttpSession;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@Named("loggedUserController")
@SessionScoped
public class LoggedUserController implements Serializable {

    @EJB private es.agrupados.beans.ApplicationUserDetailsFacade userFacade;
    private List<ApplicationUserDetails> items = null;
    private ApplicationUserDetails loggedUser;
    private MapModel model;
    private Marker marker;

    public LoggedUserController() {
    }

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

        ApplicationUsers clientUser = (ApplicationUsers) session.getAttribute("client");
        ApplicationUsers businessUser = (ApplicationUsers) session.getAttribute("business");

        if (clientUser != null) {

            System.out.println("User in session: " + clientUser.getUsername());
            Collection<ApplicationUserDetails> userDetailsList = clientUser.getApplicationUserDetailsCollection();
            loggedUser = userDetailsList.stream()
                    .filter(userInTheList -> clientUser.getId().equals(userInTheList.getApplicationUsersId().getId()))
                    .findAny()
                    .orElse(null);
        }

        if (businessUser != null) {
            System.out.println("User in session: " + businessUser.getUsername());
            model = new DefaultMapModel();
            Collection<ApplicationUserDetails> userDetailsList = businessUser.getApplicationUserDetailsCollection();
            loggedUser = userDetailsList.stream()
                    .filter(userInTheList -> businessUser.getId().equals(userInTheList.getApplicationUsersId().getId()))
                    .findAny()
                    .orElse(null);
        }

    }
    
    
    /**
     * Método que añade un marcador al modelo del mapa.
     */
    public void addMarker() {
        model.addOverlay(new Marker(new LatLng(loggedUser.getLatitude(), loggedUser.getLongitude()), loggedUser.getFullAddress()));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", loggedUser.getCoordinatesForMap()));
    }
    
      /**
     * Método para mostrar un mensaje al contexto.
     *
     * @param message String.
     */
    public void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Getter del modelo del mapa.
     *
     * @return MapModel modelo.
     */
    public MapModel getModel() {
        return model;
    }

    /**
     * Setter del modelo del mapa.
     *
     * @param model MapModel.
     */
    public void setModel(MapModel model) {
        this.model = model;
    }

    /**
     * Método que recupera las coordenadas del API de Google Maps para una
     * dirección determinada.
     */
    public void retrieveCoordinates() {
        CoordinatesService service = new CoordinatesService();
        double[] coords = service.getLatitudeLongitude(loggedUser.getFullAddress());
        loggedUser.setLatitude(coords[0]);
        loggedUser.setLongitude(coords[1]);
        System.out.println("Dirección: " + service.getAddress(loggedUser.getLatitude(), loggedUser.getLongitude()));
        resetModel();
        addMarker();
    }

    /**
     * Getter del marcador del mapa.
     *
     * @return Marker marcador del mapa.
     */
    public Marker getMarker() {
        return marker;
    }

    /**
     * Método que reinicia el modelo del mapa.
     */
    private void resetModel() {
        model = new DefaultMapModel();
    }
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ApplicationUserDetailsFacade getUserFacade() {
        return userFacade;
    }

    public ApplicationUserDetails prepareCreate() {
        loggedUser = new ApplicationUserDetails();
        initializeEmbeddableKey();
        return loggedUser;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ApplicationUserDetailsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "Successfully Updated!");
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ApplicationUserDetailsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            loggedUser = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ApplicationUserDetails> getItems() {
        if (items == null) {
            items = getUserFacade().findAll();
        }
        return items;
    }

    public ApplicationUserDetails getLoggedUser() {
        return loggedUser;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (loggedUser != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getUserFacade().edit(loggedUser);
                } else {
                    getUserFacade().remove(loggedUser);
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

    public ApplicationUserDetails getApplicationUserDetails(java.lang.Integer id) {
        return getUserFacade().find(id);
    }

    public List<ApplicationUserDetails> getItemsAvailableSelectMany() {
        return getUserFacade().findAll();
    }

    public List<ApplicationUserDetails> getItemsAvailableSelectOne() {
        return getUserFacade().findAll();
    }

    @FacesConverter(forClass = ApplicationUserDetails.class)
    public static class ApplicationUserDetailsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ApplicationUserDetailsController controller = (ApplicationUserDetailsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "loggedUserController");
            return controller.getApplicationUserDetails(getKey(value));
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
            if (object instanceof ApplicationUserDetails) {
                ApplicationUserDetails o = (ApplicationUserDetails) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ApplicationUserDetails.class.getName()});
                return null;
            }
        }
    }
}
