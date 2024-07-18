package application;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import xadrez.Cor;
import xadrez.Partida_xadrez;
import xadrez.Peca_xadrez;
import xadrez.Posicao_xadrez;

public class UI {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHIte_BACKGROUND = "\u001B[47m";

	public static void limparTela() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	public static void printTabuleiro(Peca_xadrez[][] pecas) {
		for (int i = 0; i < pecas.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < pecas.length; j++) {
				printPeca(pecas[i][j], false);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}

	public static void printPartida(Partida_xadrez partidaXadrez, List<Peca_xadrez> capturadas) {
		printTabuleiro(partidaXadrez.getPecas());
		System.out.println();
		printPecaCapturada(capturadas);
		System.out.println();
		System.out.println("Turno : " + partidaXadrez.getTurno());
		if (!partidaXadrez.getXequeMate()) {
			System.out.println("Esperando o jogador: " + partidaXadrez.getJogadorAtual());
			if (partidaXadrez.getXeque()) {
				System.out.println("Xeque!!!!!!");
			}
		} else {
			System.out.println("XEQUE MATE!");
			System.out.println("Vencedor: " + partidaXadrez.getJogadorAtual());
		}

	}

	public static void printTabuleiro(Peca_xadrez[][] pecas, boolean[][] movimentosPossiveis) {
		for (int i = 0; i < pecas.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < pecas.length; j++) {
				printPeca(pecas[i][j], movimentosPossiveis[i][j]);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}

	private static void printPeca(Peca_xadrez peca, boolean fundoPeca) {
		if (fundoPeca) {
			System.out.print(ANSI_BLUE_BACKGROUND);
		}
		if (peca == null) {
			System.out.print("-" + ANSI_RESET);
		} else {
			if (peca.getCor() == Cor.WHITE) {
				System.out.print(ANSI_WHITE + peca + ANSI_RESET);
			} else {
				System.out.print(ANSI_RED + peca + ANSI_RESET);
			}
		}
		System.out.print(" ");
	}

	private static void printPecaCapturada(List<Peca_xadrez> capturadas) {
		List<Peca_xadrez> white = capturadas.stream().filter(x -> x.getCor() == Cor.WHITE).collect(Collectors.toList());
		List<Peca_xadrez> black = capturadas.stream().filter(x -> x.getCor() == Cor.BLACK).collect(Collectors.toList());

		System.out.println("Peças capturadas: ");
		System.out.println("Brancas: ");
		System.out.println(ANSI_WHITE);
		System.out.println(Arrays.toString(white.toArray()));
		System.out.println(ANSI_RESET);

		System.out.println("Pretas: ");
		System.out.println(ANSI_BLACK);
		System.out.println(Arrays.toString(black.toArray()));
		System.out.println(ANSI_RESET);

	}

	public static Posicao_xadrez ler_posicao_xadrez(Scanner sc) {
		try {
			String s = sc.nextLine();
			char coluna = s.charAt(0);
			int linha = Integer.parseInt(s.substring(1));
			// recorta o String a partir da posicao 1, e converte pra um INTEIRO
			return new Posicao_xadrez(coluna, linha);
		} catch (RuntimeException e) {
			throw new InputMismatchException("Erro lendo posições do xadrez. Valores válidos de h1 a h8.");
		}

	}

}
