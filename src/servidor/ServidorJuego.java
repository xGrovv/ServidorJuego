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
import java.util.ConcurrentModificationException;
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
    private int lider;
    private boolean enJuego;
    
    public ServidorJuego(){
        lider=0;    // sin lider
        enJuego=false;
        jugadorList = new ArrayList<>();
        mapaControl= new MapaControl(100, 100, CargarMapatxt());
        servidorSocket = new ServidorSocket(4455);
        servidorSocket.addListenerEvent(new ServidorSocketListener () {
            @Override
            public void onNewConnection(ServidorSocketEvent ev) {
                ClientManager clientManager = (ClientManager)ev.getSource();
                Client client = clientManager.getClient();
                //pedido de identificacion del cliente que talves reconecta
                servidorSocket.EnviarMenasaje(client, "[dateconnection_request]");
            }

            @Override
            public void onMessageReceive(ServidorSocketEvent ev) {
                onMessageReceive_Action(ev);
            }

            @Override
            public void onLostConnection(ServidorSocketEvent ev) {
                ClientManager clientManager = (ClientManager)ev.getSource();
                Client client = clientManager.getClient();
                for (Jugador jug : jugadorList) {
                    if (jug.getClient()==client){
                        jug.setEstado(false);
                        pausarJugador(jug);
                        break;
                    }
                }
            }

        });
        servidorSocket.iniciarServicio();
    }
    
    // asigna como lider al primero del lista que tenga estado true
    public void asignarLider(){
        for(Jugador jug : jugadorList){
            if (jug.getEstado()==true){
                lider=jug.getNro();
                servidorSocket.EnviarMenasaje(jug.getClient(), "[lider]");
                break;
            }
        }
    }
    
    public void pausarJugador(Jugador jugador){
        if (jugador.getNro()==lider){
            asignarLider();
            // ahora faltaria como se entera el exlider que ya no lo es
            //soluciones:  1. cuando reconecte enviarle mensaje que ya no es lider (pero)
            //                  cuando alguien reconecta ya no sabremos si se trata de exlider)
            //                  el mensaje iria para todos los reconectados,
            //              2.el cliente sabe que si perdio la conexion tambien perdio el liderazgo
            //              3. reasignar lider solo cuando termine la partida
        }
        String cadena="[jugador.pausado]>"+jugador.getNro()+"_"+jugador.getPosX()+"-"+jugador.getPosY();
        enviarMensajeTodosEstadoTrue(cadena);
    }
    
    private void onMessageReceive_Action(ServidorSocketEvent ev){
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
        
        // si no se encuentra en la lista el cliente
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
                            jug.setEstado(true);
                            String texto = "[reconectado]>"+jug.getNro()+"_"+jug.getPosX()+"-"+jug.getPosY();
                            enviarMensajeTodosEstadoTrue(texto);
                            
                            break;
                        }
                    }
                    //si tiene un data connection y aun asi no esta en la lista
                    addNewJugador(cli.getClient());
                }
            }
            return;
        }
        
        if (msj.contains("[reg]")){
            if (enJuego==true){
                servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg]enjuego");
                return;
            }
            String nickname = msj.substring(5);
            if (existeNickname(nickname)){
                servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg-no]");
                return;
            }
            jugador.setNickname(nickname);
            jugador.setEstado(true);
            
            servidorSocket.EnviarMenasaje(jugador.getClient(), "[reg]done>"+jugador.getNro());
            if(lider==0){
                lider= jugador.getNro();
                servidorSocket.EnviarMenasaje(jugador.getClient(), "[lider]");
            }
            return;
        }
        
        if (msj.contains("[map]")){
            servidorSocket.EnviarMenasaje(jugador.getClient(), "[map]"+mapaControl.getMapaModeloString());
            return;
        }
        
        if (msj.contains("[move]>")){ // supone pos1 bicho y pos2 lugar
            String posX1=msj.substring(msj.indexOf('_')+1, msj.indexOf("-"));
            String posY1=msj.substring(msj.indexOf('-')+1, msj.indexOf("*"));
            String posX2=msj.substring(msj.indexOf('*')+1, msj.indexOf("@"));
            String posY2=msj.substring(msj.indexOf('@')+1);
            if (!mapaControl.posicionLibre(Integer.parseInt(posX2), Integer.parseInt(posY2)))
                return;
            mapaControl.cambiarValores(Integer.parseInt(posX1),Integer.parseInt(posY1),Integer.parseInt(posX2),Integer.parseInt(posY2));
            jugador.setPosX(Integer.parseInt(posX2));
            jugador.setPosY(Integer.parseInt(posY2));
            enviarMensajeTodosEstadoTrue(msj);
            return;
        }
        
        if (msj.contains("[salir]")){
            if (jugador.getNro()==lider){
                lider=0;
                //falta aqui darle lider a otro jugador
            }
            String cadena="[jugador.quitado]>"+jugador.getNro()+"_"+jugador.getPosX()+"-"+jugador.getPosY();
            enviarMensajeTodosEstadoTrue(cadena);
            mapaControl.cambiarValor((byte)0, jugador.getPosX(), jugador.getPosY());
            jugadorList.remove(jugador);
            System.out.println("un jugador se retiró: "+ jugadorList.size());
            return;
        }
        
        
        
        if (msj.contains("[jugar]")){
            if (jugadorList.size()>0){ // mas de un jugador
                enviarMensajeTodosEstadoTrue("[jugar-done]");
                enJuego=true;
            }
            return;
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
    
    private void enviarMensajeTodosEstadoTrue(String msj){
        try{
            for(Jugador jug: jugadorList){
            if (jug.getEstado())
                servidorSocket.EnviarMenasaje(jug.getClient(), msj);
            }
        }catch (ConcurrentModificationException e){
            System.out.println("servidor.ServidorJuego.enviarMensajeTodosEstadoTrue()");
        }
        
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
    
    private String CargarMapatxt(){
        String linea;
        FileReader fileR;
        String mapaString="";
        try {
            fileR = new FileReader(getClass().getResource("/Recursos/mapa1.txt").getPath());
            BufferedReader br = new BufferedReader(fileR);
            while((linea = br.readLine())!=null) {
                // letra c es camino, letra p es pared. convertiremos estos a 0 caminio y 1 pared
                for (byte letra : linea.getBytes()){
                    if (letra=='c')
                        letra=0;
                    else
                        letra=1;
                    mapaString=mapaString+letra;
                }
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
