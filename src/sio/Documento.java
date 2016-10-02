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

import java.util.Arrays;
import java.util.Scanner;

public class Documento {
    
    String frases[] = new String[10];   //todas os documentos na forma de String
    int numeroTermos[] = new int[10];   //conta quantos termos tem em cada documento
    String palavras[][] = new String[10][10];   //matriz em que cada posiçao é uma palavra e cada linha é uma frase
    double tf_idf[][] = new double [10][10];   // = tf * idt
    double idt;   //inverse document frequency de um termo t
    double tf = 0;   //numero de vezes que um termo t aparece em um documento d
    double n = frases.length;   //numero total de documentos em uma colecão
    double dt = 0;   //numero de documentos onde o termo t aparece
    double sim_dq[] = new double[frases.length];   //calculo das opções mais relevantes na busca
    double ranking[] = new double[sim_dq.length];   //opções ordenadas.
    
    public Documento() {
        frases[0] = "O jogador marcou um gol legal";
        frases[1] = "O goleiro foi bem na bola";
        frases[2] = "O levantador bateu na rede";
        frases[3] = "Que baita gol";
        frases[4] = "O saque foi pessimo e a bola ficou na rede";
        frases[5] = "O levantador nao esta bem";
        frases[6] = "Quase foi gol, bateu pelo lado de fora da rede";
        frases[7] = "Bernard Vai para o saque";
        frases[8] = "Grande saque do Moreno";
        frases[9] = "Gol ilegal, o atacante estava impedido";
    }
    
    public void termos(){
        //conta quantos termos tem em cada frase.
        Arrays.fill(numeroTermos, 1);
        for(int i=0; i < frases.length; i++){
            char ch[] = frases[i].toCharArray();
            for(int j=0; j < ch.length; j++){
                if(ch[j] == ' ')
                    numeroTermos[i]++;
            }
        }
        //cria uma matriz e separa as palavras das frases.
        for(int i=0; i < frases.length; i++){
           Arrays.fill(palavras[i], "*");   //preenche toda a matriz com *, onde nao tem palavras, tem *
           String preencher[] = frases[i].split(", | ");   //limpa a frase, retirando os espaços e virgulas
           for(int j=0; j < preencher.length; j++){
               palavras[i][j] = preencher[j];
           }
        }
    }
    
    public void calculosVetoriais(){
        //inicializa a matriz tf-idf em zero
        for(int i=0; i < tf_idf.length; i++)
           Arrays.fill(tf_idf[i], 0);
        //verifica em quantos documentos cada termo aparece
        int aux = 0;
        for(int i=0; i < frases.length; i++){
            for(int j=0; j < numeroTermos[i]; j++){
                for(int w=0; w < frases.length; w++){
                    for(int k=0; k < numeroTermos[w] && aux == 0; k++){
                        if(palavras[i][j].equalsIgnoreCase(palavras[w][k])){
                            dt++;
                            aux++;
                        }
                    }
                    aux = 0 ;
                }
                //slide 21
                idt = Math.log10(n/dt);  //inverse document frequency de um termo t
                tf_idf[i][j] = idt;  
                dt = 0;
            }            
        }
        //encontra o valor do tf
        for(int i=0; i < frases.length; i++){   
            for(int j=0; j < numeroTermos[i]; j++){
                for(int k=0; k < numeroTermos[i]; k++){
                    if(palavras[i][j].equalsIgnoreCase(palavras[i][k]))
                        tf++;
                }
                //slide 22, 23
                tf_idf[i][j] = tf * tf_idf[i][j];  //tf-idf = tf * idf
                tf = 0;
             }
        }
    }
    
    public void consulta(){
        //metodo pra gerar a consulta
        String b;
        Scanner ler = new Scanner(System.in);
        System.out.println("Pesquisa: ");
        //lê a string a pesquisar
        b = ler.nextLine();
        //separa a string, cada palavra em uma posição do vetor
        String busca[] = b.split(" ");
        //calcula o tf_idf da busca digitada
        double tf_idfBusca[] = new double[busca.length];
        Arrays.fill(tf_idfBusca, 0);   //inicializa o vetor em zero.
        //slide 24
        for(int i=0; i < frases.length; i++){
            for(int j=0; j < numeroTermos[i]; j++){
                for(int k=0; k < busca.length; k++){
                    if(palavras[i][j].equalsIgnoreCase(busca[k]))
                        tf_idfBusca[k] = tf_idf[i][j];
                }
            } 
        }
        //slide 25
        double dividendo = 0, divisor = 0, soma_tf = 0, soma_busca = 0;
        int aux = 0, aux2 = 0;
        Arrays.fill(sim_dq, 0);   //inicializa o vetor em zero.
        for(int i=0; i < frases.length; i++){
            for(int j=0; j < numeroTermos[i]; j++){
                //somatorio do tf-idt ao quadrado dos termos.
                soma_tf = (tf_idf[i][j] * tf_idf[i][j]) + soma_tf;
                for(int k=0; k < busca.length; k++){
                    if(aux2 == 0)
                        //somatorio do tf-idf ao quadrado dos termos da pesquisa
                        soma_busca = (tf_idfBusca[k] * tf_idfBusca[k]) + soma_busca;
                    //se a palavra buscada estiver em algum documento
                    if(palavras[i][j].equalsIgnoreCase(busca[k])){
                        //multiplica o tf-idf do documento com o tf-idf da pesquisa
                        dividendo = (tf_idf[i][j] * tf_idfBusca[k]) + dividendo;
                        aux++;   //auxiliar pra verificar se foi encontrado alguma palavra da busca no documento.
                    }
                }
                aux2++;   //auxiliar pra verificar se já somou todos os tf-idt da busca. Nao deixando soma varias vezes
            } 
            if(aux > 0){
                //raiz quadrada da multiplicação do tf-idf do documento com o tf-idf da busca
                divisor = Math.sqrt(soma_tf * soma_busca);
                sim_dq[i] = dividendo/divisor;
            }
            dividendo = 0;
            divisor = 0;
            soma_busca = 0;
            soma_tf = 0;
            aux = 0;
            aux2 = 0;
        }
        
    }
    
    public void ranking(){
        //ranking dos calculos vetoriais
        //cria uma novo vetor, copia o resultado dos calculos e ordena crescente.
        System.arraycopy(sim_dq, 0, ranking, 0, sim_dq.length);
        Arrays.sort(ranking);
        //varre o vetor copia crescente de tras pra frente
        for(int i = ranking.length - 1; i >= 0; i--){
            for(int j=0; j < sim_dq.length; j++){
                //posições que tiver o valor -1, não aparecem no resultado.
                if(ranking[i] == 0)
                    ranking[i] = -1;
                //procurando a posiçao das posições mais relevantes e salvando na ordem correta.
                else if(sim_dq[j] != 0 && ranking[i] == sim_dq[j]){
                    System.out.println(+j +"   " +frases[j] +"   " +sim_dq[j]);
                    ranking[i] = j;
                    sim_dq[j] = 0;
                    j = sim_dq.length;
                }
            }
        }
        System.out.println("\n");
    }

 }

