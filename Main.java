import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EventoCidade sistema = new EventoCidade();
        sistema.carregarEventos();
        sistema.executar();
        sistema.salvarEventos();
    }
}