import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// Classe para representar uma cidade
class Cidade {
    public double x;
    public double y;
    public String nome;

    public Cidade(double x, double y, String nome) {
        this.x = x;
        this.y = y;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    // Método para calcular a distância entre duas cidades
    public double distanciaAte(Cidade outraCidade) {
        double deltaX = this.x - outraCidade.x;
        double deltaY = this.y - outraCidade.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }
}

// Classe para representar uma rota
class Rota {
    private ArrayList<Cidade> cidades = new ArrayList<Cidade>();
    ArrayList<Rota> melhoresSolucoes = new ArrayList<>();
    private static int gen = 0;
    private double distanciaTotalFixa = 99999999;
    double temperatura = 1000.0; // Temperatura inicial
    double taxaResfriamento = 0.995;

    // Adiciona uma cidade à rota
    public void adicionarCidade(Cidade cidade) {
        cidades.add(cidade);
    }
    public void adicionarPopulacao(ArrayList<Cidade> novaRota){
        cidades = novaRota;
    }

    public void run() {
        int maxIteracoes = 100000000;
        long startTime = System.currentTimeMillis();
    
        for (int i = 0; i < maxIteracoes; i++) {
            gen++;
            mutacao();
            temperatura *= taxaResfriamento;
        }
    
        long endTime = System.currentTimeMillis();
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
    }
    public void mutacao() {
        ArrayList<Cidade> novaRota = new ArrayList<>(cidades);
        int indice1 = (int) (Math.random() * novaRota.size());
        int indice2 = (int) (Math.random() * novaRota.size());
        while (indice1 == indice2) {
        indice2 = (int) (Math.random() * novaRota.size());
        }
        
         // Garante que indice1 < indice2
        if (indice1 > indice2) {
            int temp = indice1;
            indice1 = indice2;
            indice2 = temp;
        }

    // Cria uma nova rota invertendo o segmento entre indice1 e indice2
        while (indice1 < indice2) {
            Collections.swap(novaRota, indice1, indice2);
            indice1++;
            indice2--;
        }

        double distTotal = calcularDistanciaTotal(novaRota);
    
        // Decidir se a nova rota deve ser aceita
        if (distTotal < distanciaTotalFixa) {
            System.out.println("Nova distancia: " + distTotal +" Gen: "+ gen);
            distanciaTotalFixa = distTotal;
            cidades = novaRota;
        } else {
            double probabilidadeAceitacao = Math.exp((distanciaTotalFixa - distTotal) / temperatura);
        if (Math.random() < probabilidadeAceitacao) {
            crossover(novaRota, cidades);
        }
        }
    }
    

    public ArrayList<Cidade> crossover(ArrayList<Cidade> pai1, ArrayList<Cidade> pai2) {
        ArrayList<Cidade> filho = new ArrayList<>(Collections.nCopies(pai1.size(), null));
    
        // Determina o tamanho do segmento que será preservado (1/4 do pai1)
        int segmentoTamanho = pai1.size() / 4;
        int startPos = (int) (Math.random() * (pai1.size() - segmentoTamanho));
        int endPos = startPos + segmentoTamanho;
    
        // Copia o segmento do pai1 para o filho
        for (int i = startPos; i <= endPos; i++) {
            filho.set(i, pai1.get(i));
        }
    
        // Preenche o restante do filho com as cidades do pai2, mantendo a ordem e evitando duplicatas
        int fillPos = (endPos + 1) % pai1.size(); // Começa a preencher logo após o segmento
        for (Cidade cidade : pai2) {
            if (!filho.contains(cidade)) {
                filho.set(fillPos, cidade);
                fillPos = (fillPos + 1) % pai1.size();
            }
        }
    
        return filho;
    }
    

    // Calcula a distância total da rota
    public double calcularDistanciaTotal(ArrayList<Cidade> rota) {
        double distanciaTotal = 0;
        int tamanho = rota.size();
        for (int i = 0; i < tamanho - 1; i++) {
            Cidade atual = rota.get(i);
            Cidade proxima = rota.get(i + 1);
            distanciaTotal += atual.distanciaAte(proxima);
        }
        // Conectar a última cidade de volta à primeira
        distanciaTotal += rota.get(tamanho - 1).distanciaAte(rota.get(0));
        return distanciaTotal;
    }


    }

    


// Exemplo de uso
public class Main {
    public static void main(String[] args) {
        Rota rota = new Rota();
        try {
            // Substitua "caminho/do/arquivo.txt" pelo caminho real do seu arquivo
            Scanner scanner = new Scanner(new File("data.txt"));
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                String[] partes = linha.split(" ");
                // Ignora a primeira linha que contém o ano
                if (partes.length == 3) {
                    double x = Double.parseDouble(partes[0]);
                    double y = Double.parseDouble(partes[1]);
                    String nome = partes[2];
                    rota.adicionarCidade(new Cidade(x, y, nome));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado.");
        }
        rota.run();

        
    }
}
