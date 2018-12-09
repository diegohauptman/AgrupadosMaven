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
@Table(name = "application_roles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ApplicationRoles.findAll", query = "SELECT a FROM ApplicationRoles a")
    , @NamedQuery(name = "ApplicationRoles.findById", query = "SELECT a FROM ApplicationRoles a WHERE a.id = :id")
    , @NamedQuery(name = "ApplicationRoles.findByRolename", query = "SELECT a FROM ApplicationRoles a WHERE a.rolename = :rolename")})
public class ApplicationRoles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "rolename")
    private String rolename;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private Collection<ApplicationUsers> applicationUsersCollection;

    public ApplicationRoles() {
    }

    public ApplicationRoles(Integer id) {
        this.id = id;
    }

    public ApplicationRoles(Integer id, String rolename) {
        this.id = id;
        this.rolename = rolename;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    @XmlTransient
    public Collection<ApplicationUsers> getApplicationUsersCollection() {
        return applicationUsersCollection;
    }

    public void setApplicationUsersCollection(Collection<ApplicationUsers> applicationUsersCollection) {
        this.applicationUsersCollection = applicationUsersCollection;
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
        if (!(object instanceof ApplicationRoles)) {
            return false;
        }
        ApplicationRoles other = (ApplicationRoles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getRolename() + " [ id=" + id + " ]";
    }
    
}
