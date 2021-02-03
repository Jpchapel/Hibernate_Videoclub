/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestion;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.Customer;
import pojos.Film;
import pojos.Inventory;
import pojos.Payment;
import pojos.Rental;
import pojos.Staff;

/**
 *
 * @author Joaquín Pereira Chapel
 */
public class Videoclub {

    public void imprimirFilm(Short filmId) {
        Conexion conexion = null;
        try {
            conexion = new Conexion();
            Film f = conexion.leerFilm(filmId);
            System.out.println("Titulo " + f.getTitle());
            System.out.println("Year " + f.getReleaseYear());
            System.out.println("");
        } catch (Exception ex) {
            Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void borrarFilm(Short film_id) {
        Conexion conexion = null;
        try {
            conexion = new Conexion();
            conexion.empezarTransaccion();

            Film f = conexion.leerFilm(film_id);

            if (f != null) {
                conexion.borrarFilm(f);
            }

            conexion.completarTransaccion();

        } catch (Exception ex) {
            try {
                conexion.dehacerTransaccion();
            } catch (Exception ex1) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List listarMejoresClientes(long minimoOperacions, BigDecimal minimoFacturacion) {
        Conexion conexion = null;

        try {
            conexion = new Conexion();
            return conexion.listarMejoresClientes(minimoOperacions, minimoFacturacion);

        } catch (Exception ex) {
            Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String[] obtenerIdClientePrestamo(int inventory_id) {
        String[] datos = null;
        Conexion conexion = null;

        try {
            conexion = new Conexion();
            Customer c = conexion.obtenerCustomerInventoryPrestado(inventory_id);

            if (c != null) {
                datos = new String[4];
                
                getDatos(datos, c);
                
                return datos;
            }
        } catch (Exception ex) {
            Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return datos;
    }

    private void getDatos(String[] datos, Customer c) {
        datos[0] = c.getFirstName();
        datos[1] = c.getLastName();
        datos[2] = c.getAddress().getPhone();
        datos[3] = c.getEmail();
    }

    public String[] obtenerDatosClientePrestamo(int inventory_id) {
        Conexion conexion = null;

        try {
            conexion = new Conexion();
            return conexion.obtenerDatosCustomerInventoryPrestado(inventory_id);

        } catch (Exception ex) {
            Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
    
    public void alquilarDvd(Integer inventory_id, Short customer_id, Byte staff_id) {
        Conexion conexion = null;

        try {
            conexion = new Conexion();
            conexion.empezarTransaccion();

            Inventory inventario = conexion.leerInventory(inventory_id);

            if (inventario != null) {
                comprobarPrestamo(inventario, conexion, customer_id, staff_id);
            }

            conexion.completarTransaccion();

        } catch (Exception ex) {
            try {
                conexion.dehacerTransaccion();
            } catch (Exception ex1) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void comprobarPrestamo(Inventory inventario, Conexion conexion, Short customer_id, Byte staff_id) throws Exception {
        estaPrestado(inventario);
        
        if (!estaPrestado(inventario)) {
            
            comprobarCustomerAlquiler(conexion, customer_id, staff_id, inventario);
        }
    }
    private boolean estaPrestado(Inventory inventario) {
        //Buscamos el ultimo rental para ver si esta prestado
        boolean prestado = false;
        for (Rental r : (Set<Rental>) inventario.getRentals()) {
            if (r.getReturnDate() == null) {
                prestado = true;
                break;
            }
        }
        return prestado;
    }

    private void comprobarCustomerAlquiler(Conexion conexion, Short customer_id, Byte staff_id, Inventory inventario) throws Exception {
        Customer customer = conexion.leerCustomer(customer_id);
        
        if (customer != null) {
            comprobarStaffAlquiler(conexion, staff_id, customer, inventario);            
        }
    }

    private void comprobarStaffAlquiler(Conexion conexion, Byte staff_id, Customer customer, Inventory inventario) throws Exception {
        Staff staff = conexion.leerStaff(staff_id);
        
        if (staff != null) {
            agregarAlquiler(customer, staff, inventario, conexion);
        }
    }
    
    private void agregarAlquiler(Customer cust, Staff staff, Inventory inventario, Conexion conexion) throws Exception {
        Rental alquiler = new Rental(cust, staff, inventario, new Date(), new Date());
        alquiler = conexion.grabarRental(alquiler);
        cust.getRentals().add(alquiler);
        inventario.getRentals().add(alquiler);
        staff.getRentals().add(alquiler);
    }
    
    public void devolverDvd(Integer inventory_id, Short customer_id, Byte staff_id) {
        Conexion conexion = null;

        try {
            conexion = new Conexion();
            conexion.empezarTransaccion();

            Inventory inventario = conexion.leerInventory(inventory_id);

            if (inventario != null) {
                comprobarCustomerDevolver(conexion, customer_id, staff_id, inventario);
            }

            conexion.completarTransaccion();

        } catch (Exception ex) {
            try {
                conexion.dehacerTransaccion();
            } catch (Exception ex1) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void comprobarCustomerDevolver(Conexion conexion, Short customer_id, Byte staff_id, Inventory inventario) throws Exception {
        Customer customerDevolver = conexion.leerCustomer(customer_id);
        if (customerDevolver != null) {
            comprobarStaffDevolucion(conexion, staff_id, inventario, customerDevolver);
        }
    }

    private void comprobarStaffDevolucion(Conexion conexion, Byte staff_id, Inventory inventario, Customer cust) throws Exception {
        Staff staff = conexion.leerStaff(staff_id);
        if (staff != null) {
            agregarDevolucion(inventario, cust, conexion, staff);
        }
    }

    private void agregarDevolucion(Inventory inventario, Customer cust, Conexion conexion, Staff staff) throws Exception {
        for (Rental rental : (Set<Rental>) inventario.getRentals()) {
            if (rental.getReturnDate() == null) {
                if (rental.getCustomer().getCustomerId().equals(cust.getCustomerId())) {
                    cambiarFechas(rental, conexion);
                    
                    Payment p = new Payment(cust, rental, staff, inventario.getFilm().getRentalRate(), new Date(), new Date());
                    conexion.grabarPayment(p);
                    
                    cust.getPayments().add(p);
                    staff.getPayments().add(p);
                    rental.getPayments().add(p);
                    
                    if (calcularDiferenciaDias(rental.getRentalDate(), rental.getReturnDate()) > inventario.getFilm().getRentalDuration()) {
                        calcularPenalizacion(rental, inventario, cust, staff, conexion);
                    }
                    
                    break;
                }
            }
        }
    }

    private void cambiarFechas(Rental rental, Conexion conexion) throws Exception {
        rental.setReturnDate(new Date());
        rental.setLastUpdate(new Date());
        conexion.updateRental(rental);
    }

    private void calcularPenalizacion(Rental rental, Inventory inventario, Customer cust, Staff staff, Conexion conexion) throws Exception {
        BigDecimal calculo = new BigDecimal(calcularDiferenciaDias(rental.getReturnDate(), rental.getRentalDate()) - inventario.getFilm().getRentalDuration());
        
        Payment penalizacion = new Payment(cust, rental, staff, calculo, new Date(), new Date());
        
        conexion.grabarPayment(penalizacion);
        
        cust.getPayments().add(penalizacion);
        staff.getPayments().add(penalizacion);
        rental.getPayments().add(penalizacion);
    }
    private long calcularDiferenciaDias(Date data1, Date data2) {

        LocalDateTime localdata1 = LocalDateTime.ofInstant(data1.toInstant(), ZoneId.systemDefault());
        LocalDateTime localdata2 = LocalDateTime.ofInstant(data2.toInstant(), ZoneId.systemDefault());

        return Duration.between(localdata1, localdata2).toDays();
    }
    
    public void listaFueraPlazo() {
        Conexion conexion = null;

        try {
            conexion = new Conexion();
            conexion.listarPrestamosSinDevolver();

        } catch (Exception ex) {
            Logger.getLogger(Videoclub.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conexion.cerrarSesion();
            } catch (Exception ex) {
                Logger.getLogger(Videoclub.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
