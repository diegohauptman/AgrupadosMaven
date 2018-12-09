/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.persistence;

/**
 *
 * @author mundakamacbook
 */
public enum Order {
    
    ASC, DESC;

	
	public boolean isAscOrder() {
		return ASC.equals(this);
	}
    
}
