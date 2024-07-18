package xadrez;

import jogo_tabuleiro.Peca;
import jogo_tabuleiro.Posicao;
import jogo_tabuleiro.Tabuleiro;

public abstract class Peca_xadrez extends Peca {

	// subClasse da classe Peca
	private Cor cor;
	private int contadorDeMovimentos;

	public Peca_xadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Posicao_xadrez getPosicao_xadrez() {
		return Posicao_xadrez.fromPosicao(posicao);
	}

	public Cor getCor() {
		return cor;
	}

	public int getContadorDeMovimentos() {
		return contadorDeMovimentos;
	}

	public void acrescentarNumeroDeMovimento() {
		contadorDeMovimentos++;
	}

	public void decrescerNumeroDeMovimento() {
		contadorDeMovimentos--;
	}

	protected boolean existePecaInimiga(Posicao posicao) {
		Peca_xadrez p = (Peca_xadrez) getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;

	}

}
