/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.login.LoginBean;
import es.agrupados.persistence.ApplicationUsers;
import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

@FacesConfig
@Named("userLoginView")
@SessionScoped
public class UserLoginView implements Serializable {

    //@Inject
    //private ApplicationUsersFacade userBean;
    @Inject
    private LoginBean userBean;
    private ApplicationUsers user = new ApplicationUsers();
    public final static String USER_KEY = "auth_user";

    public ApplicationUsers getUser() {
        return user;
    }

    public String login() {

        boolean loggedIn = false;
        String userName = user.getUsername();
        System.out.println("Haciendo login de usuário "
                + userName);
        System.out.println("Usuario: " + userName);
        FacesContext context = FacesContext.getCurrentInstance();

        user = userBean.userAuth(user);
        System.out.println("User info: " + user.toString());

        if (userBean.isAdmin(user)) {
            String role = "admin";
            String page = "/administrator/AdminIndex?faces-redirect=true";
            return validateUser(context, role, page, user.getUsername());

        } else if (userBean.isClient(user)) {
            String role = "client";
            String page = "/client/ClientIndex?faces-redirect=true";
            return validateUser(context, role, page, user.getUsername());

        } else if (userBean.isBusiness(user)) {
            String role = "business";
            String page = "/business/BusinessIndex?faces-redirect=true";
            return validateUser(context, role, page, user.getUsername());

        } else {
            loggedIn = false;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
            context.addMessage(null, message);
        }

        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        return "";
    }

    private String validateUser(FacesContext context, String role, String page, String userName) {
        boolean loggedIn;
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", userName);
        context.getExternalContext().getSessionMap().clear();
        context.getExternalContext().getSessionMap()
                .put(role, this.user);
        loggedIn = true;
        System.out.println("Logged In: " + loggedIn);
        System.out.println("User should be => " + role + user.getRole().getRolename());
        context.addMessage(null, message);
        context.getExternalContext().getFlash().setKeepMessages(true);
        return page;
    }

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
