/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.awt.Point;
import java.util.Random;

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
            if (letra==48) // 48 valor assci de cero
                letra=0;
            if (letra==49) // 49 valor assci de uno
                letra=1;
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
    
    public Point getPosicionLibreRandom(){
        Random rn = new Random();
        while (true){
            int fil=rn.nextInt(20);
            int col=rn.nextInt(20);
            int c1 = mapaModelo.getValue(col, fil);
            if (c1==0)
                return new Point(col,fil);
        }
    }
    
    private void situarJugador(Jugador jugador){
        Point punto = getPosicionLibreRandom();
        jugador.setPosX(punto.x);
        jugador.setPosY(punto.y);
        
        
        // if posLibre esta a tiene una lejania minima de "distancia" con la pos del objetivo
        // asignar los valores a jugador
        // de otro modo volver a pedir otra posLibre
        //tips:: no situar al jugador en la imaginario area de vision del objetivo
     
    }
    
    public void addJugador(Jugador jugador){
        situarJugador(jugador);
        mapaModelo.setValue(jugador.getNro(), jugador.getPosX(), jugador.getPosY());
    }
    
    public boolean posicionLibre (int posX, int posY){
        return mapaModelo.getValue(posX, posY)==ESPACIO_LIBRE;
    }
    
    public void cambiarValores(int posX1, int posY1, int posX2, int posY2){
        byte val1=mapaModelo.getValue(posX1, posY1);
        byte val2=mapaModelo.getValue(posX2, posY2);
        mapaModelo.setValue(val2, posX1, posY1);
        mapaModelo.setValue(val1, posX2, posY2);
    }
    
    public void cambiarValor(byte val, int posX1, int posY1){
        mapaModelo.setValue(val, posX1, posY1);
    }
    
}
