/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gestion;

import java.math.BigDecimal;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pojos.Customer;
import pojos.Film;
import pojos.Inventory;
import pojos.Payment;
import pojos.Rental;
import pojos.Staff;
import utils.HibernateUtil;

/**
 *
 * @author Joaquín Pereira Chapel
 */
public class Conexion {
    Session session;
    Transaction trans;

    public Conexion() throws Exception {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }

    public void cerrarSesion() throws Exception {
        try {
            session.close();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }

    public void empezarTransaccion() throws Exception {
        try {
            session.beginTransaction();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }

    public void completarTransaccion() throws Exception {
        try {
            session.getTransaction().commit();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }

    public void dehacerTransaccion() throws Exception {
        try {
            session.getTransaction().rollback();
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }
    
    public Film leerFilm(Short film_id) throws Exception {
        try {
            return (Film) session.get(Film.class, film_id);
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }
    
    public void borrarFilm(Film film) throws Exception {
        try {
            session.delete(film);
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }
    public Inventory leerInventory(Integer inventory) throws Exception {
        try {
            return (Inventory) session.get(Inventory.class, inventory);
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }
    public Customer leerCustomer(Short cust) throws Exception {
        try {
            return (Customer) session.get(Customer.class, cust);
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }

    public Staff leerStaff(Byte staff) throws Exception {
        try {
            return (Staff) session.get(Staff.class, staff);
        } catch (HibernateException ex) {
            ex.printStackTrace();
            throw new Exception(ex);
        }
    }
    
    public Rental grabarRental(Rental rental) throws Exception {
        try {
            session.save(rental);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        
        return rental;
    }

    public Rental updateRental(Rental rental) throws Exception {
        try {
            session.update(rental);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        
        return rental;
    }

    public Payment grabarPayment(Payment payment) throws Exception {
        try {
            session.save(payment);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        
        return payment;
    }
    public List<Customer> listarMejoresClientes(long operaciones, BigDecimal facturacion) throws Exception {
        try {
            Query q = session.createQuery("select p.customer from Payment p "
                    + "group by p.customer "
                    + "having count(distinct p.rental) >= :operaciones "
                    + "and sum(p.amount) >= :facturacion");
            List<Customer> lista = q.setParameter("operaciones", operaciones).setParameter("facturacion", facturacion).list();
            
            if (lista.isEmpty()) {
                return null;
            } else {
                return lista;
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    public Customer obtenerCustomerInventoryPrestado(int inventory_id) throws Exception {
        try {
            Query q = session.createQuery("select r.customer"
                    + " from Rental r "
                    + "where r.returnDate is null "
                    + "and r.inventory.inventoryId = :inventory_id");
            q.setParameter("inventory_id", inventory_id);
            q.setMaxResults(1);
            return (Customer) q.uniqueResult();

        } catch (HibernateException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    
     public String[] obtenerDatosCustomerInventoryPrestado(int inventory_id) throws Exception {
        try {
            Query q = session.createQuery("select r.customer.firstName, "
                    + "r.customer.lastName, r.customer.email, r.customer.address.phone"
                    + " from Rental r where r.returnDate is null and r.inventory.inventoryId = :inventory_id");
            q.setParameter("inventory_id", inventory_id);
            q.setMaxResults(1);

            List<Object[]> lista = q.list();

            if (!lista.isEmpty()) {
                String[] datos = new String[4];

                for (int i = 0; i < 4; i++) {
                    datos[i] = (String) lista.get(0)[i];
                }
                return datos;
            }

        } catch (HibernateException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return null;
    }
    
    public List<Object[]> listarPrestamosSinDevolver() throws Exception {
        try {
            Query q = session.createQuery("select r.customer.firstName, "
                    + "r.customer.lastName, r.customer.email, r.customer.address.phone, r.inventory.film.title"
                    + " from Rental r where r.returnDate is null "
                    + "and  DATEDIFF (current_date, r.rentalDate) >  r.inventory.film.rentalDuration");
            
            List<Object[]> lista = q.list();
            
            if (!lista.isEmpty()) {
                return lista;
            } else {
                return null;
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
