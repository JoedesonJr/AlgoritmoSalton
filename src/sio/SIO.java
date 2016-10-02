/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sio;

/**
 *
 * @author JUNIOR
 */



public class SIO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Documento doc = new Documento();
        
        doc.termos();
        doc.calculosVetoriais();
        doc.consulta();
        System.out.println("\n");
        doc.ranking();
        
    }
}
