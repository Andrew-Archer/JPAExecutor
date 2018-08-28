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

    public synchronized static <T> T executeQuery(
            Function<EntityManager, T> function,
            EntityManagerFactory emf) {
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
     * @param emf фабрика для создания EntityManager.
     * @return персистентная сущность.
     */
    public static <T> T merge(T entity, EntityManagerFactory emf) {
        return executeQuery((em) -> {
            return em.merge(entity);
        }, emf);
    }

    /**
     * Создает новую сущность в базе данных.
     *
     * @param <T> тип сущности.
     * @param entity сама сущность для создания в базе.
     * @param emf фабрика для создания EntityManager.
     * @return персистентная сущность.
     */
    public static <T> T persist(T entity, EntityManagerFactory emf) {
        return executeQuery((em) -> {
            em.persist(entity);
            return entity;
        }, emf);
    }
}
