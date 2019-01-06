/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.jsf;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author mundakamacbook
 */
@Named("addtoCart")
@SessionScoped
public class AddToCartBean implements Serializable {
    
    private String buttonValue;
    private int counter;
    
    @PostConstruct
    public void init(){
        this.buttonValue = "Add to Cart";
        counter = 0;
    }

    public String getButtonValue() {
        return buttonValue;
    }
    
    public void added(){
       counter++; 
       buttonValue = "Added " + counter;
    }
    
    
}