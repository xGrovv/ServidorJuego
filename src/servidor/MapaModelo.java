/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Grover
 */
public class MapaModelo {
    
    private final int nroColumnas;
    private final int nroFilas;
    private byte[][] matriz=null;// = new byte[FILAS][COLUMNAS];

    public MapaModelo(int nroColumnas, int nroFilas) {
        this.nroColumnas = nroFilas;
        this.nroFilas = nroFilas;
        matriz = new byte[nroColumnas][nroFilas];
    }
    
    public void setValue(byte value, int posX, int PosY){
        matriz[posX][PosY]=value;
    }
    
    public byte getValue(int posX, int PosY){
        return matriz[posX][PosY];
    }
    
    public byte getElemento(Point punto){
        return matriz[punto.x][punto.y];
        
    }
    
    
    
    
}
