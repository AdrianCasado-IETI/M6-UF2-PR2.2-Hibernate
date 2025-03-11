package com.project;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.hibernate.cfg.Configuration;

public class Manager {
    private static SessionFactory factory; 

    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            // Important: Afegim les classes anotades
            configuration.addAnnotatedClass(Ciutat.class);
            configuration.addAnnotatedClass(Ciutada.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex); 
        }
    }

    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }

    public static Ciutat addCiutat(String nom, String pais, int habitants) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = null;
        Ciutat ciutat = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            ciutat = new Ciutat(nom, pais, habitants);
            entityManager.persist(ciutat);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return ciutat;
    }

    public static Ciutada addCiutada(String nom, String cognom, int edat) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = null;
        Ciutada ciutada = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            ciutada = new Ciutada(nom, cognom, edat);
            entityManager.persist(ciutada);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return ciutada;
    }

    public static void updateCiutat(long ciutatId, String nom, String pais, int poblacio, Set<Ciutada> ciutadans) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            Ciutat ciutat = entityManager.find(Ciutat.class, ciutatId);
    
            if (ciutat == null) {
                throw new RuntimeException("Ciutat not found with id " + ciutatId);
            }
    
            ciutat.setNom(nom);
            ciutat.setPais(pais);
            ciutat.setPoblacio(poblacio);
    
            // Clear the existing collection
            ciutat.getCiutadans().clear();
    
            // Add the new elements to the collection
            if (ciutadans != null) {
                for (Ciutada ciutada : ciutadans) {
                    Ciutada managedCiutat = entityManager.find(Ciutada.class, ciutada.getCiutadaId());
                    if (managedCiutat != null) {
                        ciutat.addCiutada(managedCiutat);
                    }
                }
            }
    
            entityManager.merge(ciutat);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
    

    public static void updateCiutada(long ciutadaId, String nom, String cognom, int edat) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            Ciutada ciutada = entityManager.find(Ciutada.class, ciutadaId);
            ciutada.setNom(nom);
            ciutada.setCognom(cognom);
            ciutada.setEdat(edat);
            entityManager.merge(ciutada);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public static Ciutat getCiutatWithCiutadans(long ciutatId) {
        EntityManager entityManager = factory.createEntityManager();
        Ciutat ciutat = null;
        try {
            ciutat = entityManager.find(Ciutat.class, ciutatId);
            ciutat.getCiutadans().size(); // Initialize the collection
        } finally {
            entityManager.close();
        }
        return ciutat;
    }

    public static <T> T getById(Class<? extends T> clazz, long id) {
        EntityManager entityManager = factory.createEntityManager();
        T obj = null;
        try {
            obj = entityManager.find(clazz, id);
        } finally {
            entityManager.close();
        }
        return obj;
    }

    public static <T> void delete(Class<? extends T> clazz, Serializable id) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            T obj = entityManager.find(clazz, id);
            if (obj != null) {
                entityManager.remove(obj);
                tx.commit();
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where) {
        EntityManager entityManager = factory.createEntityManager();
        Collection<?> result = null;
        try {
            String jpql = "SELECT e FROM " + clazz.getName() + " e";
            if (!where.isEmpty()) {
                jpql += " WHERE " + where;
            }
            result = entityManager.createQuery(jpql, clazz).getResultList();
        } finally {
            entityManager.close();
        }
        return result;
    }

    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection) {
        StringBuilder txt = new StringBuilder();
        for (Object obj : collection) {
            T cObj = clazz.cast(obj);
            txt.append("\n").append(cObj.toString());
        }
        if (txt.length() > 0 && txt.charAt(0) == '\n') {
            txt.deleteCharAt(0);
        }
        return txt.toString();
    }

    public static void queryUpdate(String queryString) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            entityManager.createNativeQuery(queryString).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    public static List<Object[]> queryTable(String queryString) {
        EntityManager entityManager = factory.createEntityManager();
        List<Object[]> result = null;
        try {
            result = entityManager.createNativeQuery(queryString, Object[].class).getResultList();
        } finally {
            entityManager.close();
        }
        return result;
    }
}
