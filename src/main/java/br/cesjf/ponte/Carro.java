package br.cesjf.ponte;

public class Carro extends Thread {
    
    // Declaração de variáveis e objetos
    private final int id;
    private Estado estado;
    private final String lado;

    // Construtor
    public Carro(int id, String lado) {
        this.id = id;
        this.lado = lado;
        this.estado = Estado.CHEGOU;
    }
    
    @Override
    public void run() {
        try {
            // Variável de controle de quando deve ser finalizada a Thread
            boolean ativo = true;
            // Mantém em execução enquanto a Thread não finaliza
            while(ativo){
                // Verifica o estado
                switch(estado){
                    case CHEGOU:
                        // Exclusão mútua chegando - Início
                        Ponte.chegando.acquire();
                        // Impressão
                        System.out.println("O carro "+this.id+" chegou ao lado "+this.lado+" da ponte");
                        // Verificação do lado da ponte
                        if(this.lado.equals("A")) {
                            // Caso não exista carro atravessando nem aguardando libera a travessia, caso contrário deve aguardar
                            if(Ponte.aguardandoA.getQueueLength() == 0 && Ponte.aguardandoB.getQueueLength() == 0 && Ponte.atravessando.getQueueLength() == 0) {
                                // Altera o estado
                                this.estado = Estado.AGUARDANDO;
                                // Altera o lado
                                Ponte.lado = "A";
                                // Reseta o contador
                                Ponte.contador = Ponte.quantidade;
                                // Libera a passagem do carro
                                Ponte.aguardandoA.release();
                            } else {
                                // Altera o estado
                                this.estado = Estado.AGUARDANDO;
                                // Impressão
                                System.out.println("O carro "+this.id+" está aguardando sua vez no lado "+this.lado+" da ponte");
                            }
                        } else {
                            // Caso não exista carro atravessando nem aguardando libera a travessia, caso contrário deve aguardar
                            if(Ponte.aguardandoA.getQueueLength() == 0 && Ponte.aguardandoB.getQueueLength() == 0 && Ponte.atravessando.getQueueLength() == 0) {
                                // Altera o estado
                                this.estado = Estado.AGUARDANDO;
                                // Altera o lado
                                Ponte.lado = "B";
                                // Reseta o contador
                                Ponte.contador = Ponte.quantidade;
                                // Libera a passagem do carro
                                Ponte.aguardandoB.release();
                            } else {
                                // Altera o estado
                                this.estado = Estado.AGUARDANDO;
                                // Impressão
                                System.out.println("O carro "+this.id+" está aguardando sua vez no lado "+this.lado+" da ponte");
                            }
                        }
                        // Exclusão mútua chegando - Fim
                        Ponte.chegando.release();
                        break;
                        
                    case AGUARDANDO:
                        // Verificação do lado da ponte
                        if(this.lado.equals("A")){
                            // Acessa o semáforo dos carros aguardando do lado A
                            Ponte.aguardandoA.acquire();
                            // Exclusão mútua atravessando - Início
                            Ponte.atravessando.acquire();
                            // Altera o estado
                            this.estado = Estado.ATRAVESSANDO;
                        } else {
                            // Acessa o semáforo dos carros aguardando do lado B
                            Ponte.aguardandoB.acquire();
                            // Exclusão mútua atravessando - Início
                            Ponte.atravessando.acquire();
                            // Altera o estado
                            this.estado = Estado.ATRAVESSANDO;
                        }
                        break;
                    
                    case ATRAVESSANDO:
                        // Impressão
                        System.out.println("O carro "+this.id+" está atravessando do lado "+this.lado+" da ponte para o outro lado");
                        // Subtrai do contador
                        Ponte.contador--;
                        // Executa o método que faz uma pausa para atravessar
                        atravessando();
                        // Altera o estado
                        this.estado = Estado.TERMINOU;
                        break;
                        
                    case TERMINOU:
                        // Impressão
                        System.out.println("O carro "+this.id+" atravessou do lado "+this.lado+" da ponte para o outro lado");
                        // Exucuta o método que verifica se o lado deve ser alternado
                        Ponte.controle();
                        // Libera a passagem de mais um carro
                        if(Ponte.lado.equals("A")) {
                            Ponte.aguardandoA.release();
                        } else {
                            Ponte.aguardandoB.release();
                        }
                        // Exclusão mútua atravessando - Fim
                        Ponte.atravessando.release();
                        // Finaliza a Thread
                        ativo = false;
                        break;
                        
                }

            }
        
        } catch (InterruptedException ex) {}
        
    }
    
    // Definição do tempo que o carro demora para atravessar
    private static void atravessando() {
        try {
            Thread.sleep((long) Math.round(Math.random() * 5000));
        } catch (InterruptedException e) {}
    }
    
}
