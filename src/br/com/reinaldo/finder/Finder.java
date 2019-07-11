/*
 * Finder.java
 *
 * Created on June 27, 2012, 5:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.reinaldo.finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Procurar um texto em um arquivo e exibir a linha + x linhas antes + x linhas depois
 * @author mg25752
 */
public class Finder {
    
    static int antes = 0;
    static int depois = 0;
    static String path = "";
    static String texto = "";
    static boolean clean = false;
    static boolean ignorCase = false;
    
    /* ./finder texto arquivo -a 20 -d 30   */
    public static void main(String[] args) {
        try {
            validarArgs(args);
            ler(new File(path));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    static void validarArgs(final String[] args) throws Exception {
        if (args == null || args.length == 0) {
            throw(new Exception(ajuda()));
        }
        if (args[0].equals("-h")) {
            throw(new Exception(ajuda()));
        }
        for (int i = 0; i < args.length; i++) {
            // Verifica se o parametro atual e uma pasta ou arquivo
            File f = new File(args[i]);
            if (f.exists() && (f.isDirectory() || f.isFile())) {
                path = args[i];
            } 
            // Verifica se foi informada o parametro de quantidade de linhas antes
            else if (args[i].equalsIgnoreCase("-a")) {
                try {
                    antes = Integer.parseInt(args[i+1]);
                } catch (Exception ex) {
                    throw(new Exception("Um inteiro deve ser informado depois de -a"));
                }
            }
            // Verifica se foi informado o parametro de quantidade de linhas depois
            else if (args[i].equalsIgnoreCase("-d")) {
                try {
                    depois = Integer.parseInt(args[i+1]);
                } catch (Exception ex) {
                    throw (new Exception("Um inteiro deve ser informado depois de -d"));
                }
            }
            // Verifica se foi informado o parametro CLEAN, para exibir apenas o texto encontrado
            else if (args[i].equals("-c")) {
                clean = true;
            }
            // Verifica se foi informado o parametro ignorCase
            else if (args[i].equals("-i")) {
                ignorCase = true;
            }
            // Se nenhum dos parametros acima for atendido, entao o parametro atual e o texto que sera procurado
            else {
                if (i > 0) {
                    if (!"-d".equals(args[i-1]) && !"-a".equals(args[i-1])) {
                        texto = args[i];
                    }
                } else {
                    texto = args[i];
                }
            }
        }
        
        if (path == null || "".equals(path)) {
            throw new Exception("Informe um arquivo ou pasta para pesquisa");
        }
        if (texto == null || "".equals(texto)) {
            throw new Exception("Informe o texto que sera procurado");
        }
    }
    
    static void ler(final File f) {
        try {
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (int i = 0; i < files.length; i++) {
                    ler(files[i]);
                    System.gc();
                }
            } else {
                String ext = f.getAbsolutePath().toLowerCase();
                if (ext.endsWith(".log") || ext.endsWith(".txt") || ext.endsWith("err") || ext.endsWith(".java") 
                    || ext.endsWith(".c") || ext.endsWith(".h") || ext.endsWith(".out") || ext.endsWith(".properties") 
                    || ext.endsWith("xml") || ext.endsWith(".mf") || ext.endsWith(".css") || ext.endsWith(".js")
                    || ext.endsWith(".jsp") || ext.endsWith(".htm") || ext.endsWith(".html") || ext.endsWith(".cfg")
                    || ext.endsWith(".cnf")) {
                    Obj obj = lerArquivo(f);
                    procurar(obj);
                }
            }
        } catch (Exception ex) {}
    }
    
    static Obj lerArquivo(final File file) throws Exception {
        Obj o = new Obj();
        List conteudo = new ArrayList();
        try {
            o.setPath(file.getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String linha = "";
            while ((linha=br.readLine()) != null) {
                conteudo.add(linha);
            }
            br.close();
        } catch (Exception ex) {
            System.out.println("Nao foi possivel ler o arquivo " + path);
        }
        o.setConteudo(conteudo);
        return o;
    }
    
    static void procurar(Obj atual) {
        System.gc();
        StringBuffer sb = new StringBuffer();
        List conteudo = atual.getConteudo();
        boolean encontrou = false;
        final int tamanho = conteudo.size();
        for (int x = 0; x < tamanho; x++) {
            if (ignorCase) {
                if (((String)conteudo.get(x)).toLowerCase().indexOf(texto.toLowerCase()) >= 0) {
                    capturarTexto(atual.getPath(), conteudo, x);
                    encontrou = true;
                }
            } else {
                if (((String)conteudo.get(x)).indexOf(texto) >= 0) {
                    capturarTexto(atual.getPath(), conteudo, x);
                    encontrou = true;
                }
            }
            System.out.flush();
            
        }
    }
    
    static void capturarTexto(final String path, List conteudo, int indice) {
        int inicio = (indice-antes);
        int fim = (indice + depois);
        
        if (inicio < 0) inicio = 0;
        if (fim >= conteudo.size()) fim = conteudo.size()-1;
        
        for (int i = inicio; i <= fim; i++) {
            if (clean) {
                System.out.println((String) conteudo.get(i));
            } else {
                System.out.println(path + " ["+(i+1)+"]\t" + (String) conteudo.get(i));
            }
        }
        System.out.println("");
    }
    
    static String ajuda() {
        String retorno = 
                "========================== FINDER ==========================\n" +
                " - Procura um texto em um arquivo ou pasta.\n" +
                " - Informa o nome do arquivo, a linha onde o\n" +
                " texto foi encontrado e imprime algumas linhas\n" +
                " antes e depois, dependendo dos parametros.\n\n" +
                " :: UTILIZACAO:\n" +
                " ./finder.jar -i -c -a 2 -d 7 textoProcurado localOndeProcurar \n\n" +
                "* Os parametros a seguir sao opcionais, e nao possuem ordem fixa\n\n" +
                "-d <n>\n" +
                "Especifica o numero de linhas apos a linha onde o texto foi encontrado\n\n" +
                "-a <n>\n" +
                "Especifica o numero de linhas antes da linha onde o texto foi encontrado\n\n" +
                "Ao informar os parametros -a e/ou -d, a quantidade de linhas especificada\n" +
                "sera impressa antes e/ou depois da linha onde o texto foi encontrado\n\n" +
                "-c\n" +
                "Imprime apenas os textos encontrados, ignorando o nome do arquivo e a linha\n\n" +
                "-i\n" +
                "Deixa as comparacoes NAO sensiveis a caixa.\n" +
                "============================================================\n\n";
        return retorno;
    }
    
}
