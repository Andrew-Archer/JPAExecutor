package com.gmail.razandale.jpa;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 *
 * @author RazumnovAA
 */
public class JPAExecutor {

    private final EntityManagerFactory emf;
    
    public JPAExecutor(EntityManagerFactory emf){
        this.emf = emf;
    }

    public synchronized <T> T executeQuery(
            Function<EntityManager, T> function) {
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction trans = em.getTransaction();
            try {
                return function.apply(em);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            } finally {
                if (trans.isActive()) {
                    trans.rollback();
                }
                if (em != null) {
                    em.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Обновляет или создает новую сущность в базе.
     *
     * @param <T> тип сущности.
     * @param entity сама сущность для создания в базе.
     * @return персистентная сущность.
     */
    public <T> T merge(T entity) {
        return executeQuery((em) -> {
            return em.merge(entity);
        });
    }

    /**
     * Создает новую сущность в базе данных.
     *
     * @param <T> тип сущности.
     * @param entity сама сущность для создания в базе.
     * @return персистентная сущность.
     */
    public <T> T persist(T entity) {
        return executeQuery((em) -> {
            em.persist(entity);
            return entity;
        });
    }
}
