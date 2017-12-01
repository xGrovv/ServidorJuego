/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import eventos.ServidorSocketEvent;
import eventos.ServidorSocketListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Grover
 */
public class ServidorJuego {
    
    private ServidorSocket servidorSocket=null;
    private ArrayList<Jugador> jugadorList;
    private Partida partida;
    private MapaControl mapaControl;
    
    
    public ServidorJuego(){
        jugadorList = new ArrayList<Jugador>();
        mapaControl= new MapaControl(100, 100, CargarMapatxt());
        servidorSocket = new ServidorSocket(4455);
        servidorSocket.addListenerEvent(new ServidorSocketListener () {
            @Override
            public void onNewConnection(ServidorSocketEvent ev) {
                ClientManager clientManager = (ClientManager)ev.getSource();
                addNewJugador(clientManager.getClient());
            }

            @Override
            public void onMessageReceive(ServidorSocketEvent ev) {
                ClientManager cli = (ClientManager)ev.getSource();
                String msj=cli.getMessage();
                Jugador jugador=null;
                System.out.println("__"+msj);
                for(Jugador jug : jugadorList){
                    if (jug.getClient().equals(cli.getClient()))
                        jugador=jug;
                        break;
                }
                if (msj.contains("[reg]")){
                    String nickname = msj.substring(5);
                    //for(Jugador jugador : jugadorList){
                        //if (jugador.getClient().equals(cli.getClient())){
                            jugador.setNickname(nickname);
                            confirmarRegistro(jugador);
                        //}
                        
                    //}
                }
                if (msj.contains("[map]")){
                    String nickname = msj.substring(5);
                    servidorSocket.EnviarMenasaje(jugador.getClient(), "[map]"+mapaControl.getMapaModeloString());
                }
            }
        });
        servidorSocket.iniciarServicio();
        
    }
    
    public String CargarMapatxt(){
        //String dir=getClass().getResource("/Recursos/mapa1.txt").getPath();
        String linea;
        FileReader fileR;
        String mapaString="";
        try {
            fileR = new FileReader(getClass().getResource("/Recursos/mapa1.txt").getPath());
            BufferedReader br = new BufferedReader(fileR);
            int fila=0;
            while((linea = br.readLine())!=null) {
                int columna=0;
                // letra c es camino, letra p es pared. convertiremos estos a 0 caminio y 1 pared
                for (byte letra : linea.getBytes()){
                    if (letra=='c')
                        letra=1;
                    else
                        letra=0;
                    
                    mapaString=mapaString+letra;
                    columna++;
                }
                for (int i=0; i<=linea.length();i++)
                fila++;
            }   
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MapaControl.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
                Logger.getLogger(MapaControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapaString;
    }
    
    public void confirmarRegistro(Jugador jugador){
        if (jugadorList.get(0)==jugador)
            servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg]done1");
        else
            servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg]done");
    }
    
    public void addNewJugador(Client client){
        // compaarar el cliente por su idDate en la lista de jugadores
        // para reconectar a jugador
        // si no esta en la lista add como nuevo jugador
        Jugador jugador = new Jugador(client);
        jugadorList.add(jugador);
    }
    
    
}
