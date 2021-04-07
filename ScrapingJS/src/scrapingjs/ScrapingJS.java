/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrapingjs;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.lang.*;

public class ScrapingJS {
    
    int[][] matriz_mapa;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";     //Métodos para obtención de colores de subrayado
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    int tam_pixel = 23;   //Tamaño de cuadro, 24 pixeles por 24 pixeles
    int width, height;
    int direccion = 1;                     //Se definen las direcciones, 0-> Abajo, 1-> Arriba, 2-> Derecha, 3-> Izquierda
    
    public static void main(String args[])throws IOException, InterruptedException{
        ScrapingJS funciones = new ScrapingJS();
        for(int i=1; i<=3; i++){
            System.out.print(ANSI_PURPLE_BACKGROUND + ANSI_PURPLE + "                       NIVEL    " + i + "                        "+ ANSI_RESET);
            Thread.sleep(5000);
            funciones.niveles(i);
            funciones.direccion = 1;
        }
    }
    
    public void niveles(int num_level) throws InterruptedException{
        BufferedImage img = null;
        File f = null;
        switch(num_level){
            case 1:
                try{
                    f = new File("images\\Level1.jpg");    //Lectura de imagen, se debe poner la ruta de la imagen previamente abstraida mediante el programa realizado en python adjunto
                    img = ImageIO.read(f);
                }catch(IOException e){      //Lectura de imagen
                    System.out.println(e);
                }
                break;
            case 2:
                try{
                    f = new File("images\\Level2.jpg");    //Lectura de imagen, se debe poner la ruta de la imagen previamente abstraida mediante el programa realizado en python adjunto
                    img = ImageIO.read(f);
                }catch(IOException e){      //Lectura de imagen
                    System.out.println(e);
                }
                break;
            case 3:
                try{
                    f = new File("images\\Level3.jpg");    //Lectura de imagen, se debe poner la ruta de la imagen previamente abstraida mediante el programa realizado en python adjunto
                    img = ImageIO.read(f);
                }catch(IOException e){      //Lectura de imagen
                    System.out.println(e);
                }
                break;
            default:
                break;
        }
        int width_img = img.getWidth();
        width = width_img - (width_img%tam_pixel);    //Se recorta el tamaño de la imagen a un tamaño multiplo del tamaño de cuadro, para que salgan cuadros exactos y cuadrados
        int height_img = img.getHeight();
        height = height_img - (height_img%tam_pixel);
        
        this.mapear(img);     //Esta funcion se encarga de "eliminar" los pixeles que no aportan información
        this.ampliar_cuadricula(img);    //Esta función amplia el mapa de pixeles y lo transforma en una matriz de números dependiendo el objeto
        this.imprimir_matriz();
        this.explorar();
    }
    
    public BufferedImage mapear(BufferedImage img){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x, y);
                Color c = new Color(p);
                int R = c.getRed();       //Función que recorre todo el mapa de pixeles y "elimina" los pixeles que no aportan información
                int G = c.getGreen();
                int B = c.getBlue();
                int col = this.color(R,G,B);
                switch(col){
                    case 0:
                        img.setRGB(x, y, 00000000);    //LOS PIXELES NO UTILES SE PONEN EN NEGRO
                        break;
                    case 1:
                        img.setRGB(x, y, p);
                        break;
                }
            }
        }
        return img;
    }
    
    public int color(int R, int G, int B){
        int col = 0;
        if(R==34 && G==43 && B==14){              //Con estos if se rescatan los objetos de valor, tales como buhos, el carro o la casa
            col = 1; //MUROS
        }
        if(R==102 && G==102 && B==102){         //Se salvan los objetos por medio de su codigo RGB, representativo de cada objeto
            col = 1; //CAMINO
        }
        if((R>136 && R<141)||(G>0 && G<5)||(B>41 && B<53)){
            col = 1; //CARRO
        }
        if((R>51 && R<58)&&(G>61 && G<78)&&(B>59 && B<79)){  //BUHOS
            col = 1;
        }
        if((R>95 && R<108)||(G>0 && G<3)||(B>0 && B<5)){
            col = 1; //CASA
        }
        return col;
    }
    
    public BufferedImage ampliar_cuadricula(BufferedImage img){
        boolean objeto = false;
        int p, R, G, B, contador_px_muros = 0, contador_px_camino = 0;         //Inicialización de variables
        matriz_mapa = new int[width/tam_pixel][height/tam_pixel];           //Se inicializa la matriz que tendrá la información final
        for(int y = 0; y < height; y+=tam_pixel){
            for(int x = 0; x < width; x+=tam_pixel){                        //Se utilizan los primedos dos for para situar un marcador en el siguiente bloque de pixeles a analizar
                for(int ypos = y; ypos < y+tam_pixel; ypos++){
                    for(int xpos = x; xpos < x+tam_pixel; xpos++){             //Cada bloque de pixeles se recorre para hacer conteo de pixeles de colores y determinar el tamaño final de ese cuadro
                        p = img.getRGB(xpos, ypos);
                        Color c = new Color(p);
                        R = c.getRed();
                        G = c.getGreen();          //Se obtienen los valors RGB
                        B = c.getBlue();
                        if(R==34 && G==43 && B==14){
                            contador_px_muros++;            //Si el color del muro corresponde entonces se suma un pixel al total
                        }
                        if(R==102 && G==102 && B==102){
                            contador_px_camino++;           //Si el color del camino corresponde entonces se suma un pixel al total 
                        }
                        if((R>136 && R<141)&&(G>0 && G<5)&&(B>41 && B<53)){   //CARRO
                            objeto = true;                                    //La variable objeto avisa si el pixel encontrado es un objeto importante
                            matriz_mapa[x/tam_pixel][y/tam_pixel] = 2;          //Se mapea con el codigo 2, representa carro
                        }
                        if((R>51 && R<58)&&(G>61 && G<78)&&(B>59 && B<79)){   //BUHOS
                            objeto = true;
                            matriz_mapa[x/tam_pixel][y/tam_pixel] = 3;            //Se mapea con el codigo 3, representa buho
                        }
                        if((R>95 && R<108)&&(G>0 && G<3)&&(B>0 && B<5)){   //CASA
                            objeto = true;                                           //Se mapea con el codigo 4, representa casa
                            matriz_mapa[x/tam_pixel][y/tam_pixel] = 4;
                        }
                    }
                }
                if(!objeto){
                   this.color_pixel(contador_px_muros, contador_px_camino, x/tam_pixel, y/tam_pixel);   //Si se encuentra que no hay objeto encontrado entonces se llena con 0 o 1
                }
                objeto = false;
                contador_px_muros = 0;    //Se dejan las variables en cero para el proximo conteo
                contador_px_camino = 0;
            }
        }
        return img;    //Se retorna la imagen
    }
    
    public void color_pixel(int num_muros, int num_caminos, int posX, int posY){
        if(num_muros==0 && num_caminos==0){        //Si no hay pixeles ni de muro ni de color entonces se deja como muro por fácilidad
            matriz_mapa[posX][posY] = 0;
        }
        if(num_muros>=num_caminos){             //Si hay mas pixeles de muros que de camino entonces se deja como muro
            matriz_mapa[posX][posY] = 0;
        }
        if(num_muros<num_caminos){            //Si hay mas pixeles de camino que de muro entonces de deja como camino
            matriz_mapa[posX][posY] = 1;
        }
    }
    
    public void explorar() throws InterruptedException{
        int[] pos_carro = new int[2];
        for(int i=0; i<(height/tam_pixel); i++){
            for(int j=0; j<(width/tam_pixel); j++){
                if(matriz_mapa[j][i]==2){
                    pos_carro[0] = j;
                    pos_carro[1] = i;
                    break;
                }
            }
        }
        this.funcion_recursiva(pos_carro[0], pos_carro[1]);
    }
    
    public int bordes(int j, int i){   
        int id_bordes = 0; 
        if(j==0 && i==0){
            id_bordes = 1;        //Esquina superior izquierda identificada
        }
        if(j==(width/tam_pixel)-1 && i==0){
            id_bordes = 2;        //Esquina superior derecha identificada
        }
        if(j==0 && i==(height/tam_pixel)-1){                                                    //IDENTIFICACIÓN DE ESQUINAS
            id_bordes = 3;        //Esquina inferior izquierda identificada
        }
        if(j==(width/tam_pixel)-1 && i==(height/tam_pixel)-1){
            id_bordes = 4;        //Esquina inferior derecha identificada
        }
        if(j==0 && i!=0 && i!=(height/tam_pixel)-1){
            id_bordes = 5;        //Borde izquierdo identificado
        }
        if(j!=0 && j!=(width/tam_pixel)-1 && i==0){
            id_bordes = 6;        //Borde superior identificado
        }
        if(j==(width/tam_pixel)-1 && i!=0 && i!=(height/tam_pixel)-1){                      //IDENTIFICACIÓN DE BORDES
            id_bordes = 7;        //Borde derecho identificado
        }
        if(j!=0 && j!=(width/tam_pixel)-1 && i==(height/tam_pixel)-1){
            id_bordes = 8;        //Borde inferior identificado
        }
        return id_bordes;
    }
    
    public int[] logica(int sen_izq, int sen_der, int sen_sup, int sen_inf, int x, int y){
        int[] next_pos = new int[2];
        boolean direccion_encontrada = false;
        switch(direccion){
            case 0:
                if(sen_inf==1 || sen_inf==2){          //Se prioriza el moviminiento hacia abajo y hacia a la derecha antes que arriba y a la izquierda
                    marcar_cuadro(x, y, 5);            //El parametro es 5, representa un cuadro marcado, es decir ya se recorrió
                    next_pos[0] = x;
                    next_pos[1] = y+1;
                    direccion_encontrada = true;
                }
                if(sen_inf==0 || sen_inf==3 || sen_inf==6){          //Se verifica si hay buhos o muros, eso con el fin de tomar una nueva dirección
                    if(sen_sup==5){
                        if(sen_der!=0 && sen_der!=3 && sen_der!=6 && sen_der!=5){
                            marcar_cuadro(x, y, 5);
                            next_pos[0] = x+1;              //Abajo hay un muro, arriba hay una marca, a la derecha no hay ni 0 ni 3, entonces se puede girar a la derecha
                            next_pos[1] = y;
                            direccion = 2;
                            direccion_encontrada = true;
                        }else{
                            if(sen_izq!=0 && sen_izq!=3 && sen_izq!=6 && sen_izq!=5){
                                marcar_cuadro(x, y, 5);
                                next_pos[0] = x-1;                            //Abajo hay un muro, arriba hay una marca, a la derecha hay un 0 o un 3, entonces se debe girar a la izquierda
                                next_pos[1] = y;
                                direccion = 3;
                                direccion_encontrada = true;
                            }else{
                                marcar_cuadro(x, y, 6);                 //El parametro es 6, representa un cuadro remarcado, es decir ya se identificó que no hay salida por ese camino
                                next_pos[0] = x;
                                next_pos[1] = y-1;
                                direccion = 1;
                                direccion_encontrada = true;
                            }
                        }
                    }else{
                        if(sen_sup==6){
                            if(matriz_mapa[x][y]==5 || matriz_mapa[x][y]==6){
                                marcar_cuadro(x, y, 6);
                            }else{
                                marcar_cuadro(x, y, 5);
                            }
                            if(sen_der!=0 && sen_der!=3 && sen_der!=6){
                                next_pos[0] = x+1;              //Abajo hay un muro, arriba hay una marca, a la derecha no hay ni 0 ni 3, entonces se puede girar a la derecha
                                next_pos[1] = y;
                                direccion = 2;
                                direccion_encontrada = true;
                            }else{
                                if(sen_izq!=0 && sen_izq!=3 && sen_izq!=6){
                                    next_pos[0] = x-1;                            //Abajo hay un muro, arriba hay una marca, a la derecha hay un 0 o un 3, entonces se debe girar a la izquierda
                                    next_pos[1] = y;
                                    direccion = 3;
                                    direccion_encontrada = true;
                                }   
                            }
                        }else{
                            next_pos[0] = x;                //Abajo hay un muro, arriba no hay una marca, entonces la siguiente direccion es arriba
                            next_pos[1] = y-1;
                            direccion = 1;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(sen_inf==5){
                    marcar_cuadro(x, y, 6);               //Si se pasa por un cuadro que ya fue recorrido entonces se remarca con el 6
                    if(sen_der!=0 && sen_der!=3 && sen_der!=6 && sen_der!=5){
                        next_pos[0] = x+1;
                        next_pos[1] = y;
                        direccion = 2;
                        direccion_encontrada = true;
                    }else{
                        if(sen_izq!=0 && sen_izq!=3 && sen_izq!=6 && sen_izq!=5){
                            next_pos[0] = x-1;
                            next_pos[1] = y;
                            direccion = 3;
                            direccion_encontrada = true;
                        }else{
                            next_pos[0] = x;
                            next_pos[1] = y+1;
                            direccion = 0;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(!direccion_encontrada){
                    next_pos[0] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[0];
                    next_pos[1] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[1];
                    direccion = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[2];
                }
                break;
            case 1:
                if(sen_sup==1 || sen_sup==2){          //Se prioriza el moviminiento hacia abajo y hacia a la derecha antes que arriba y a la izquierda
                    marcar_cuadro(x, y, 5);            //El parametro es 5, representa un cuadro marcado, es decir ya se recorrió
                    next_pos[0] = x;
                    next_pos[1] = y-1;
                    direccion_encontrada = true;
                }
                if(sen_sup==0 || sen_sup==3 || sen_sup==6){          //Se verifica si hay buhos o muros, eso con el fin de tomar una nueva dirección
                    if(sen_inf==5){
                        if(sen_der!=0 && sen_der!=3 && sen_der!=6 && sen_der!=5){
                            marcar_cuadro(x, y, 5);
                            next_pos[0] = x+1;              //Arriba hay un muro, abajo hay una marca, a la derecha no hay ni 0 ni 3, entonces se puede girar a la derecha
                            next_pos[1] = y;
                            direccion = 2;
                            direccion_encontrada = true;
                        }else{
                            if(sen_izq!=0 && sen_izq!=3 && sen_izq!=6 && sen_izq!=5){
                                marcar_cuadro(x, y, 5);
                                next_pos[0] = x-1;              //Arriba hay un muro, abajo hay una marca, a la derecha hay un 0 o un 3, entonces se debe girar a la izquierda
                                next_pos[1] = y;
                                direccion = 3;
                                direccion_encontrada = true;
                            }else{
                                marcar_cuadro(x, y, 6);                 //El parametro es 6, representa un cuadro remarcado, es decir ya se identificó que no hay salida por ese camino
                                next_pos[0] = x;
                                next_pos[1] = y+1;
                                direccion = 0;
                                direccion_encontrada = true;
                            } 
                        }
                    }else{
                        if(sen_inf==6){
                            if(matriz_mapa[x][y]==5 || matriz_mapa[x][y]==6){
                                marcar_cuadro(x, y, 6);
                            }else{
                                marcar_cuadro(x, y, 5);
                            }
                            if(sen_der!=0 && sen_der!=3 && sen_der!=6){
                                next_pos[0] = x+1;              //Arriba hay un muro, abajo hay una marca, a la derecha no hay ni 0 ni 3, entonces se puede girar a la derecha
                                next_pos[1] = y;
                                direccion = 2;
                                direccion_encontrada = true;
                            }else{
                                if(sen_izq!=0 && sen_izq!=3 && sen_izq!=6){
                                    next_pos[0] = x-1;              //Arriba hay un muro, abajo hay una marca, a la derecha hay un 0 o un 3, entonces se debe girar a la izquierda
                                    next_pos[1] = y;
                                    direccion = 3;
                                    direccion_encontrada = true;
                                }
                            }
                        }else{
                            next_pos[0] = x;                //Arriba hay un muro, abajo no hay una marca, entonces la siguiente direccion es abajo
                            next_pos[1] = y+1;
                            direccion = 0;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(sen_sup==5){
                    marcar_cuadro(x, y, 6);               //Si se pasa por un cuadro que ya fue recorrido entonces se remarca con el 6
                    if(sen_der!=0 && sen_der!=3 && sen_der!=6 && sen_izq!=5){
                        next_pos[0] = x+1;
                        next_pos[1] = y;
                        direccion = 2;
                        direccion_encontrada = true;
                    }else{
                        if(sen_izq!=0 && sen_izq!=3 && sen_izq!=6 && sen_izq!=5){
                            next_pos[0] = x-1;
                            next_pos[1] = y;
                            direccion = 3;
                            direccion_encontrada = true;
                        }else{
                            next_pos[0] = x;
                            next_pos[1] = y-1;
                            direccion = 1;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(!direccion_encontrada){
                    next_pos[0] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[0];
                    next_pos[1] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[1];
                    direccion = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[2];
                }
                break;
            case 2:
                if(sen_der==1 || sen_der==2){          //Se prioriza el moviminiento hacia abajo y hacia a la derecha antes que arriba y a la izquierda
                    marcar_cuadro(x, y, 5);            //El parametro es 5, representa un cuadro marcado, es decir ya se recorrió
                    next_pos[0] = x+1;
                    next_pos[1] = y;
                    direccion_encontrada = true;
                }
                if(sen_der==0 || sen_der==3 || sen_der==6){          //Se verifica si hay buhos o muros, eso con el fin de tomar una nueva dirección
                    if(sen_izq==5){
                        if(sen_inf!=0 && sen_inf!=3 && sen_inf!=6 && sen_inf!=5){
                            marcar_cuadro(x, y, 5);
                            next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo no hay ni 0 ni 3, entonces se puede girar hacia abajo
                            next_pos[1] = y+1;
                            direccion = 0;
                            direccion_encontrada = true;
                        }else{
                            if(sen_sup!=0 && sen_sup!=3 && sen_sup!=6 && sen_sup!=5){
                                marcar_cuadro(x, y, 5);
                                next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo hay un 0 o un 3, entonces se debe girar hacia arriba
                                next_pos[1] = y-1;
                                direccion = 1;
                                direccion_encontrada = true;
                            }else{
                                marcar_cuadro(x, y, 6);                 //El parametro es 6, representa un cuadro remarcado, es decir ya se identificó que no hay salida por ese camino
                                next_pos[0] = x-1;
                                next_pos[1] = y;
                                direccion = 3;
                                direccion_encontrada = true;
                            } 
                        }
                    }else{
                        if(sen_izq==6){
                            if(matriz_mapa[x][y]==5 || matriz_mapa[x][y]==6){
                                marcar_cuadro(x, y, 6);
                            }else{
                                marcar_cuadro(x, y, 5);
                            }
                            if(sen_inf!=0 && sen_inf!=3 && sen_inf!=6){
                                next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo no hay ni 0 ni 3, entonces se puede girar hacia abajo
                                next_pos[1] = y+1;
                                direccion = 0;
                                direccion_encontrada = true;
                            }else{
                                if(sen_sup!=0 && sen_sup!=3 && sen_sup!=6){
                                    next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo hay un 0 o un 3, entonces se debe girar hacia arriba
                                    next_pos[1] = y-1;
                                    direccion = 1;
                                    direccion_encontrada = true;
                                }
                            }
                        }else{
                            next_pos[0] = x-1;                //A la derecha hay un muro, a la izquierda no hay una marca, entonces la siguiente direccion es izquierda
                            next_pos[1] = y;
                            direccion = 3;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(sen_der==5){
                    marcar_cuadro(x, y, 6);               //Si se pasa por un cuadro que ya fue recorrido entonces se remarca con el 6
                    if(sen_inf!=0 && sen_inf!=3 && sen_inf!=6 && sen_inf!=5){
                        next_pos[0] = x;
                        next_pos[1] = y+1;
                        direccion = 0;
                        direccion_encontrada = true;
                    }else{
                        if(sen_sup!=0 && sen_sup!=3 && sen_sup!=6 && sen_sup!=5){
                            next_pos[0] = x;
                            next_pos[1] = y-1;
                            direccion = 1;
                            direccion_encontrada = true;
                        }else{
                            next_pos[0] = x+1;
                            next_pos[1] = y;
                            direccion = 2;
                            direccion_encontrada = true;
                        } 
                    }
                }
                if(!direccion_encontrada){
                    next_pos[0] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[0];
                    next_pos[1] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[1];
                    direccion = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[2];
                }
                break;
            case 3:
                if(sen_izq==1 || sen_izq==2){          //Se prioriza el moviminiento hacia abajo y hacia a la derecha antes que arriba y a la izquierda
                    marcar_cuadro(x, y, 5);            //El parametro es 5, representa un cuadro marcado, es decir ya se recorrió
                    next_pos[0] = x-1;
                    next_pos[1] = y;
                    direccion_encontrada = true;
                }
                if(sen_izq==0 || sen_izq==3 || sen_izq==6){          //Se verifica si hay buhos o muros, eso con el fin de tomar una nueva dirección
                    if(sen_der==5){
                        if(sen_inf!=0 && sen_inf!=3 && sen_inf!=6 && sen_inf!=5){
                            marcar_cuadro(x, y, 5);
                            next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo no hay ni 0 ni 3, entonces se puede girar hacia abajo
                            next_pos[1] = y+1;
                            direccion = 0;
                            direccion_encontrada = true;
                        }else{
                            if(sen_sup!=0 && sen_sup!=3 && sen_sup!=6 && sen_sup!=5){
                                marcar_cuadro(x, y, 5);
                                next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo hay un 0 o un 3, entonces se debe girar hacia arriba
                                next_pos[1] = y-1;
                                direccion = 1;
                                direccion_encontrada = true;
                            }else{
                                marcar_cuadro(x, y, 6);                 //El parametro es 6, representa un cuadro remarcado, es decir ya se identificó que no hay salida por ese camino
                                next_pos[0] = x+1;
                                next_pos[1] = y;
                                direccion = 2;
                                direccion_encontrada = true;  
                            }
                        }
                    }else{
                        if(sen_der==6){
                            if(matriz_mapa[x][y]==5 || matriz_mapa[x][y]==6){
                                marcar_cuadro(x, y, 6);
                            }else{
                                marcar_cuadro(x, y, 5);
                            }
                            if(sen_inf!=0 && sen_inf!=3 && sen_inf!=6){
                                next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo no hay ni 0 ni 3, entonces se puede girar hacia abajo
                                next_pos[1] = y+1;
                                direccion = 0;
                                direccion_encontrada = true;
                            }else{
                                if(sen_sup!=0 && sen_sup!=3 && sen_sup!=6){
                                    next_pos[0] = x;              //A la derecha hay un muro, a la izquierda hay una marca, abajo hay un 0 o un 3, entonces se debe girar hacia arriba
                                    next_pos[1] = y-1;
                                    direccion = 1;
                                    direccion_encontrada = true;
                                }
                            }
                        }else{
                            next_pos[0] = x+1;                //A la derecha hay un muro, a la izquierda no hay una marca, entonces la siguiente direccion es izquierda
                            next_pos[1] = y;
                            direccion = 2;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(sen_izq==5){
                    marcar_cuadro(x, y, 6);               //Si se pasa por un cuadro que ya fue recorrido entonces se remarca con el 6
                    if(sen_inf!=0 && sen_inf!=3 && sen_inf!=6 && sen_inf!=5){
                        next_pos[0] = x;
                        next_pos[1] = y+1;
                        direccion = 0;
                        direccion_encontrada = true;
                    }else{
                        if(sen_sup!=0 && sen_sup!=3 && sen_sup!=6 && sen_sup!=5){
                            next_pos[0] = x;
                            next_pos[1] = y-1;
                            direccion = 1;
                            direccion_encontrada = true;
                        }else{
                            next_pos[0] = x-1;
                            next_pos[1] = y;
                            direccion = 3;
                            direccion_encontrada = true;
                        }
                    }
                }
                if(!direccion_encontrada){
                    next_pos[0] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[0];
                    next_pos[1] = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[1];
                    direccion = this.encontrar_direccion(sen_inf, sen_sup, sen_der, sen_izq, x, y)[2];
                }
                break;
        }
        return next_pos;
    }
    
    public void marcar_cuadro(int x, int y, int parametro){
        matriz_mapa[x][y] = parametro;        //El número 5 va a representar el marcado de un cuadro ya recorrido
    }
    
    public int[] encontrar_direccion(int inf, int sup, int der, int izq, int x, int y){
        int[] arreglo = new int[3];
        switch(direccion){
            case 0:
                if(izq==6){
                    arreglo[0] = x-1;
                    arreglo[1] = y;
                    arreglo[2] = 3;
                }
                if(der==6){
                    arreglo[0] = x+1;
                    arreglo[1] = y;
                    arreglo[2] = 2;
                }
                if(sup==6){
                    arreglo[0] = x;
                    arreglo[1] = y-1;
                    arreglo[2] = 1;
                }
                if(inf==6){
                    arreglo[0] = x;
                    arreglo[1] = y+1;
                    arreglo[2] = 0;
                }
                break;
            case 1:
                if(izq==6){
                    arreglo[0] = x-1;
                    arreglo[1] = y;
                    arreglo[2] = 3;
                }
                if(der==6){
                    arreglo[0] = x+1;
                    arreglo[1] = y;
                    arreglo[2] = 2;
                }
                if(inf==6){
                    arreglo[0] = x;
                    arreglo[1] = y+1;
                    arreglo[2] = 0;
                }
                if(sup==6){
                    arreglo[0] = x;
                    arreglo[1] = y-1;
                    arreglo[2] = 1;
                }
                break;
            case 2:
                if(sup==6){
                    arreglo[0] = x;
                    arreglo[1] = y-1;
                    arreglo[2] = 1;
                }
                if(inf==6){
                    arreglo[0] = x;
                    arreglo[1] = y+1;
                    arreglo[2] = 0;
                }
                if(izq==6){
                    arreglo[0] = x-1;
                    arreglo[1] = y;
                    arreglo[2] = 3;
                }
                if(der==6){
                    arreglo[0] = x+1;
                    arreglo[1] = y;
                    arreglo[2] = 2;
                }
                break;
            case 3:
                if(sup==6){
                    arreglo[0] = x;
                    arreglo[1] = y-1;
                    arreglo[2] = 1;
                }
                if(inf==6){
                    arreglo[0] = x;
                    arreglo[1] = y+1;
                    arreglo[2] = 0;
                }
                if(der==6){
                    arreglo[0] = x+1;
                    arreglo[1] = y;
                    arreglo[2] = 2;
                }
                if(izq==6){
                    arreglo[0] = x-1;
                    arreglo[1] = y;
                    arreglo[2] = 3;
                }
                break;
        }
        return arreglo;
    }
    
    public void funcion_recursiva(int j, int i) throws InterruptedException{
        int sensor_izquierdo, sensor_derecho, sensor_superior, sensor_inferior;
        int[] next_position;
        switch(this.bordes(j, i)){
            default:
                sensor_izquierdo = matriz_mapa[j-1][i];
                sensor_derecho = matriz_mapa[j+1][i];
                sensor_superior = matriz_mapa[j][i-1];
                sensor_inferior = matriz_mapa[j][i+1];
                break;
            case 1:
                sensor_izquierdo = 0;
                sensor_derecho = matriz_mapa[j+1][i];         //Si se detecta un borde entonces lo pone como 0, es decir, un muro
                sensor_superior = 0;
                sensor_inferior = matriz_mapa[j][i+1];
                break;
            case 2:
                sensor_izquierdo = matriz_mapa[j-1][i];
                sensor_derecho = 0;                       
                sensor_superior = 0;
                sensor_inferior = matriz_mapa[j][i+1];
                break;
            case 3:
                sensor_izquierdo = 0;
                sensor_derecho = matriz_mapa[j+1][i];           
                sensor_superior = matriz_mapa[j][i-1];
                sensor_inferior = 0;
                break;
            case 4:
                sensor_izquierdo = matriz_mapa[j-1][i];
                sensor_derecho = 0;                     
                sensor_superior = matriz_mapa[j][i-1];
                sensor_inferior = 0;
                break;
            case 5:
                sensor_izquierdo = 0;
                sensor_derecho = matriz_mapa[j+1][i];            
                sensor_superior = matriz_mapa[j][i-1];
                sensor_inferior = matriz_mapa[j][i+1];
                break;
            case 6:
                sensor_izquierdo = matriz_mapa[j-1][i];
                sensor_derecho = matriz_mapa[j+1][i];            
                sensor_superior = 0;
                sensor_inferior = matriz_mapa[j][i+1];
                break;
            case 7:
                sensor_izquierdo = matriz_mapa[j-1][i];
                sensor_derecho = 0;                         
                sensor_superior = matriz_mapa[j][i-1];
                sensor_inferior = matriz_mapa[j][i+1];
                break;
            case 8:
                sensor_izquierdo = matriz_mapa[j-1][i];
                sensor_derecho = matriz_mapa[j+1][i];           
                sensor_superior = matriz_mapa[j][i-1];
                sensor_inferior = 0;
                break;
        }
        if(matriz_mapa[j][i]==1 || matriz_mapa[j][i]==2 || matriz_mapa[j][i]==5 || matriz_mapa[j][i]==6){      //El 5 representa un cuadro marcado, pero se puede volver a recorrer, no se recorre solamente cuando está remarcado
            next_position = this.logica(sensor_izquierdo, sensor_derecho, sensor_superior, sensor_inferior, j, i);
            if(matriz_mapa[next_position[0]][next_position[1]]!=4){
                Thread.sleep(400);
                this.imprimir_matriz();
                this.funcion_recursiva(next_position[0], next_position[1]);
            }
        }
    }
    
    public void imprimir_matriz(){
        for(int j=0; j<(height/tam_pixel); j++){         //Se recorre la matriz generada por la ampliación de cuadricula
            for(int i=0; i<(width/tam_pixel); i++){
                switch(matriz_mapa[i][j]){
                    case 0:
                        System.out.print(matriz_mapa[i][j]);             //Con este switch lo que se pretende es dar una ilustración óptica de la matriz
                        break;
                    case 1:
                        System.out.print(ANSI_RED_BACKGROUND + ANSI_RED + matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 2:
                        System.out.print(ANSI_GREEN_BACKGROUND + ANSI_GREEN + matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 3:
                        System.out.print(ANSI_YELLOW_BACKGROUND + ANSI_YELLOW + matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 4:
                        System.out.print(ANSI_BLUE_BACKGROUND + ANSI_BLUE + matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 5:
                        System.out.print(ANSI_CYAN_BACKGROUND + ANSI_CYAN + matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 6:
                        System.out.print(ANSI_RED_BACKGROUND + ANSI_RED + matriz_mapa[i][j] + ANSI_RESET);
                        break;
                }
            }
            System.out.println("");
        }
        System.out.println(ANSI_PURPLE_BACKGROUND + "0-> MUROS   1-> CAMINOS   2-> CARRO    3-> BUHOS   4-> CASA" + ANSI_RESET);
        System.out.println("");
    }
}

