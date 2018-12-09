/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.persistence;

import java.io.Serializable;
import java.time.LocalDate;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "application_user_details")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ApplicationUserDetails.findAll", query = "SELECT a FROM ApplicationUserDetails a")
    , @NamedQuery(name = "ApplicationUserDetails.findById", query = "SELECT a FROM ApplicationUserDetails a WHERE a.id = :id")
    , @NamedQuery(name = "ApplicationUserDetails.findByName", query = "SELECT a FROM ApplicationUserDetails a WHERE a.name = :name")
    , @NamedQuery(name = "ApplicationUserDetails.findByDniCif", query = "SELECT a FROM ApplicationUserDetails a WHERE a.dniCif = :dniCif")
    , @NamedQuery(name = "ApplicationUserDetails.findByAddress", query = "SELECT a FROM ApplicationUserDetails a WHERE a.address = :address")
    , @NamedQuery(name = "ApplicationUserDetails.findByCity", query = "SELECT a FROM ApplicationUserDetails a WHERE a.city = :city")
    , @NamedQuery(name = "ApplicationUserDetails.findByProvince", query = "SELECT a FROM ApplicationUserDetails a WHERE a.province = :province")
    , @NamedQuery(name = "ApplicationUserDetails.findByCountry", query = "SELECT a FROM ApplicationUserDetails a WHERE a.country = :country")
    , @NamedQuery(name = "ApplicationUserDetails.findByLatitude", query = "SELECT a FROM ApplicationUserDetails a WHERE a.latitude = :latitude")
    , @NamedQuery(name = "ApplicationUserDetails.findByLongitude", query = "SELECT a FROM ApplicationUserDetails a WHERE a.longitude = :longitude")
    , @NamedQuery(name = "ApplicationUserDetails.findByDateOfRegistration", query = "SELECT a FROM ApplicationUserDetails a WHERE a.dateOfRegistration = :dateOfRegistration")
    , @NamedQuery(name = "ApplicationUserDetails.findByLeavingDate", query = "SELECT a FROM ApplicationUserDetails a WHERE a.leavingDate = :leavingDate")})
public class ApplicationUserDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "dni_cif")
    private String dniCif;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "city")
    private String city;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "province")
    private String province;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "country")
    private String country;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date_of_registration")
    @Temporal(TemporalType.DATE)
    private Date dateOfRegistration;
    @Temporal(TemporalType.DATE)
    @Column(name = "leaving_date")
    private Date leavingDate;
    @JoinColumn(name = "application_users_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ApplicationUsers applicationUsersId;
    
    @Transient
    private boolean defaultAddress;

    public ApplicationUserDetails() {
    }

    public ApplicationUserDetails(Integer id) {
        this.id = id;
    }

    public ApplicationUserDetails(Integer id, String name, String dniCif, String address, String city, String province, String country, Date dateOfRegistration) {
        this.id = id;
        this.name = name;
        this.dniCif = dniCif;
        this.address = address;
        this.city = city;
        this.province = province;
        this.country = country;
        this.dateOfRegistration = dateOfRegistration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDniCif() {
        return dniCif;
    }

    public void setDniCif(String dniCif) {
        this.dniCif = dniCif;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public Date getLeavingDate() {
        return leavingDate;
    }

    public void setLeavingDate(Date leavingDate) {
        this.leavingDate = leavingDate;
    }

    public ApplicationUsers getApplicationUsersId() {
        return applicationUsersId;
    }

    public void setApplicationUsersId(ApplicationUsers applicationUsersId) {
        this.applicationUsersId = applicationUsersId;
    }
    
    /**
     * Getter del booleano que indica si es la dirección por defecto.
     *
     * @return boolean defaultAddress.
     */
    public boolean getDefaultAddress() {
        return defaultAddress;
    }
    
    /**
     * Setter del booleano que indica si es la dirección por defecto.
     *
     * @param defaultAddress boolean.
     */
    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
    
    /**
     * Método que devuelve las coordenadas formateadas.
     *
     * @return String coordenadas formateadas.
     */
    public String getCoordinatesForMap() {
        return getLatitude() + "," + getLongitude();
    }
    
     /**
     * Método que devuelve la dirección completa formateada correctamente.
     *
     * @return String dirección completa.
     */
    public String getFullAddress() {
        return (!"".equals(getAddress()) ? getAddress() + "," : "")
                + (!"".equals(getCity()) ? getCity() + "," : "")
                + (!"".equals(getProvince()) ? getProvince() + "," : "")
                + (!"".equals(getCountry()) ? getCountry() + "," : "");
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
        if (!(object instanceof ApplicationUserDetails)) {
            return false;
        }
        ApplicationUserDetails other = (ApplicationUserDetails) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "es.agrupados.persistence.ApplicationUserDetails[ id=" + id + " ]";
    }
    
}
