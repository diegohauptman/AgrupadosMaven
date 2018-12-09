/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.gmap.CoordinatesService;
import es.agrupados.persistence.ApplicationUserDetails;
import es.agrupados.persistence.ApplicationUsers;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
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
 *
 * @author mundakamacbook
 */
@Named(value = "userWizard")
@ViewScoped
public class UserWizard implements Serializable {
    
    @EJB ApplicationUsersFacade userFacade;
    @EJB ApplicationUserDetailsFacade userDetailsFacade;
    @EJB ApplicationRolesFacade rolesFacade;
    
    private ApplicationUsers user;
    private ApplicationUserDetails userDetails;
    private MapModel model;
    private Marker marker;
    
    @PostConstruct
    void init(){
    model = new DefaultMapModel();
    }
    
    private boolean skip;
     
    public ApplicationUsers getUser() {
        if(user == null)
            user = new ApplicationUsers();
        return user;
    }
    
    public ApplicationUserDetails getUserDetails() {
        if(userDetails == null)
            userDetails = new ApplicationUserDetails();
        return userDetails;
    }
 
    public void setUser(ApplicationUsers user) {
        this.user = user;
    }
    
    public void save(int role) {
        user.setRole(rolesFacade.find(role));
        user.setActive(true);
        System.out.println(user.getRole());
        userFacade.create(user);
        userDetails.setApplicationUsersId(user);
        Date date = new Date();
        userDetails.setDateOfRegistration(date);
        userDetailsFacade.create(userDetails);
        FacesMessage msg = new FacesMessage("Successful", "Welcome :" + user.getUsername());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public boolean isSkip() {
        return skip;
    }
 
    public void setSkip(boolean skip) {
        this.skip = skip;
    }
     
    public String onFlowProcess(FlowEvent event) {
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
    
     /**
     * Método que añade un marcador al modelo del mapa.
     */
    public void addMarker() {
        model.addOverlay(new Marker(new LatLng(userDetails.getLatitude(), userDetails.getLongitude()), userDetails.getFullAddress()));
        addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", userDetails.getCoordinatesForMap()));
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
