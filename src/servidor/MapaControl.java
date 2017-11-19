/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

/**
 *
 * @author Grover
 */
public class MapaControl {
    
    private MapaModelo mapaModelo;
    private int nroColumnas;
    private int nroFilas;
    private static final byte LIBRE = 0;
    
    public MapaControl(String mapaTexto){
        mapaModelo=new MapaModelo(nroColumnas, nroFilas);
    }
    
    public int getNroColumnas(){
        return nroColumnas;
    }
    
    public int getNroFilas(){
        return nroFilas;
    }
    
    public void setMapaModelo(String mapaTexto){
        
        
        
    }
    
    public void addJugador(byte idJugador, int posX, int posY){
        mapaModelo.setValue(idJugador, posX, posY);
    }
    
    public boolean posicionLibre (int posX, int posY){
        return mapaModelo.getValue(posX, posY)==LIBRE;
    }
    
}
