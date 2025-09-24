import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventoCidade {
	private static final String FORMATO_DATA = "dd/MM/yyyy HH:mm";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATO_DATA);
	private static final Set<String> CATEGORIAS_VALIDAS = new HashSet<>(
			Arrays.asList("FESTA", "SHOW", "ESPORTE", "CONFERÊNCIA", "WORKSHOP", "OUTRO"));

	private ArrayList<Evento> eventos = new ArrayList<>();
	private Usuario usuario;
	private Scanner sc = new Scanner(System.in);

	public void executar() {
		try {
			cadastrarUsuario();
			exibirMenu();
		} catch (Exception e) {
			System.out.println("\nErro inesperado: " + e.getMessage());
		}
	}

	private void cadastrarUsuario() {
		System.out.println("\n=== Cadastro de Usuário ===");
		String nome = lerEntradaValida("Nome", this::validarNome);
		String email = lerEntradaValida("Email", this::validarEmail);
		String cidade = lerEntradaValida("Cidade", this::validarCidade);
		usuario = new Usuario(nome, email, cidade);
		System.out.println("\nUsuário cadastrado com sucesso!");
	}

	private void exibirMenu() {
		int opcao;
		do {
			System.out.println("\n=== MENU DO SISTEMA DE EVENTOS ===");
			System.out.println("1. Cadastrar novo evento");
			System.out.println("2. Listar todos os eventos");
			System.out.println("3. Participar de um evento");
			System.out.println("4. Cancelar participação");
			System.out.println("5. Meus eventos");
			System.out.println("6. Eventos em andamento");
			System.out.println("7. Eventos passados");
			System.out.println("0. Sair");
			System.out.print("\nEscolha uma opção: ");

			try {
				opcao = Integer.parseInt(sc.nextLine());
				executarOpcao(opcao);
			} catch (NumberFormatException e) {
				System.out.println("Por favor, digite um número válido.");
				opcao = -1;
			} catch (Exception e) {
				System.out.println("Erro: " + e.getMessage());
				opcao = -1;
			}
		} while (opcao != 0);
	}

	private void executarOpcao(int opcao) {
		switch (opcao) {
		case 1 -> cadastrarEvento();
		case 2 -> listarEventos();
		case 3 -> participarEvento();
		case 4 -> cancelarParticipacao();
		case 5 -> listarMeusEventos();
		case 6 -> listarOcorrendo();
		case 7 -> listarPassados();
		case 0 -> System.out.println("Saindo do sistema...");
		default -> System.out.println("Opção inválida!");
		}
	}

	private String lerEntradaValida(String campo, ValidadorEntrada validador) {
		String entrada;
		do {
			System.out.print(campo + ": ");
			entrada = sc.nextLine().trim();
		} while (!validador.validar(entrada));
		return entrada;
	}

	private boolean validarNome(String nome) {
		if (nome.length() < 3) {
			System.out.println("O nome deve ter pelo menos 3 caracteres.");
			return false;
		}
		return true;
	}

	private boolean validarEmail(String email) {
		if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			System.out.println("Email inválido.");
			return false;
		}
		return true;
	}

	private boolean validarCidade(String cidade) {
		if (cidade.length() < 2) {
			System.out.println("Nome da cidade muito curto.");
			return false;
		}
		return true;
	}

	private void cadastrarEvento() {
		System.out.println("\n=== Cadastro de Evento ===");
		String nome = lerEntradaValida("Nome do evento", this::validarNome);
		String endereco = lerEntradaValida("Endereço", s -> s.length() >= 5);

		System.out.println("\nCategorias disponíveis: " + String.join(", ", CATEGORIAS_VALIDAS));
		String categoria = lerEntradaValida("Categoria", this::validarCategoria).toUpperCase();

		LocalDateTime horario = null;
		while (horario == null) {
			System.out.print("Data e hora (" + FORMATO_DATA + "): ");
			horario = parseData(sc.nextLine());
		}

		String desc = lerEntradaValida("Descrição", s -> s.length() >= 10);

		Evento e = new Evento(nome, endereco, categoria, horario, desc);
		eventos.add(e);
		System.out.println("\nEvento cadastrado com sucesso!");
	}

	private boolean validarCategoria(String categoria) {
		if (!CATEGORIAS_VALIDAS.contains(categoria.toUpperCase())) {
			System.out.println("Categoria inválida!");
			return false;
		}
		return true;
	}

	private LocalDateTime parseData(String dataStr) {
		try {
			LocalDateTime data = LocalDateTime.parse(dataStr, FORMATTER);
			if (data.isBefore(LocalDateTime.now())) {
				System.out.println("Não é possível cadastrar eventos no passado!");
				return null;
			}
			return data;
		} catch (DateTimeParseException e) {
			System.out.println("Formato de data inválido!");
			return null;
		}
	}

	private void listarEventos() {
		if (eventos.isEmpty()) {
			System.out.println("\nNenhum evento cadastrado.");
			return;
		}

		System.out.println("\n=== Lista de Eventos ===");
		eventos.sort(Comparator.comparing(Evento::getHorario));
		for (int i = 0; i < eventos.size(); i++) {
			System.out.println("\nEvento #" + i);
			System.out.println(eventos.get(i));
			System.out.println("Participantes: " + eventos.get(i).getParticipantes().size());
		}
	}

	private void participarEvento() {
		if (eventos.isEmpty()) {
			System.out.println("\nNão há eventos disponíveis.");
			return;
		}

		listarEventos();
		try {
			System.out.print("\nDigite o número do evento: ");
			int idx = Integer.parseInt(sc.nextLine());
			if (idx >= 0 && idx < eventos.size()) {
				Evento evento = eventos.get(idx);
				if (evento.jaAconteceu()) {
					System.out.println("Não é possível participar de eventos passados!");
					return;
				}
				evento.adicionarParticipante(usuario.getEmail());
				System.out.println("Participação confirmada com sucesso!");
			} else {
				System.out.println("Número de evento inválido!");
			}
		} catch (NumberFormatException e) {
			System.out.println("Por favor, digite um número válido.");
		}
	}

	private void cancelarParticipacao() {
		List<Evento> meusEventos = eventos.stream().filter(e -> e.getParticipantes().contains(usuario.getEmail()))
				.toList();

		if (meusEventos.isEmpty()) {
			System.out.println("\nVocê não está participando de nenhum evento.");
			return;
		}

		System.out.println("\n=== Seus Eventos ===");
		for (int i = 0; i < meusEventos.size(); i++) {
			System.out.println("\nEvento #" + i);
			System.out.println(meusEventos.get(i));
		}

		try {
			System.out.print("\nDigite o número do evento para cancelar: ");
			int idx = Integer.parseInt(sc.nextLine());
			if (idx >= 0 && idx < meusEventos.size()) {
				Evento evento = meusEventos.get(idx);
				evento.removerParticipante(usuario.getEmail());
				System.out.println("Participação cancelada com sucesso!");
			} else {
				System.out.println("Número de evento inválido!");
			}
		} catch (NumberFormatException e) {
			System.out.println("Por favor, digite um número válido.");
		}
	}

	private void listarMeusEventos() {
		System.out.println("\n=== Meus Eventos ===");
		List<Evento> meus = eventos.stream().filter(e -> e.getParticipantes().contains(usuario.getEmail())).toList();

		if (meus.isEmpty()) {
			System.out.println("Você não está participando de nenhum evento.");
			return;
		}

		for (int i = 0; i < meus.size(); i++) {
			System.out.println("\nEvento #" + i);
			System.out.println(meus.get(i));
		}
	}

	private void listarOcorrendo() {
		System.out.println("\n=== Eventos em Andamento ===");
		boolean encontrou = false;
		for (Evento e : eventos) {
			if (e.estaOcorrendo()) {
				System.out.println("\n" + e);
				encontrou = true;
			}
		}
		if (!encontrou) {
			System.out.println("Não há eventos em andamento.");
		}
	}

	private void listarPassados() {
		System.out.println("\n=== Eventos Passados ===");
		boolean encontrou = false;
		for (Evento e : eventos) {
			if (e.jaAconteceu()) {
				System.out.println("\n" + e);
				encontrou = true;
			}
		}
		if (!encontrou) {
			System.out.println("Não há eventos passados.");
		}
	}

	public void salvarEventos() {
		ArquivoEventos.salvar(eventos);
	}

	public void carregarEventos() {
		eventos = ArquivoEventos.carregar();
	}

	@FunctionalInterface
	private interface ValidadorEntrada {
		boolean validar(String entrada);
	}
}