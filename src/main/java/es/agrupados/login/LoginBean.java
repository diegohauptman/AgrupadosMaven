/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.login;

import es.agrupados.persistence.ApplicationUsers;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author mundakamacbook
 */
@Stateless
public class LoginBean {
    
    @PersistenceContext(unitName = "AgrupadosPU")
    private EntityManager em;
    
     public ApplicationUsers userAuth (ApplicationUsers user) {
        System.out.println("Usuario inside userAuth method: " + user.getEmail());
        TypedQuery<ApplicationUsers> query = em.createNamedQuery(
                "ApplicationUsers.login", ApplicationUsers.class);
        query.setParameter("pUsername", user.getUsername());
        query.setParameter("pPassword", user.getPassword());
        
        try {
            user = query.getSingleResult();
            System.out.println(user.toString());
        } catch (NoResultException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return user;
    }
    
    public boolean isAdmin(ApplicationUsers user) {
        String rolename = user.getRole().getRolename();
        System.out.println("Rolename: " + rolename);
        return rolename.equals("Administrator");
    }
    
    public boolean isClient(ApplicationUsers user) {
        String rolename = user.getRole().getRolename();
        System.out.println("Rolename: " + rolename);
        return rolename.equals("Client");
    }
    
    public boolean isBusiness(ApplicationUsers user) {
        String rolename = user.getRole().getRolename();
        System.out.println("Rolename: " + rolename);
        return rolename.equals("Business");
    }
    
}
