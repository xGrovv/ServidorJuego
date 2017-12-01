/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class MapaControl {
    
    private MapaModelo mapaModelo;
    private String mapaModeloString;
    private int nroColumnas;
    private int nroFilas;
    private int posX_Objetivo;
    private int posY_Objetivo;
    private static final byte ESPACIO_LIBRE = 0;
    
    
    public MapaControl(int nroColumnas, int nroFilas, String mapaModeloString){
        this.nroColumnas=nroColumnas;
        this.nroFilas= nroFilas;
        this.mapaModeloString = mapaModeloString;
        mapaModelo=new MapaModelo(nroColumnas, nroFilas);
        cargarMapaModelo();
        //SituarObjetivo();
    }
    
    public String getMapaModeloString(){
        return mapaModeloString;
    }
    
    public int getNroColumnas(){
        return nroColumnas;
    }
    
    public int getNroFilas(){
        return nroFilas;
    }
    
    private void cargarMapaModelo(){
        byte[] Arreglo = mapaModeloString.getBytes();
        int fil, col;
        fil = col =0;
        for (byte letra : Arreglo){
            mapaModelo.setValue(letra, col, fil);
            col++;
            if (col>=100){
                col=0;
                fil++;
            }
        }
    }
    
    private void SituarObjetivo(){
        Point posLibre = getPosicionLibreRandom();
        posX_Objetivo=posLibre.x;
        posY_Objetivo=posLibre.y;
    }
    
    private Point getPosicionLibreRandom(){
        Random rn = new Random();
        while (true){
            int fil=rn.nextInt(100);
            int col=rn.nextInt(100);
            if (mapaModelo.getValue(col, fil)==0)
                return new Point(col,fil);
        }
    }
    
    private void situarJugador(Jugador jugador, int distancia){
        Point posLibre = getPosicionLibreRandom();
        // if posLibre esta a tiene una lejania minima de "distancia" con la pos del objetivo
        // asignar los valores a jugador
        // de otro modo volver a pedir otra posLibre
        //tips:: no situar al jugador en la imaginario area de vision del objetivo
        
        
    }
    
    /**
     *
     * @param jugador
     * @param distancia es la lejania minima entre el jugador y el objetivo
     */
    public void addJugador(Jugador jugador, int distancia){
        situarJugador(jugador, distancia);
        mapaModelo.setValue(jugador.getNro(), jugador.getPosX(), jugador.getPosY());
    }
    
    public boolean posicionLibre (int posX, int posY){
        return mapaModelo.getValue(posX, posY)==ESPACIO_LIBRE;
    }
    
}
