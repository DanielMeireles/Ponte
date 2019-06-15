package br.cesjf.ponte;

import java.util.concurrent.Semaphore;

public class Ponte {
    
    // Declação de variáveis e objetos
    // Quantidade de carros, que devem atravessar se estiverem aguardando
    public static final int quantidade = 5;
    // Semáforo para uso de exclusão mútua dos carros que estão atravessando
    public static final Semaphore atravessando = new Semaphore(1);
    // Semáforo para uso de exclusão mútua dos carros que estão chegando
    public static final Semaphore chegando = new Semaphore(1);
    // Semáforo para controle dos carros aguardando do lado A
    public static final Semaphore aguardandoA = new Semaphore(0);
    // Semáforo para controle dos carros aguardando do lado B
    public static final Semaphore aguardandoB = new Semaphore(0);
    // Variáveis para controle de qual lado está sendo permitida a passagem
    public static String lado = "A";
    public static int contador = quantidade;
    
    public static void main(String[] args) {
        
        // Contador utilizado como id do carro
        int i = 1;
        
         // While para a criação dos carros
        while(true) {
            
            // Gera número aleatório para decidir se o carro chegará no lado A ou B
            long aux = Math.round(Math.random() * 100);
            
            // Se o número for par o carro será gerado no lado A se for ímpar será gerado no lado B
            if(aux % 2 == 0){
                Carro carro = new Carro(i, "A");
                carro.start();
            } else {
                Carro carro = new Carro(i, "B");
                carro.start();
            }
            
            // Adiciona no contador
            i++;
            
            // Executa o método que dá uma pausa entre as criações de carros
            pausa();
        }

    }
    
    // Definição do tempo entre a geração dos carros
    private static void pausa() {
        try {
            Thread.sleep((long) Math.round(Math.random() * 2500));
        } catch (InterruptedException e) {}
    }
    
    // Método que executa o controle de qual lado pode atravessar
    public static void controle() {
        // Caso o contador chegue ao valor 0, ou seja, a quantidade foi atingida, alterna o lado que tem permissão de atravessar
        if(Ponte.contador <= 0) {
            if(Ponte.lado.equals("A")) {
                Ponte.lado = "B";
                Ponte.contador = Ponte.quantidade;
            } else {
                Ponte.lado = "A";
                Ponte.contador = Ponte.quantidade;
            }
        }
        // Caso o lado permitido não tenha carros a atravessar, mas do outro lado tenha, alterna o lado
        if(Ponte.lado.equals("A") && Ponte.aguardandoA.getQueueLength() == 0 && Ponte.aguardandoB.getQueueLength() > 0) {
            Ponte.lado = "B";
            Ponte.contador = Ponte.quantidade;
        } else if(Ponte.lado.equals("B") && Ponte.aguardandoB.getQueueLength() == 0 && Ponte.aguardandoA.getQueueLength() > 0) {
            Ponte.lado = "A";
            Ponte.contador = Ponte.quantidade;
        }
    }
    
}
