/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import eventos.ClientManagerEvent;
import eventos.ClientManagerListener;
import eventos.ServidorSocketEvent;
import eventos.ServidorSocketListener;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
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
                Client client = clientManager.getClient();
                //pedido de identificacion del cliente quet alves reconecta
                servidorSocket.EnviarMenasaje(client, "[dateconnection_request]");
            }

            @Override
            public void onMessageReceive(ServidorSocketEvent ev) {
                ClientManager cli = (ClientManager)ev.getSource();
                String msj=cli.getMessage();
                Jugador jugador=null;
                System.out.println("__"+msj);
                
                ListIterator li = jugadorList.listIterator();
                while (li.hasNext()) {
                    Jugador jug = (Jugador) li.next();
                    if (jug.getClient().equals(cli.getClient())){
                        jugador=jug;
                        break;
                    }
                }
                if (jugador==null){
                    if (msj.contains("[dateconnection]>")){
                        String cadena = msj.substring(msj.indexOf('>')+1);
                        Long dateCon = Long.parseLong(cadena);
                        if (dateCon==0){// nuevo jugador
                            addNewJugador(cli.getClient());
                        }else{
                            for(Jugador jug: jugadorList){
                                if (dateCon==jug.getDateConnection()){
                                    jug.setClient(cli.getClient());
                                }
                            }
                        }
                    }
                    return;
                }
                
                /*for(Jugador jug : jugadorList){
                    if (jug.getClient().equals(cli.getClient())){
                        jugador=jug;
                        break;
                    }
                }*/
                if (msj.contains("[reg]")){
                    String nickname = msj.substring(5);
                    if (existeNickname(nickname)){
                        servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg-no]"+jugador.getNro());
                        return;
                    }
                    jugador.setNickname(nickname);
                    jugador.setEstado(true);
                    servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg]done>"+jugador.getNro());
                }
                if (msj.contains("[map]")){
                    String nickname = msj.substring(5);
                    servidorSocket.EnviarMenasaje(jugador.getClient(), "[map]"+mapaControl.getMapaModeloString());
                }
                if (msj.contains("[jugar]")){
                    System.out.println("el juego inicio");
                    
                }
                
                
                if (msj.contains("[pos]")){
                  
                  mapaControl.addJugador(jugador);
                  for(Jugador jug2 : jugadorList){
                      if (jug2.getEstado()==true){
                        servidorSocket.EnviarMenasaje(jugador.getClient(), "[pos]"+FormatoStringJugadorPos(jug2));     
                        if (!jug2.equals(jugador))
                            servidorSocket.EnviarMenasaje(jug2.getClient(), "[pos]"+FormatoStringJugadorPos(jugador));     
                      }
                  }
                }
            }

            
        });
        servidorSocket.iniciarServicio();
        
    }
    
    private boolean existeNickname(String nickname) {
        for(Jugador jug: jugadorList){
            if (jug.getNickname().equals(nickname))
                return true;
        }
        return false;
    }
    
    private String FormatoStringJugadorPos(Jugador jug){
        String numero, fil, col, posjug;
        numero=String.valueOf(jug.getNro());
        fil=String.valueOf(jug.getPosX());
        col=String.valueOf(jug.getPosY());   
        posjug="<"+numero+"_"+fil+"-"+col+">";
        return posjug;
        
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
    
    
    
    public void addNewJugador(Client client){    
        Long dateCon= System.currentTimeMillis();
        String numeroString =  String.valueOf(jugadorList.size()+11) ;
        Jugador jugador = new Jugador(client);
        jugador.setNro(Byte.parseByte(numeroString));
        jugador.setDateConnection(dateCon);
        jugadorList.add(jugador);
        
        // entregamos su date connection al nuevo jugador
        servidorSocket.EnviarMenasaje(jugador.getClient(), "[dateconnection]>"+dateCon.toString());
    }
    
    
}
