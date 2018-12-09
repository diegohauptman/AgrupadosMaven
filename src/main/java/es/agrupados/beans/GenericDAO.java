/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import es.agrupados.persistence.Order;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.NotFoundException;


/**
 * A generic dao implementation based solely on JPA.
 * 
 * <p>Comparing to the Hibernate version, the main difference is Criteria
 * API. JPA's criteria is a lot more verbose. Type safety comes at a price.
 * 
 * <p>Please note that this implementation is not type safe, properties are passed
 * as String's. Probably something that could be better since that's what JPA's criteria
 * API is all about.
 * 
 * @author Rodrigo Uchôa (http://rodrigouchoa.wordpress.com)
 *
 */
@Stateless
public class GenericDAO {
	
	/*
	 * A "copy" of the Hibernate's API as this doesn't exist
	 * in JPA.
	 */
	public enum MatchMode { START, END, EXACT, ANYWHERE }

        @PersistenceContext(unitName = "AgrupadosPU")
	private EntityManager entityManager;

	/**
	 * Saves an entity.
	 * 
	 * @param entity
	 * @return newly created id for the entity.
	 */
	public <T> void save(T entity) {
		entityManager.persist(entity);
	}


	/**
	 * Marges objects with the same identifier within a session into a newly
	 * created object.
	 * 
	 * @param entity
	 * @return a newly created instance merged.
	 */
	public <T> T merge(T entity) {
		return entityManager.merge(entity);
	}

	/**
	 * Deletes tne entity.
	 * 
	 * @param clazz
	 * @param id
	 * @throws NotFoundException if the id does not exist.
	 */
	public <T> void delete(Class<T> clazz, Serializable id) {
		T entity = find(clazz, id);
		if (entity != null) {
			entityManager.remove(entity);
		} else {
			throw new NotFoundException();
		}
	}

	/**
	 * Find an entity by its identifier.
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T> T find(Class<T> clazz, Serializable id) {
		return entityManager.find(clazz, id);
	}

	/**
	 * Finds an entity by one of its properties.
	 * 
	 * 
	 * @param clazz the entity class.
	 * @param propertyName the property name.
	 * @param value the value by which to find.
	 * @return
	 */
	public <T> List<T> findByProperty(Class<T> clazz, String propertyName, Object value) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		cq.where(cb.equal(root.get(propertyName), value));
		return entityManager.createQuery(cq).getResultList();
	}
	
	/**
	 * Finds entities by a String property specifying a MatchMode. This search 
	 * is case insensitive.
	 * 
	 * @param clazz the entity class.
	 * @param propertyName the property name.
	 * @param value the value to check against.
	 * @param matchMode the match mode: EXACT, START, END, ANYWHERE.
	 * @return
	 */
	public <T> List<T> findByProperty(Class<T> clazz, String propertyName, String value, MatchMode matchMode) {
		//convert the value String to lowercase
		value = value.toLowerCase();
		if (MatchMode.START.equals(matchMode)) {
			value = value + "%";
		} else if (MatchMode.END.equals(matchMode)) {
			value = "%" + value;
		} else if (MatchMode.ANYWHERE.equals(matchMode)) {
			value = "%" + value + "%";
		}
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		cq.where(cb.like(cb.lower(root.get(propertyName)), value));
		
		return entityManager.createQuery(cq).getResultList();
	}
	
	

	/**
	 * Finds all objects of an entity class.
	 * 
	 * @param clazz the entity class.
	 * @return
	 */
	public <T> List<T> findAll(Class<T> clazz) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		cq.from(clazz);
		return entityManager.createQuery(cq).getResultList();
	}

	/**
	 * Finds all objects of a class by the specified order.
	 * 
	 * @param clazz the entity class.
	 * @param order the order: ASC or DESC.
	 * @param propertiesOrder the properties on which to apply the ordering.
	 * 
	 * @return
	 */
	public <T> List<T> findAll(Class<T> clazz, Order order, String... propertiesOrder) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		
		List<javax.persistence.criteria.Order> orders = new ArrayList<>();
		for (String propertyOrder : propertiesOrder) {
			if (order.isAscOrder()) {
				orders.add(cb.asc(root.get(propertyOrder)));
			} else {
				orders.add(cb.desc(root.get(propertyOrder)));
			}
		}
		cq.orderBy(orders);

		return entityManager.createQuery(cq).getResultList();
	}

}