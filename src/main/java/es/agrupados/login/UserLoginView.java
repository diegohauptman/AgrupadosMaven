/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.login;

import es.agrupados.persistence.ApplicationUsers;
import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

/**
 * Class that manages role based authorization. This class calls LoginBean for the 
 * authentication job and with the returned user it checks for the role and redirects
 * the user to the corresponding page.
 * @author Diego
 */
@FacesConfig
@Named("userLoginView")
@RequestScoped
public class UserLoginView implements Serializable {

    @Inject
    private LoginBean userLoginBean;
    private ApplicationUsers user = new ApplicationUsers();
    private boolean isClient;
    private boolean isBusiness;
    private boolean isAdmin;

    /**
     * Getter of the user. To be used in the xhtml page.
     * @return
     */
    public ApplicationUsers getUser() {
        return user;
    }

    private void checkRole(){
        
        isAdmin = false;
        isClient = false;
        isBusiness = false;
        
        if (user != null) {
            String rolename = user.getRole().getRolename();
            System.out.println("Rolename: " + rolename);
            
            if(rolename.equals("Business")){
                isBusiness = true;
            } 
            
            if(rolename.equals("Client")){
                isClient = true;
            } 
            
            if(rolename.equals("Administrator")){
                isAdmin = true;
            }
        } 
    }

    /**
     * Checks if the user is authenticated and then redirects user to the 
     * corresponding pages based on its roles. Also includes the user in the 
     * session Map.
     * @return String jsf page or empty string
     */
    public String login() {

        boolean loggedIn;
        String userName = user.getUsername();
        System.out.println("Haciendo login de usuário "
                + userName);
        System.out.println("Usuario: " + userName);
        FacesContext context = FacesContext.getCurrentInstance();

        user = userLoginBean.userAuth(user);
        //System.out.println("User info: " + user.toString());
        
        checkRole();

        if (isAdmin && user != null) {
            String role = "admin";
            String page = "/administrator/AdminIndex?faces-redirect=true";
            isAdmin = true;
            return validateUser(context, role, page, user.getUsername());

        } else if (isClient && user != null) {
            if (!user.getActive()) {
                loggedIn = false;
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error",
                         "Usuário desactivado. Regístrate otra vez con el mismo nombre de usuário para reactivar.");
                context.addMessage(null, message);
            } else {
                String role = "client";
                String page = "/client/ClientIndex?faces-redirect=true";
                isClient = true;
                return validateUser(context, role, page, user.getUsername());
            }
        } else if (isBusiness && user != null) {
            String role = "business";
            String page = "/business/BusinessIndex?faces-redirect=true";
            isBusiness = true;
            return validateUser(context, role, page, user.getUsername());

        } else {
            loggedIn = false;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Credenciales inválidas");
            context.addMessage(null, message);
        }

        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        return "";
    }

    private String validateUser(FacesContext context, String role, String page, String userName) {
        boolean loggedIn;
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", userName);
        //context.getExternalContext().getSessionMap().clear();
        context.getExternalContext().getSessionMap()
                .put(role, this.user);
        loggedIn = true;
        System.out.println("Logged In: " + loggedIn);
        System.out.println("User should be => " + role + user.getRole().getRolename());
        context.addMessage(null, message);
        context.getExternalContext().getFlash().setKeepMessages(true);
        return page;
    }

    /**
     * Logout method that invalidates the session and redirects user to the
     * main page.
     */
    public void logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();
        System.out.println("Signing Out");
        try {
            context.getExternalContext().redirect("index.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
