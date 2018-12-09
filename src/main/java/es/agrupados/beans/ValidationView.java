/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import java.time.LocalDate;
import javax.inject.Named;

/**
 *
 * @author mundakamacbook
 */
@Named(value = "validationView")
public class ValidationView {
    
    private String text;
    private String description;
    private Integer integer;
    private Double doubleNumber;
    private Double money;
    private String regexText;
    private LocalDate date;
 
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
 
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
 
    public Integer getInteger() {
        return integer;
    }
    public void setInteger(Integer integer) {
        this.integer = integer;
    }
 
    public Double getDoubleNumber() {
        return doubleNumber;
    }
    public void setDoubleNumber(Double doubleNumber) {
        this.doubleNumber = doubleNumber;
    }
 
    public Double getMoney() {
        return money;
    }
    public void setMoney(Double money) {
        this.money = money;
    }
 
    public String getRegexText() {
        return regexText;
    }
    public void setRegexText(String regexText) {
        this.regexText = regexText;
    }
 
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    } 
}
