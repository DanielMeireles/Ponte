package br.cesjf.ponte;

public class Main {
    
    public static void main(String[] args) {
        
        // Instanciação da ponte
        Ponte ponte = new Ponte();
        // Inicialização da Thread da ponte
        ponte.start();
        
        // Contador utilizado como id do carro
        int i = 0;
        
         // Loop para a criação dos carros
        while(true) {
            
            // Adiciona valor ao contador
            i++;
            
            // Executa o método que dá uma pausa entre a geração dos carros
            pausa();
            
            // Geração de um número randômico e verificação se é par ou ímpar para escolher o lado
            if(Math.round(Math.random() * 100) % 2 == 0){
                // Instanciação do carro
                Carro carro = new Carro(i, "A");
                // Inicialização da Thread do carro
                carro.start();
            } else {
                // Instanciação do carro
                Carro carro = new Carro(i, "B");
                // Inicialização da Thread do carro
                carro.start();
            }
            
        }

    }
    
    // Definição do tempo entre a geração dos carros
    private static void pausa() {
        try {
            Thread.sleep((long) Math.round(Math.random() * 5000));
        } catch (InterruptedException e) {}
    }
    
}