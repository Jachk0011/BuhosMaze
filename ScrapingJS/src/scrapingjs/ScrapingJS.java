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
import javax.imageio.ImageIO;

public class ScrapingJS {
    
    int[][] matriz_mapa;
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";     //Métodos para obtención de colores de subrayado
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    int tam_pixel = 24;   //Tamaño de cuadricula, 24x24 pixeles
    
    public static void main(String args[])throws IOException{
        
        ScrapingJS funciones = new ScrapingJS();
        BufferedImage img = null;
        File f = null;

        try{
          f = new File("images\\Level1.jpg");    //Lectura de imagen, se debe poner la ruta de la imagen previamente abstraida mediante el programa realizado en python adjunto
          img = ImageIO.read(f);
        }catch(IOException e){      //Lectura de imagen
          System.out.println(e);
        }

        int width = img.getWidth();
        width = width - (width%funciones.tam_pixel);    //Se recorta el tamaño de la imagen a un tamaño multiplo del tamaño de cuadro, para que salgan cuadros exactos y cuadrados
        int height = img.getHeight();
        height = height - (height%funciones.tam_pixel);
        
        funciones.mapear(height, width, img);     //Esta funcion se encarga de "eliminar" los pixeles que no aportan información
        funciones.ampliar_cuadricula(height, width, img);    //Esta función amplia el mapa de pixeles y lo transforma en una matriz de números dependiendo el objeto
        for(int j=0; j<(height/funciones.tam_pixel); j++){         //Se recorre la matriz generada por la ampliación de cuadricula
            for(int i=0; i<(width/funciones.tam_pixel); i++){
                switch(funciones.matriz_mapa[i][j]){
                    case 0:
                        System.out.print(funciones.matriz_mapa[i][j]);             //Con este switch lo que se pretende es dar una ilustración óptica de la matriz
                        break;
                    case 1:
                        System.out.print(ANSI_RED_BACKGROUND + funciones.matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 2:
                        System.out.print(ANSI_GREEN_BACKGROUND  + funciones.matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 3:
                        System.out.print(ANSI_YELLOW_BACKGROUND  + funciones.matriz_mapa[i][j] + ANSI_RESET);
                        break;
                    case 4:
                        System.out.print(ANSI_BLUE_BACKGROUND  + funciones.matriz_mapa[i][j] + ANSI_RESET);
                        break;
                }
            }
            System.out.println("");
        }
        System.out.print( "  0-> MUROS \n  1-> CAMINOS \n  2-> CARRO  \n  3-> BUHOS \n  4-> CASA" + ANSI_RESET);
    }
    
    public BufferedImage mapear(int height, int width, BufferedImage img){
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
        if(R==136 && G==0 && B==45){
            col = 1; //CARRO
        }
        if((R>53 && R<56)||(G>63 && G<76)||(B>61 && B<77)){  //BUHOS
            col = 1;
        }
        if(R==101 && G==1 && B==1){
            col = 1; //CASA
        }
        return col;
    }
    
    public BufferedImage ampliar_cuadricula(int height, int width, BufferedImage img){
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
                        if(R==136 && G==0 && B==45){   //CARRO
                            objeto = true;                                    //La variable objeto avisa si el pixel encontrado es un objeto importante
                            matriz_mapa[x/tam_pixel][y/tam_pixel] = 2;          //Se mapea con el codigo 2, representa carro
                        }
                        if((R>53 && R<56)&&(G>63 && G<76)&&(B>61 && B<77)){   //BUHOS
                            objeto = true;
                            matriz_mapa[x/tam_pixel][y/tam_pixel] = 3;            //Se mapea con el codigo 3, representa buho
                        }
                        if(R==101 && G==1 && B==1){   //CASA
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
}

