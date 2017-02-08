/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;


/**
 *
 * @author Usuario
 */
        public class modelo {
            MongoCollection<Document> clientes;
            MongoCollection<Document> articulos;
            MongoCollection<Document> ventas;
    
    
    public modelo(){
            MongoClient cliente = new MongoClient("localhost",27017);
            MongoDatabase db = cliente.getDatabase("ventas");
            clientes = db.getCollection("clientes");
            articulos = db.getCollection("articulos");
            ventas = db.getCollection("ventas");
        }
    //-----------Clientes------------//
    
        public void insertarCliente(String cod, String nombre, String poblacion){
            Document cliente =new Document();
            cliente.put("_id", cod);
            cliente.put("nombre", nombre);
            cliente.put("poblacion", poblacion);
            clientes.insertOne(cliente);
                
    }
    
        public void borrarCliente(String cod){
            clientes.deleteOne(eq("_id",cod));
    }
    
        public void actualizarCliente(String cod, String nombre, String poblacion){
            clientes.updateOne(eq("_id",cod), set("nombre",nombre));
            clientes.updateOne(eq("_id",cod), set("poblacion",poblacion));
    }
        
        public DefaultTableModel listarClientes(){
        DefaultTableModel tab=new DefaultTableModel(){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        String column[]={"codigo", "nombre", "poblacion", "num compras", " gastado en total"};
        tab.setColumnIdentifiers(column);
        List<Document> list=clientes.find().into(new ArrayList<Document>());
        for(int i=0; i<list.size(); i++){
            Document a=list.get(i);
            long count = ventas.count(eq("codcliente", a.getString("_id")));
            List <Document> d=ventas.find(eq("codcliente", a.getString("_id"))).into(new ArrayList<Document>());
            double sum=0;
            for(Document d1:d){
                sum+=d1.getInteger("unidades")*articulos.find(eq("_id",d1.getString("codarticulo"))).first().getDouble("pvp");
            }
            Object data[]={a.getString("_id"), a.getString("nombre"), a.get("poblacion"), count, sum};
            tab.addRow(data);
        }
        return tab;
    }
        
    //--------Articulos-------//
    
        public void insertarArticulo(String id, String denominacion, String precio, String stock){
         Document articulo = new Document();
         articulo.put("_id", id);
         articulo.put("denominacion", denominacion);
         articulo.put("pvp", Double.parseDouble(precio));
         articulo.put("stock", Integer.parseInt(stock));
         articulos.insertOne(articulo);
     }
    
        public void borrarArticulo(String id){
            articulos.deleteOne(eq("_id",id));
    }
        
        public void actualizarArticulo(String id, String denominacion, String precio, String stock){
         
         articulos.updateOne(eq("_id", id), set("denominacion",denominacion));
         articulos.updateOne(eq("_id", id), set("pvp",Double.parseDouble(precio)));
         articulos.updateOne(eq("_id", id), set("stock",Integer.parseInt(stock)));
         
    }
        public DefaultTableModel listarArticulos(){
         DefaultTableModel dtm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
         };
         String columnas[]={"id", "denominacion", "pvp", "stock", "cantidad_ganada", "total_ventas"};
         dtm.setColumnIdentifiers(columnas);
         List<Document> list = articulos.find().into(new ArrayList<Document>());
         for(int i=0; i<list.size(); i++){
             Document a = list.get(i);
             int total;
            List<Document> sum = ventas.aggregate(Arrays.asList(match(eq("codarticulo",a.getString("_id"))), group("$sum", sum("uni", "$unidades")))).into(new ArrayList<Document>());
           try{
             total=sum.get(0).getInteger("uni");
           }catch(Exception e){
               total=0;
           }
             Object data[]={a.getString("_id"), a.getString("denominacion"), a.get("pvp"), a.get("stock"), total*a.getDouble("pvp"), total};
             dtm.addRow(data);
         }
         return dtm;
     }
    
    
    
    //---------Ventas-----------//
         public void insertarVenta(String id, String cli, String art, String uds){
            Document venta = new Document();
            venta.put("_id", id);
            venta.put("codcliente", cli);
            venta.put("codarticulo", art);
            venta.put("fecha", new SimpleDateFormat("DD-mm-YYYY").format(new Date()));
            venta.put("unidades", Integer.parseInt(uds));
            ventas.insertOne(venta);
    }
     
        public void borrarVenta(String id){
            ventas.deleteOne(eq("_id",id));
    }
     
        public DefaultTableModel getVenta(){
            DefaultTableModel tab=new DefaultTableModel(){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
            String column[]={"codigo", "cliente", "articulo", "precio", "unidades", "fecha"};
            tab.setColumnIdentifiers(column);
            List<Document> list=ventas.find().into(new ArrayList<Document>());
            for(int i=0; i<list.size(); i++){
            Document a=list.get(i);
            Document cli=clientes.find(eq("_id",a.get("codcliente"))).first();
            Document art=articulos.find(eq("_id",a.get("codart"))).first();
            Object data[]={a.getInteger("_id"), cli.getString("nombre"), art.getString("denominacion"), art.getDouble("pvp")*a.getInteger("unidades"),
            a.get("unidades"), a.get("fecha")};
            tab.addRow(data);
        }
            return tab;
    }
    
}
