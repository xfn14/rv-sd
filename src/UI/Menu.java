package UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Menu {

    public interface Handler {
        void execute();
    }

    public interface PreCondition {
        boolean validate();
    }

    private static BufferedReader is = new BufferedReader(new InputStreamReader(System.in));

    private String titulo;
    private List<String> opcoes;
    private List<PreCondition> disponivel;
    private List<Handler> handlers;

    public Menu(String titulo, List<String> opcoes) {
        this.titulo = titulo;
        this.opcoes = new ArrayList(opcoes);
        this.disponivel = new ArrayList();
        this.handlers = new ArrayList();
        this.opcoes.forEach(s -> {
            this.disponivel.add(() -> true);
            this.handlers.add(() -> System.out.println("\nATENÇÃO: Opção não implementada!"));
        });
    }

    public Menu(List<String> opcoes) {
        this("Menu", opcoes);
    }

    public Menu(String[] opcoes) {
        this(Arrays.asList(opcoes));
    }

    public Menu(String titulo, String[] opcoes) {
        this(titulo, Arrays.asList(opcoes));
    }

    public void runOnce() {
        int op;
        show();
        op = readOption();
        if (op > 0 && !this.disponivel.get(op - 1).validate()) {
            System.out.println("Opção indisponível!");
        } else if (op > 0) {
            this.handlers.get(op - 1).execute();
        }
    }

    public void run() {
        int op;
        do {
            show();
            op = readOption();
            if (op > 0 && !this.disponivel.get(op - 1).validate()) {
                System.out.println("Opção indisponível! Tente novamente.");
            } else if (op > 0) {
                this.handlers.get(op - 1).execute();
            }
        } while (op != 0);
    }


    public void setPreCondition(int i, PreCondition b) {
        this.disponivel.set(i - 1, b);
    }


    public void setHandler(int i, Handler h) {
        this.handlers.set(i - 1, h);
    }

    private void show() {
        System.out.println("\n *** " + this.titulo + " *** ");
        for (int i = 0; i < this.opcoes.size(); i++) {
            System.out.print(i + 1);
            System.out.print(" - ");
            System.out.println(this.disponivel.get(i).validate() ? this.opcoes.get(i) : this.opcoes.get(i) + " (Não disponível)");
        }
        System.out.println("0 - Sair");
    }

    private int readOption() {
        int op;

        System.out.print("Opção: ");
        try {
            String line = is.readLine();
            op = Integer.parseInt(line);
        } catch (Exception e) { // Não foi inscrito um int
            op = -1;
        }
        if (op < 0 || op > this.opcoes.size()) {
            System.out.println("Opção Inválida!!!");
            op = -1;
        }
        return op;
    }
}


