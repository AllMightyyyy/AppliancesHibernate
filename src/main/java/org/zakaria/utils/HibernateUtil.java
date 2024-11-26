package org.zakaria.utils;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the sessionFactory out of the hibernate.cfg.xml config file
            return new Configuration().configure().buildSessionFactory();
        } catch ( Throwable t ) {
            System.err.println("Initial SessionFactory creation failed." + t);
            throw new ExceptionInInitializerError(t);
        }
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
