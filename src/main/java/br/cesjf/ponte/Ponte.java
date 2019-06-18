package br.cesjf.ponte;

import java.util.concurrent.Semaphore;

public class Ponte extends Thread {
    
    // Quantidade de carros, que devem atravessar se estiverem aguardando
    public static final int quantidade = 5;
    // Semáforo para uso de exclusão mútua dos carros que estão atravessando
    public static final Semaphore atravessando = new Semaphore(0);
    // Semáforo para controle dos carros aguardando do lado A
    public static final Semaphore aguardandoA = new Semaphore(0);
    // Semáforo para controle dos carros aguardando do lado B
    public static final Semaphore aguardandoB = new Semaphore(0);
    // Controle para manter a fila em ordem
    public static final Semaphore mutex = new Semaphore(1);
    // Variáveis para controle de qual lado está sendo permitida a passagem
    public static String lado = "A";
    public static int contador = quantidade;
    
    @Override
    public void run() {
        
        try {
            
            // Impressão
            System.out.println("Ponte iniciada");

            // Loop da ponte
            while(true) {

                // Exclusão mútua para manter a fila em ordem (Enquanto faz a verificação não ocorrem chegadas) - Início
                Ponte.mutex.acquire();
                
                // Alterna o lado e reseta a quantidade quando o contador está zerado
                if(Ponte.contador == 0) {
                    if(Ponte.lado.equals("A")) {
                        Ponte.lado = "B";
                        Ponte.contador = quantidade;
                    } else {
                        Ponte.lado = "A";
                        Ponte.contador = quantidade;
                    }
                }

                // Alterna o lado enquanto não existe fila de carros
                if(Ponte.lado.equals("A") && Ponte.aguardandoA.getQueueLength() == 0) {
                    if(Ponte.aguardandoB.getQueueLength() > 0) {
                        lado = "B";
                        contador = quantidade;
                    }
                } else if (Ponte.lado.equals("B") && Ponte.aguardandoB.getQueueLength() == 0) {
                    if(Ponte.aguardandoB.getQueueLength() > 0) {
                        lado = "A";
                        contador = quantidade;
                    }
                }
                
                // Exclusão mútua para manter a fila em ordem (Enquanto faz a verificação não ocorrem chegadas) - Fim
                Ponte.mutex.release();

                // Verifica se tem algum carro na fila e faz a liberação para que ele atravesse
                if(Ponte.lado.equals("A") && Ponte.aguardandoA.getQueueLength() > 0) {
                    // Semáforo de carros aguardando do lado A - Libera
                    Ponte.aguardandoA.release();
                    // Excusão mútua de atravessando (Ficará parado aqui até que o carro termine de atravessar) - Início
                    Ponte.atravessando.acquire();
                } else if(Ponte.lado.equals("B") && Ponte.aguardandoB.getQueueLength() > 0) {
                    // Semáforo de carros aguardando do lado B - Libera
                    Ponte.aguardandoB.release();
                    // Excusão mútua de atravessando (Ficará parado aqui até que o carro termine de atravessar) - Início
                    Ponte.atravessando.acquire();
                }
                
            }
            
        } catch (InterruptedException ex) {}
        
    }
    
}