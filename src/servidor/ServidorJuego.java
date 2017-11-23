/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import eventos.ServidorSocketEvent;
import eventos.ServidorSocketListener;
import java.util.ArrayList;

/**
 *
 * @author Grover
 */
public class ServidorJuego {
    
    private ServidorSocket servidorSocket=null;
    private ArrayList<Jugador> jugadorList;
    private Partida partida;
    
    public ServidorJuego(){
        servidorSocket = new ServidorSocket(6611);
        servidorSocket.addListenerEvent(new ServidorSocketListener () {
            @Override
            public void onNewConnection(ServidorSocketEvent ev) {
                ClientManager clientManager = (ClientManager)ev.getSource();
                addJugador(clientManager.getClient());
            }
        });
        servidorSocket.iniciarServicio();
        
    }
    
    public void addJugador(Client client){
        // compaarar el cliente por su idDate en la lista de jugadores
        // para reconectar a jugador
        // si no esta en la lista add como nuevo jugador
        Jugador jugador = new Jugador(client);
        jugadorList.add(jugador);
    }
    
}
