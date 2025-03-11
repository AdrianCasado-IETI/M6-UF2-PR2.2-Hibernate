package com.project;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;

public class Manager {
    private static SessionFactory factory;

    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            configuration.addResource("Ciutat.hbm.xml");
            configuration.addResource("Ciutada.hbm.xml");

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex); 
        }
    }

    public static void close () {
        factory.close();
    }
    
    public static Ciutat addCiutat(String nom, String pais, int habitants) {
        Session session = factory.openSession();
        Transaction tx = null;
        Ciutat ciutat = null;
        try {
            tx = session.beginTransaction();
            ciutat = new Ciutat(nom, pais, habitants);
            session.persist(ciutat);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutat;
    }

    public static Ciutada addCiutada(String nom, String cognom, int edat) {
        Session session = factory.openSession();
        Transaction tx = null;
        Ciutada ciutada = null;
        try {
            tx = session.beginTransaction();
            ciutada = new Ciutada(nom, cognom, edat);
            session.persist(ciutada);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutada;
    }

    public static void updateCiutat(long ciutatId, String nom, String pais, int poblacio, Set<Ciutada> ciutadans) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Ciutat ciutat = session.get(Ciutat.class, ciutatId);

            if(ciutat == null) {
                throw new RuntimeException("Ciutat not found with id " + ciutatId);
            }

            ciutat.setNom(nom);
            ciutat.setPais(pais);
            ciutat.setPoblacio(poblacio);
            ciutat.setCiutadans(ciutadans);

            if(ciutat.getCiutadans() != null) {
                for(Ciutada ciutada : new HashSet<>(ciutat.getCiutadans())) {
                    ciutat.removeCiutada(ciutada);
                }
            }

            if(ciutadans != null) {
                for(Ciutada ciutada : ciutadans) {
                    Ciutada managedCiutat = session.get(Ciutada.class, ciutada.getCiutadaId());
                    if(managedCiutat != null) {
                        ciutat.addCiutada(managedCiutat);
                    }
                }
            }

            session.merge(ciutat);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void updateCiutada(long ciutadaId, String nom, String cognom, int edat) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Ciutada ciutada = session.get(Ciutada.class, ciutadaId);
            ciutada.setNom(nom);
            ciutada.setCognom(cognom);
            ciutada.setEdat(edat);
            session.merge(ciutada);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static Ciutat getCiutatWithCiutadans(long ciutatId) {
        
        Ciutat ciutat = null;
        try(Session session = factory.openSession();) {
            Transaction tx = session.beginTransaction();
            ciutat = session.get(Ciutat.class, ciutatId);
            ciutat.getCiutadans().size();
            tx.commit();
        }
        return ciutat;
    }

    public static <T> T getById(Class<? extends T> clazz, long id){
        Session session = factory.openSession();
        Transaction tx = null;
        T obj = null;
        try {
            tx = session.beginTransaction();
            obj = clazz.cast(session.get(clazz, id)); 
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
        return obj;
    }

    public static <T> void delete(Class<? extends T> clazz, Serializable id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            T obj = clazz.cast(session.get(clazz, id));
            if (obj != null) {  
                session.remove(obj);
                tx.commit();
            }
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where){
        Session session = factory.openSession();
        Transaction tx = null;
        Collection<?> result = null;
        try {
            tx = session.beginTransaction();
            if (where.length() == 0) {
                result = session.createQuery("FROM " + clazz.getName(), clazz).list();
            } else {
                result = session.createQuery("FROM " + clazz.getName() + " WHERE " + where, clazz).list();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
        return result;
    }

    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection){
        String txt = "";
        for(Object obj: collection) {
            T cObj = clazz.cast(obj);
            txt += "\n" + cObj.toString();
        }
        if (txt.length() > 0 && txt.substring(0, 1).compareTo("\n") == 0) {
            txt = txt.substring(1);
        }
        return txt;
    }

    public static void queryUpdate(String queryString) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            NativeQuery<?> query = session.createNativeQuery(queryString, Void.class); // Updated to NativeQuery
            query.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
    }

    public static List<Object[]> queryTable(String queryString) {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Object[]> result = null;
        try {
            tx = session.beginTransaction();
            NativeQuery<Object[]> query = session.createNativeQuery(queryString, Object[].class); // Updated to NativeQuery
            result = query.getResultList(); // Changed from list() to getResultList()
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
        return result;
    }
}