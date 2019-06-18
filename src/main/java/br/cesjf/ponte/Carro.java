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
            
            // Mantém a Thread em execução enquanto a variável ativo for verdadeira
            while(ativo){
                
                // Verificação do estado
                switch(estado){
                    
                    // OK
                    case CHEGOU:
                        // Exclusão mútua para manter a fila em ordem (Enquanto está chegando não ocorrem verificação na ponte) - Início
                        Ponte.mutex.acquire();
                        // Impressão
                        System.out.println("O carro "+this.id+" chegou ao lado "+this.lado+" da ponte");
                        // Altera o estado para aguardando
                        this.estado = Estado.AGUARDANDO;
                        break;
                        
                    case AGUARDANDO:
                        // Impressão
                        System.out.println("O carro "+this.id+" está aguardando sua vez no lado "+this.lado+" da ponte");
                        // Exclusão mútua para manter a fila em ordem (Enquanto está chegando não ocorre verificação na ponte) - Fim
                        Ponte.mutex.release();
                        // Verificação do lado da ponte e acessa o semáforo
                        if(this.lado.equals("A")){
                            // Acessa o semáforo dos carros aguardando do lado A
                            Ponte.aguardandoA.acquire();
                        } else {
                            // Acessa o semáforo dos carros aguardando do lado B
                            Ponte.aguardandoB.acquire();
                        }
                        // Exclusão mútua para manter a fila em ordem (Enquanto está mudando de estado para atravessando não ocorrem verificação na ponte) - Início
                        Ponte.mutex.acquire();
                        // Altera o estado para atravessando
                        this.estado = Estado.ATRAVESSANDO;
                        break;
                    
                    case ATRAVESSANDO:
                        // Impressão
                        System.out.println("O carro "+this.id+" está atravessando do lado "+this.lado+" da ponte para o outro lado");
                        // Subtrai do contador
                        Ponte.contador--;
                        // Exclusão mútua para manter a fila em ordem (Enquanto está mudando de estado para atravessando não ocorrem verificação na ponte) - Fim
                        Ponte.mutex.release();
                        // Executa o método que faz uma pausa para atravessar
                        atravessando();
                        // Altera o estado para terminou
                        this.estado = Estado.TERMINOU;
                        break;
                        
                    case TERMINOU:
                        // Impressão
                        System.out.println("O carro "+this.id+" atravessou do lado "+this.lado+" da ponte para o outro lado");
                        // Excusão mútua de atravessando (Libera a ponte para continuar a execução) - Fim
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
