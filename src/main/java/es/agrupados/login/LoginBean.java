/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.login;

import es.agrupados.persistence.ApplicationUsers;
import java.util.List;
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

    public ApplicationUsers userAuth(ApplicationUsers user) {
        System.out.println("Usuario inside userAuth method: " + user.getUsername());
        List<ApplicationUsers> usersList;
        TypedQuery<ApplicationUsers> query = em.createNamedQuery(
                "ApplicationUsers.login", ApplicationUsers.class);
        query.setParameter("pUsername", user.getUsername());
        query.setParameter("pPassword", user.getPassword());
        usersList = query.getResultList();

        if (!usersList.isEmpty()) {
            for (ApplicationUsers userIntheList : usersList) {
                user = userIntheList;
            }
            return user;
        } else {
            return null;
        }
        //System.out.println(user.toString());
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
