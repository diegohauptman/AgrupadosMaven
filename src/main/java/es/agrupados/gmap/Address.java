/*
 * The MIT License
 *
 * Copyright 2015 SEAS - Estudios Abiertos.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.agrupados.gmap;

/**
 * Class to define Address entity.
 *
 * @author Diego
 */
public class Address {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String address;
    private String city;
    private String country;
    private String province;
    private String postalCode;
    private Double longitude;
    private Double latitude;
    private boolean defaultAddress;

    /**
     * Default constructor.
     */
    public Address() {
        this.id = 0;
        this.defaultAddress = true;
        this.postalCode = "";
        this.address = "";
        this.city = "";
        this.country = "";
        this.province = "";
    }

    /**
     * Parametrized constructor.
     *
     * @param id Integer id from Address in Database.
     */
    public Address(Integer id) {
        this.id = id;
    }

    /**
     * Parametrized constructor with all object properties. It excludes the latitude
     * and longitude because Google API should return its values.
     *
     * @param id Integer id.
     * @param address String dirección.
     * @param city String ciudad.
     * @param country String pais.
     * @param province String provincia.
     * @param postalCode String código postal.
     * @param defaultAddress boolean parámetro que indica si es la dirección
     * principal o no.
     */
    public Address(Integer id, String address, String city, String country, String province, String postalCode, boolean defaultAddress) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.country = country;
        this.province = province;
        this.postalCode = postalCode;
        this.defaultAddress = defaultAddress;
    }

    /**
     * Getter of id.
     *
     * @return Integer id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter of id.
     *
     * @param id Integer.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for Address.
     *
     * @return String address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter for Addres.
     *
     * @param address String.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Getter for city.
     *
     * @return String city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for city.
     *
     * @param city String.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter of country.
     *
     * @return String country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setter for country.
     *
     * @param country String.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Getter of province.
     *
     * @return String province.
     */
    public String getProvince() {
        return province;
    }

    /**
     * Setter of province.
     *
     * @param province String.
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Getter for postalCode.
     *
     * @return String postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Setter for postalCode.
     *
     * @param postalCode String postalCode.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Getter for longitude.
     *
     * @return Double longitude.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Setter for longitude.
     *
     * @param longitude Double.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for latitude.
     *
     * @return Double latitude.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Setter for latitude.
     *
     * @param latitude Double.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter of boolean that indicates if it's the default address.
     * 
     * @return boolean defaultAddress.
     */
    public boolean getDefaultAddress() {
        return defaultAddress;
    }

    /**
     * Setter of boolean that indicates if it's the default address.
     *
     * @param defaultAddress boolean.
     */
    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
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
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    /**
     * Method which returns the formatted coordinates.
     * 
     * @return String formatted coordinates.
     */
    public String getCoordinatesForMap() {
        return getLatitude() + "," + getLongitude();
    }

    /**
     * Method that returns the full formatted address.
     * 
     * @return String full address.
     */
    public String getFullAddress() {
        return (!"".equals(getAddress()) ? getAddress() + "," : "")
                + (!"".equals(getCity()) ? getCity() + "," : "")
                + (!"".equals(getProvince()) ? getProvince() + "," : "")
                + (!"".equals(getCountry()) ? getCountry() + "," : "")
                + (!"".equals(getPostalCode()) ? getPostalCode() : "");
    }
}
