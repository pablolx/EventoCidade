import java.util.UUID;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String cidade;

    public Usuario(String nome, String email, String cidade) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = email;
        this.cidade = cidade;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCidade() { return cidade; }

    @Override
    public String toString() {
        return nome + " (" + email + ") - " + cidade;
    }
}