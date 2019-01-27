/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.persistence.ApplicationUsers;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Facade for ApplicationUsers
 * @author Diego
 */
@Stateless
public class ApplicationUsersFacade extends AbstractFacade<ApplicationUsers> {

    @PersistenceContext(unitName = "AgrupadosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ApplicationUsersFacade() {
        super(ApplicationUsers.class);
    }
    
    /**
     * Checks if username or email already exists in database.
     * @param user
     * @return Pair with Boolean and a String.
     */
    public Pair<Boolean, Pair<ApplicationUsers, String>> exists(ApplicationUsers user){
        List<ApplicationUsers> allUsers = findAll();
        ApplicationUsers existingUser;
        for(ApplicationUsers users : allUsers){
            if(user.getUsername().equals(users.getUsername())){
                existingUser = users;
                Pair<ApplicationUsers, String> userInfo = new Pair<>(existingUser, "username");
                System.out.println("Username already exists: " + users.getUsername());
                return new Pair<>(true, userInfo);
        }
            if(user.getEmail().equals(users.getEmail())){
                 existingUser = users;
                Pair<ApplicationUsers, String> userInfo = new Pair<>(existingUser, "email");
                System.out.println("Email already exists: " + users.getEmail());
                return new Pair<>(true, userInfo);
            }
     }
        return new Pair<>(false, new Pair<ApplicationUsers, String>(null, "User don't exists"));
  }
}