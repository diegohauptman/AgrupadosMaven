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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class which does calls to the Google Maps API.
 *
 * @author Diego
 */
public class CoordinatesService {

    private static final String GEOCODER_REQUEST_PREFIX_FOR_XML = "https://maps.google.com/maps/api/geocode/xml";
    private static final String API_KEY = "AIzaSyBr1K6D8AC32CB_QpfonnJief62NkErOWA";
    private static final Logger LOGGER = Logger.getLogger(CoordinatesService.class.getName());

    /**
     * Method which returns an array of doubles with the coordinates
     * 
     * @param address String full address search.
     * @return double[] coordinates [lat/long].
     */
    public double[] getLatitudeLongitude(String address) {
        //Mostramos la dirección completa formateada por consola.
        LOGGER.info("getLatitudeLongitude ***** Address: " + address);
        //Inicialización de variables.
        double[] coordinates = new double[2];
        HttpURLConnection conn = null;
        NodeList resultNodeList;
        Document geocoderResultDocument;
        try {
            //Build the URL for the Google Maps API
            URL url = new URL(GEOCODER_REQUEST_PREFIX_FOR_XML + "?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true" + "&key=" + API_KEY);
            LOGGER.info("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputSource geocoderResultInputSource = new InputSource(conn.getInputStream());
            //Get the results from the request.
            geocoderResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(geocoderResultInputSource);
            //Use XPATH to analyze the result in a simple way.
            XPath xpath = XPathFactory.newInstance().newXPath();
            //Get the specific results that we need.
            resultNodeList = (NodeList) xpath.evaluate("/GeocodeResponse/result[1]/geometry/location/*", geocoderResultDocument, XPathConstants.NODESET);
            float lat = Float.NaN;
            float lng = Float.NaN;
            //Traverse the results.
            for (int i = 0; i < resultNodeList.getLength(); ++i) {
                Node node = resultNodeList.item(i);
                //Print the nodes and values in console
                System.out.println("Node: " + node.getNodeName() + " Value: " + node.getTextContent());
                //Get the latitude
                if ("lat".equals(node.getNodeName())) {
                    lat = Float.parseFloat(node.getTextContent());
                }
                //Get the longitude
                if ("lng".equals(node.getNodeName())) {
                    lng = Float.parseFloat(node.getTextContent());
                }
            }
            /**
             * 
             * It's supposed to one single result and it will be the last
             * and the only one stored. We could create an array with all the results
             * in case there was more than one match.
             * 
             */
            coordinates[0] = lat;
            coordinates[1] = lng;
            //Mostramos todo de nuevo por consola.
            LOGGER.info("Address with long/lat: " + address + " lat/lng=" + lat + "," + lng);
        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException ex) {
            Logger.getLogger(CoordinatesService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return coordinates;
    }

    /**
     * Method which returns an address by coordinates criteria.
     *
     * @param latitude Double latitude.
     * @param longitude Double longitude.
     * @return String unformatted full address.
     */
    public String getAddress(Double latitude, Double longitude) {
        String address = null;
        HttpURLConnection conn = null;
        NodeList resultNodeList;
        Document geocoderResultDocument;
        try {
            URL url = new URL(GEOCODER_REQUEST_PREFIX_FOR_XML + "?latlng=" + latitude + "," + longitude + "&sensor=false");
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputSource geocoderResultInputSource = new InputSource(conn.getInputStream());
            geocoderResultDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(geocoderResultInputSource);
            XPath xpath = XPathFactory.newInstance().newXPath();
            resultNodeList = (NodeList) xpath.evaluate("/GeocodeResponse/result[1]/*", geocoderResultDocument, XPathConstants.NODESET);
            for (int i = 0; i < resultNodeList.getLength(); ++i) {
                Node node = resultNodeList.item(i);
                if ("formatted_address".equals(node.getNodeName())) {
                    address = (node.getTextContent());
                    System.err.println("Dirección: " + address + "[" + latitude + "," + longitude + "]");
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException ex) {
            Logger.getLogger(CoordinatesService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return address;
    }
}
