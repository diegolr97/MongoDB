/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fachada;

import javax.swing.table.DefaultTableModel;
import modelo.modelo;

/**
 *
 * @author Alumno
 */
        public class fachada {
            modelo m  =  new modelo();
    
    
    //-------------Clientes---------------//
    
        public void insertarCliente(String cod, String nombre, String poblacion){
            m.insertarCliente(cod, nombre, poblacion);
        }
        public void borrarCliente(String cod){
            m.borrarCliente(cod);
        }
        public void actualizarCliente(String cod, String nombre, String poblacion){
            m.actualizarCliente(cod, nombre, poblacion);
        }
       
        public DefaultTableModel listarClientes(){
            return m.listarClientes();
        }
        
    //-----------Articulos------------//
        
        public void insertarArticulo(String id, String denominacion, String precio, String stock){
            m.insertarArticulo(id, denominacion, precio, stock);
            
        }
        
        public void borrarArticulo(String id){
            m.borrarArticulo(id);
        }
        
        public void actualizarArticulo(String id, String denominacion, String precio, String stock){
             m.actualizarArticulo(id, denominacion, precio, stock);
         }
        
        public DefaultTableModel listarArticulos(){
            return m.listarArticulos();
        }
         
         
    
    
    
}
