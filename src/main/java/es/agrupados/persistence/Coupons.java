/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.persistence;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "coupons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Coupons.findAll", query = "SELECT c FROM Coupons c")
    , @NamedQuery(name = "Coupons.findById", query = "SELECT c FROM Coupons c WHERE c.id = :id")
    , @NamedQuery(name = "Coupons.findByGeneratedCode", query = "SELECT c FROM Coupons c WHERE c.generatedCode = :generatedCode")
    , @NamedQuery(name = "Coupons.findByPurchaseDatetime", query = "SELECT c FROM Coupons c WHERE c.purchaseDatetime = :purchaseDatetime")
    , @NamedQuery(name = "Coupons.findByUsed", query = "SELECT c FROM Coupons c WHERE c.used = :used")})
public class Coupons implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "generated_code")
    private String generatedCode;
    @Basic(optional = false)
    @NotNull
    @Column(name = "purchase_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseDatetime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "used")
    private boolean used;
    @JoinColumn(name = "application_users_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ApplicationUsers applicationUsersId;
    @JoinColumn(name = "offers_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Offers offersId;

    public Coupons() {
    }

    public Coupons(Integer id) {
        this.id = id;
    }

    public Coupons(Integer id, String generatedCode, Date purchaseDatetime, boolean used) {
        this.id = id;
        this.generatedCode = generatedCode;
        this.purchaseDatetime = purchaseDatetime;
        this.used = used;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }

    public Date getPurchaseDatetime() {
        return purchaseDatetime;
    }

    public void setPurchaseDatetime(Date purchaseDatetime) {
        this.purchaseDatetime = purchaseDatetime;
    }

    public boolean getUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public ApplicationUsers getApplicationUsersId() {
        return applicationUsersId;
    }

    public void setApplicationUsersId(ApplicationUsers applicationUsersId) {
        this.applicationUsersId = applicationUsersId;
    }

    public Offers getOffersId() {
        return offersId;
    }

    public void setOffersId(Offers offersId) {
        this.offersId = offersId;
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
        if (!(object instanceof Coupons)) {
            return false;
        }
        Coupons other = (Coupons) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.agrupados.persistence.Coupons[ id=" + id + " ]";
    }
    
}
