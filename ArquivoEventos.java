import java.io.*;
import java.util.ArrayList;

public class ArquivoEventos {
    private static final String ARQUIVO = "events.data";

    public static void salvar(ArrayList<Evento> eventos) {
        try (PrintWriter out = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (Evento e : eventos) {
                out.println(e.toData());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    public static ArrayList<Evento> carregar() {
        ArrayList<Evento> eventos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                eventos.add(Evento.fromData(linha));
            }
        } catch (IOException e) {
            System.out.println("Arquivo de eventos não encontrado. Um novo será criado.");
        }
        return eventos;
    }
}