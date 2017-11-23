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
public class Jugador {

    private Client client;
    private int posX;
    private int posY;
    private byte nro;
    private String nickname;
    private Long idDate;
    
    public Jugador(Client client){
        this.client=client;
        posX=posY=nro-1;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setNro(byte nro) {
        this.nro = nro;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setIdDate(Long idDate) {
        this.idDate = idDate;
    }

    public Client getClient() {
        return client;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public byte getNro() {
        return nro;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getIdDate() {
        return idDate;
    }
    
}
