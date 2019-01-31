/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf;

import es.agrupados.beans.ApplicationRolesFacade;
import es.agrupados.beans.ApplicationUserDetailsFacade;
import es.agrupados.beans.ApplicationUsersFacade;
import es.agrupados.gmap.CoordinatesService;
import es.agrupados.persistence.ApplicationUserDetails;
import es.agrupados.persistence.ApplicationUsers;
import java.io.Serializable;
import java.util.Date;
import javafx.util.Pair;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 * Class with business logic that processes the users input in the registration
 * page and controls the flow of the registration wizard.
 *
 * @author mundakamacbook
 */
@Named(value = "registrationWizard")
@ViewScoped
public class RegistrationWizard implements Serializable {

    @EJB
    ApplicationUsersFacade userFacade;
    @EJB
    ApplicationUserDetailsFacade userDetailsFacade;
    @EJB
    ApplicationRolesFacade rolesFacade;

    private ApplicationUsers user;
    private ApplicationUserDetails userDetails;
    private MapModel model;
    private Marker marker;

    @PostConstruct
    void init() {
        model = new DefaultMapModel();
    }

    private boolean skip;

    public ApplicationUsers getUser() {
        if (user == null) {
            user = new ApplicationUsers();
        }
        return user;
    }

    public ApplicationUserDetails getUserDetails() {
        if (userDetails == null) {
            userDetails = new ApplicationUserDetails();
        }
        return userDetails;
    }

    public void setUser(ApplicationUsers user) {
        this.user = user;
    }

    /**
     * Method to save a registered user in the database. It first checks if the
     * username or email already exists.
     *
     * @param role the role id is defined in the jsf registration page in which
     * this method is called.
     */
    public void save(int role) {
        Pair<Boolean, Pair<ApplicationUsers, String>> result = userFacade.exists(user);
        Boolean exists = result.getKey();
        Pair<ApplicationUsers, String> valuePair = result.getValue();
        ApplicationUsers existingUser = valuePair.getKey();
        
        if(existingUser != null)
            System.out.println("existing user: " + existingUser.getUsername());
        
        String value = valuePair.getValue();
        if (exists) {
            if (value.contains("username")) {
                FacesMessage msg = new FacesMessage("El nombre de usuário: " + user.getUsername() + " ya existe.", null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

            if (value.contains("email")) {
                FacesMessage msg = new FacesMessage("El correo:  " + user.getEmail() + " ya existe.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                
            } else if (exists && !existingUser.getActive()) {
                existingUser.setActive(true);
                existingUser.setRole(rolesFacade.find(role));
                existingUser.setUsername(user.getUsername());
                existingUser.setPassword(user.getPassword());
                existingUser.setEmail(user.getEmail());
                userFacade.edit(existingUser);
                userDetails.setApplicationUsersId(existingUser);
                Date date = new Date();
                userDetails.setDateOfRegistration(date);
                userDetailsFacade.create(userDetails);
                FacesMessage msg = new FacesMessage("Usuário existente reactivado!", "Gracias por volver: " + user.getUsername());
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            user.setRole(rolesFacade.find(role));
            user.setActive(true);
            System.out.println(user.getRole());
            userFacade.create(user);
            userDetails.setApplicationUsersId(user);
            Date date = new Date();
            userDetails.setDateOfRegistration(date);
            userDetailsFacade.create(userDetails);
            FacesMessage msg = new FacesMessage("Usuário creado!", "Bienvenido: " + user.getUsername());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

    /**
     * Método que añade un marcador al modelo del mapa.
     */
    public void addMarker() {
        model.addOverlay(new Marker(new LatLng(userDetails.getLatitude(), userDetails.getLongitude()), userDetails.getFullAddress()));
        //addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", userDetails.getCoordinatesForMap()));
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
        double[] coords = service.getLatitudeLongitude(getAddress().getFullAddress());
        getAddress().setLatitude(coords[0]);
        getAddress().setLongitude(coords[1]);
        System.out.println("Dirección: " + service.getAddress(getAddress().getLatitude(), getAddress().getLongitude()));
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

    /**
     * Getter de la dirección.
     *
     * @return Address dirección por la que consulta.
     */
    public ApplicationUserDetails getAddress() {
        return userDetails;
    }

    /**
     * Setter de la dirección.
     *
     * @param defaultAddress Address dirección.
     */
    public void setAddress(ApplicationUserDetails defaultAddress) {
        this.userDetails = defaultAddress;
    }

}
