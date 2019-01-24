/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.agrupados.beans;

import java.util.List;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Abstract Facade Class with generic CRUD methods to manage 
 * transactions in database.
 * @author Diego
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    /**
     * Parametrized constructor that initializes the entityClass instance.
     * @param entityClass
     */
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Abstract getter method for the EntityManager
     * @return EntityManager
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Creates entity.
     * @param entity
     */
    public void create(T entity) {
        try {
            getEntityManager().persist(entity);
        } catch (ConstraintViolationException e) {
            // Aqui imprime los errores de constraint  en caso  de que haya errorres.
            for (ConstraintViolation actual : e.getConstraintViolations()) {
                System.out.println(actual.toString());
            }
        }
    }

    /**
     * Edits entity.
     * @param entity
     */
    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    /**
     * Removes entity.
     * @param entity
     */
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * Find an entity by ID.
     * @param id
     * @return Object
     */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Finds all entities-
     * @return List
     */
    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Finds entity in a range
     * @param range
     * @return List
     */
    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
