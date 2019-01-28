package es.agrupados.jsf;

import es.agrupados.persistence.ApplicationUserDetails;
import es.agrupados.jsf.util.JsfUtil;
import es.agrupados.jsf.util.JsfUtil.PersistAction;
import es.agrupados.beans.ApplicationUserDetailsFacade;
import es.agrupados.beans.ApplicationUsersFacade;
import es.agrupados.gmap.CoordinatesService;
import es.agrupados.persistence.ApplicationUsers;

import java.io.Serializable;
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
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 * Class to manage the edit form for logged users. It checks if the user in
 * session has Business or Client roles and defines specific methods to be used
 * for each role.
 *
 * @author Diego Hauptman
 */
@Named("loggedUserController")
@SessionScoped
public class LoggedUserController implements Serializable {

    @EJB
    private es.agrupados.beans.ApplicationUserDetailsFacade userFacade;
    @EJB
    ApplicationUsersFacade applicationUsersFacade;
    private List<ApplicationUserDetails> items = null;
    private ApplicationUserDetails loggedUser;
    private ApplicationUsers user;
    private MapModel model;
    private Marker marker;

    public LoggedUserController() {
    }

    /**
     * Initializes context and session objects, checks if the user is logged as
     * Business or Client and retrieves the specific logged user.
     */
    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

        ApplicationUsers clientUser = (ApplicationUsers) session.getAttribute("client");
        ApplicationUsers businessUser = (ApplicationUsers) session.getAttribute("business");

        //Evalua si el usuario logueado es cliente
        if (clientUser != null) {
            System.out.println("User in session: " + clientUser.getUsername());
            loggedUser = userFacade.findByApplicationUsers(clientUser);
            System.out.println("Logged User in LoggedUserController class: " + loggedUser);

        }
        //Evalua si usuario logueado es business
        if (businessUser != null) {
            System.out.println("User in session: " + businessUser.getUsername());
            loggedUser = userFacade.findByApplicationUsers(businessUser);
            model = new DefaultMapModel();
            System.out.println("Logged User in LoggedUserController class: " + loggedUser);

        }
    }

    /**
     * Adds a marker to the maps model.
     */
    public void addMarker() {
        model.addOverlay(new Marker(new LatLng(loggedUser.getLatitude(), loggedUser.getLongitude()), loggedUser.getFullAddress()));
        //addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", loggedUser.getCoordinatesForMap()));
    }

    /**
     * Shows a message.
     *
     * @param message String.
     */
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /**
     * Getter of the map model.
     *
     * @return MapModel modelo.
     */
    public MapModel getModel() {
        return model;
    }

    /**
     * Setter of the map's model..
     *
     * @param model MapModel.
     */
    public void setModel(MapModel model) {
        this.model = model;
    }

    /**
     * Retrieves the coordinates of the Google Maps API for a specific address.
     *
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
     * Getter of the map's marker.
     *
     * @return Marker map's marker.
     */
    public Marker getMarker() {
        return marker;
    }

    /**
     * Re-initializes the map's model.
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

    /**
     * Updates the user information.
     */
    public void update() {
        persist(PersistAction.UPDATE, "Successfully Updated!");
    }

    public void destroy() {
        if (loggedUser != null) {
            ApplicationUsers user;
            user = loggedUser.getApplicationUsersId();
            user.setActive(false);
            applicationUsersFacade.edit(user);
            persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ApplicationUserDetailsDeleted"));
            if (!JsfUtil.isValidationFailed()) {
                loggedUser = null; // Remove selection
                items = null;    // Invalidate list of items to trigger re-query.
            }
        }
    }

    /**
     * Getter of the logged user.
     *
     * @return loggedUser
     */
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
}
