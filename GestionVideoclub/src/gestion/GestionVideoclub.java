/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gestion;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Joaquín Pereira Chapel
 */
public class GestionVideoclub {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Videoclub videoclub = new Videoclub();
        videoclub.imprimirFilm((short) 1);
        videoclub.borrarFilm((short) 1);
        List lista = videoclub.listarMejoresClientes(30, new BigDecimal(200));
        String[] idCliente = videoclub.obtenerIdClientePrestamo(2047);
        String[] datosCliente = videoclub.obtenerDatosClientePrestamo(2047);
        videoclub.alquilarDvd(12, (short) 1, (byte) 2);
        videoclub.devolverDvd(12, (short) 1, (byte) 2);
        videoclub.listaFueraPlazo();
    }
    
}
