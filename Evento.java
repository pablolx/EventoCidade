import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Evento {
    private String nome, endereco, categoria, descricao;
    private LocalDateTime horario;
    private ArrayList<String> participantes;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Evento(String nome, String endereco, String categoria, LocalDateTime horario, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
        this.participantes = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public LocalDateTime getHorario() { return horario; }
    public ArrayList<String> getParticipantes() { return participantes; }

    public void adicionarParticipante(String email) {
        if (!participantes.contains(email)) {
            participantes.add(email);
        }
    }

    public void removerParticipante(String email) {
        participantes.remove(email);
    }

    public boolean estaOcorrendo() {
        LocalDateTime agora = LocalDateTime.now();
        return horario.isBefore(agora.plusMinutes(1)) && horario.isAfter(agora.minusHours(2));
    }

    public boolean jaAconteceu() {
        return horario.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(nome)
          .append("\nCategoria: ").append(categoria)
          .append("\nEndereço: ").append(endereco)
          .append("\nData/Hora: ").append(horario.format(FORMATTER))
          .append("\nDescrição: ").append(descricao)
          .append("\nStatus: ").append(getStatus());
        return sb.toString();
    }

    private String getStatus() {
        if (jaAconteceu()) return "Finalizado";
        if (estaOcorrendo()) return "Em andamento";
        return "Agendado";
    }

    public String toData() {
        return String.join(";",
            nome,
            endereco,
            categoria,
            horario.format(FORMATTER),
            descricao,
            String.join(",", participantes)
        );
    }

    public static Evento fromData(String linha) {
        String[] partes = linha.split(";");
        if (partes.length < 5) {
            throw new IllegalArgumentException("Formato de dados inválido");
        }

        LocalDateTime horario = LocalDateTime.parse(partes[3], FORMATTER);
        Evento e = new Evento(partes[0], partes[1], partes[2], horario, partes[4]);
        
        if (partes.length > 5 && !partes[5].isEmpty()) {
            for (String p : partes[5].split(",")) {
                e.adicionarParticipante(p);
            }
        }
        return e;
    }
}