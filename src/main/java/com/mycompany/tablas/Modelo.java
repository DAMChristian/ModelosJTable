/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.tablas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Alumno Tarde
 */
public class Modelo extends AbstractTableModel { 
    
    private int fila;
    private String columna[][];
    private String nombreColumna[];
    
    public Modelo(String ruta) {
        int lines = 0;
            try {
            File file = new File(ruta);
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
            lineNumberReader.skip(Long.MAX_VALUE);
            lines = lineNumberReader.getLineNumber();
            lineNumberReader.close();
        } catch (IOException iOException) {
        }
        
        int columnas = 0;
        try {
            BufferedReader bufLector = new BufferedReader(new FileReader(new File(ruta)));
            String linea = bufLector.readLine();
            if (linea != null) {
                String arrayColumnas[] = linea.split(",");
                columnas = arrayColumnas.length;
            }
            bufLector.close();
        } catch(FileNotFoundException e) {
            System.out.println("Fichero no encontrado");
        } catch(IOException e) {
            System.out.println("Error de I/O");
        }

        columna = new String[lines+1][columnas];
        nombreColumna = new String[columnas];
        try {
            BufferedReader bufLector = new BufferedReader(new FileReader(new File(ruta)));
            fila = 0;
            String linea;
            while ((linea = bufLector.readLine()) != null) {
                columna[fila] = linea.split(",");
                fila++;
            }
            bufLector.close();
        } catch(FileNotFoundException e) {
            System.out.println("Fichero no encontrado");
        } catch(IOException e) {
            System.out.println("Error de I/O");
        }
        
        for (int i = 0; i < nombreColumna.length; i++) {
            nombreColumna[i] = null;
        }
    }
    
    public Modelo() {
        try {
            Class.forName("org.sqlite.JDBC");        
            Connection conexion = DriverManager.getConnection("jdbc:sqlite::resource:ejemplo.db");
            
            Statement sentencia = conexion.createStatement();
            String sql = "SELECT count(*) FROM departamentos";
            ResultSet resul = sentencia.executeQuery(sql);
            fila = resul.getInt(1);
            
            sql = "SELECT * FROM departamentos";
            resul = sentencia.executeQuery(sql);
            ResultSetMetaData rsmt = resul.getMetaData();
            
            columna = new String[fila][rsmt.getColumnCount()];
            nombreColumna = new String[rsmt.getColumnCount()];
            
            for (int i = 1; i < rsmt.getColumnCount() + 1; i++) {
                nombreColumna[i-1] = rsmt.getColumnName(i);
            }
            
            int cont = 0;
            while(resul.next()) {
                
                for (int i = 0; i < rsmt.getColumnCount(); i++) {
                    columna[cont][i] = resul.getString(i + 1) + "";
                }
                cont++;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TablasFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TablasFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
          
    @Override
    public int getRowCount() {
        return fila;
    }

    @Override
    public int getColumnCount() {
        return columna[0].length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columna[rowIndex][columnIndex];

    }

    @Override
    public String getColumnName(int column) {
        return nombreColumna[column];
    }
    
    
    
    
}
