/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Grover
 */
public class Partida {
    
    private final boolean iniciado;
    private ArrayList<Jugador> jugadorList;
    private Jugador jugadorCreador;
    private final String rutaArchivoConfEmpresa = new File ("").getAbsolutePath()+"/Conf_Farma/DatosEmp.xc";
    
    public Partida(Jugador jugador){
        jugadorList = new ArrayList<>();
        jugadorList.add(jugador);
        iniciado=false;
    }
    
    public void addJugador(){
        
    }
    
}
