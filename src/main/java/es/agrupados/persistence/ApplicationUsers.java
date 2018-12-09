/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.persistence;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "application_users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ApplicationUsers.login", query = "SELECT a FROM ApplicationUsers a WHERE a.username = :pUsername AND a.password = :pPassword")
    , @NamedQuery(name = "ApplicationUsers.findAll", query = "SELECT a FROM ApplicationUsers a")
    , @NamedQuery(name = "ApplicationUsers.findById", query = "SELECT a FROM ApplicationUsers a WHERE a.id = :id")
    , @NamedQuery(name = "ApplicationUsers.findByUsername", query = "SELECT a FROM ApplicationUsers a WHERE a.username = :username")
    , @NamedQuery(name = "ApplicationUsers.findByPassword", query = "SELECT a FROM ApplicationUsers a WHERE a.password = :password")
    , @NamedQuery(name = "ApplicationUsers.findByEmail", query = "SELECT a FROM ApplicationUsers a WHERE a.email = :email")
    , @NamedQuery(name = "ApplicationUsers.findByActive", query = "SELECT a FROM ApplicationUsers a WHERE a.active = :active")})
public class ApplicationUsers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "password")
    private String password;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private boolean active;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationUsersId")
    private Collection<Offers> offersCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationUsersId")
    private Collection<Coupons> couponsCollection;
    @JoinColumn(name = "role", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ApplicationRoles role;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationUsersId")
    private Collection<ApplicationUserDetails> applicationUserDetailsCollection;

    public ApplicationUsers() {
    }

    public ApplicationUsers(Integer id) {
        this.id = id;
    }

    public ApplicationUsers(Integer id, String username, String password, String email, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }
    

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @XmlTransient
    public Collection<Offers> getOffersCollection() {
        return offersCollection;
    }

    public void setOffersCollection(Collection<Offers> offersCollection) {
        this.offersCollection = offersCollection;
    }

    @XmlTransient
    public Collection<Coupons> getCouponsCollection() {
        return couponsCollection;
    }

    public void setCouponsCollection(Collection<Coupons> couponsCollection) {
        this.couponsCollection = couponsCollection;
    }

    public ApplicationRoles getRole() {
        return role;
    }

    public void setRole(ApplicationRoles role) {
        this.role = role;
    }

    @XmlTransient
    public Collection<ApplicationUserDetails> getApplicationUserDetailsCollection() {
        return applicationUserDetailsCollection;
    }

    public void setApplicationUserDetailsCollection(Collection<ApplicationUserDetails> applicationUserDetailsCollection) {
        this.applicationUserDetailsCollection = applicationUserDetailsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApplicationUsers)) {
            return false;
        }
        ApplicationUsers other = (ApplicationUsers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getUsername() + " [ id=" + id + " ]";
    }

    
    
}
