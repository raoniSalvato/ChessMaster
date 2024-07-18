package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.Excecao_xadrez;
import xadrez.Partida_xadrez;
import xadrez.Peca_xadrez;
import xadrez.Posicao_xadrez;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException {

		Scanner sc = new Scanner(System.in);
		Partida_xadrez partida = new Partida_xadrez();
		List<Peca_xadrez> capturadas = new ArrayList<Peca_xadrez>();

		while (!partida.getXequeMate()) {
			try {
				UI.limparTela();
				UI.printPartida(partida, capturadas);
				System.out.println();
				System.out.println("Origem: ");
				Posicao_xadrez origem = UI.ler_posicao_xadrez(sc);

				boolean[][] movimentosPossiveis = partida.possiveisMovimentos(origem);
				UI.printTabuleiro(partida.getPecas(), movimentosPossiveis);

				System.out.println();
				System.out.println("Alvo: ");
				Posicao_xadrez alvo = UI.ler_posicao_xadrez(sc);

				Peca_xadrez pecaCapturada = partida.executar_movimento_xadrez(origem, alvo);
				if (pecaCapturada != null) {
					capturadas.add(pecaCapturada);
				}

				if (partida.getPromovido() != null) {
					System.out.println("Digite a peça da promoção (C/r/B/T)");
					String type = sc.nextLine().toUpperCase();
					while (!type.equals("T") && !type.equals("r") && !type.equals("C") & !type.equals("B")) {
						System.out.println("Valor inválido, digite novamente a peça da promoção (C/r/B/T)");
						type = sc.nextLine().toUpperCase();
					}
					partida.substituirPecaPromovida(type);
				}

			} catch (Excecao_xadrez e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.hasNextLine();
			}
		}
		UI.limparTela();
		UI.printPartida(partida, capturadas);
	}

}
