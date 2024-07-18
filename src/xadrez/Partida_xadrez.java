package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jogo_tabuleiro.Peca;
import jogo_tabuleiro.Posicao;
import jogo_tabuleiro.Tabuleiro;
import jogo_tabuleiro.excecao_Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class Partida_xadrez {

	private Tabuleiro tabuleiro;
	private int turno;
	private Cor jogadorAtual;
	private boolean xeque;
	private boolean xequeMate;
	private Peca_xadrez emPassagemVulneravel;
	private Peca_xadrez promovido;

	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();

	public Partida_xadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.WHITE;
		xeque = false;
		setupInicial();
	}

	public Peca_xadrez getEmPassagemVulneravel() {
		return emPassagemVulneravel;
	}

	public Peca_xadrez getPromovido() {
		return promovido;
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getXeque() {
		return xeque;
	}

	public boolean getXequeMate() {
		return xequeMate;
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.WHITE) ? Cor.BLACK : Cor.WHITE;
	}

	public Peca_xadrez[][] getPecas() {
		Peca_xadrez[][] mat = new Peca_xadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				// downCasting = interpreta como uma Peca_xadrez e não como uma Peca comum
				mat[i][j] = (Peca_xadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possiveisMovimentos(Posicao_xadrez posicaoOrigem) {
		Posicao posicao = posicaoOrigem.toPosicao();
		verificarPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public Peca_xadrez executar_movimento_xadrez(Posicao_xadrez posicaoOrigem, Posicao_xadrez posicaoAlvo) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao alvo = posicaoAlvo.toPosicao();
		verificarPosicaoOrigem(origem);
		verificarPosicaoAlvo(origem, alvo);
		Peca pecaCapturada = fazerMovimento(origem, alvo);
		if (testarXeque(jogadorAtual)) {
			desfazerMovimento(origem, alvo, pecaCapturada);
			throw new Excecao_xadrez("Você não pode se colocar em xeque.");
		}
		Peca_xadrez pecaMovida = (Peca_xadrez) tabuleiro.peca(alvo);

		// movimento especial promoção
		promovido = null;
		if (pecaMovida instanceof Peao) {
			if (pecaMovida.getCor() == Cor.WHITE && alvo.getLinha() == 0
					|| pecaMovida.getCor() == Cor.BLACK && alvo.getLinha() == 7) {
				promovido = (Peca_xadrez) tabuleiro.peca(alvo);
				promovido = substituirPecaPromovida("r");
			}
		}
		xeque = (testarXeque(oponente(jogadorAtual))) ? true : false;
		if ((testarXequeMate(oponente(jogadorAtual)))) {
			xequeMate = true;
		} else {
			proximoTurno();
		}

		// MOVIMENTO ESPECIAL
		if (pecaMovida instanceof Peao && (alvo.getLinha()) == origem.getLinha() - 2
				|| (alvo.getLinha()) == origem.getLinha() + 2) {
			emPassagemVulneravel = pecaMovida;
		} else {
			emPassagemVulneravel = null;
		}
		return (Peca_xadrez) pecaCapturada;
	}

	private Peca_xadrez novaPeca(String type, Cor cor) {
		if (type.equals("B"))
			return new Bispo(tabuleiro, cor);
		if (type.equals("r"))
			return new Rainha(tabuleiro, cor);
		if (type.equals("T"))
			return new Torre(tabuleiro, cor);
		return new Cavalo(tabuleiro, cor);
	}

	public Peca_xadrez substituirPecaPromovida(String type) {
		if (promovido == null) {
			throw new IllegalStateException("Não há peça para ser promovida.");
		}
		if (!type.equals("T") && !type.equals("r") && !type.equals("C") & !type.equals("B")) {
			return promovido;
		}

		Posicao pos = promovido.getPosicao_xadrez().toPosicao();
		Peca p = tabuleiro.removerPeca(pos);
		pecasNoTabuleiro.remove(p);

		Peca_xadrez novaPeca = novaPeca(type, promovido.getCor());
		tabuleiro.posicaoPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);

		return novaPeca;
	}

	private Peca fazerMovimento(Posicao origem, Posicao alvo) {
		Peca_xadrez p = (Peca_xadrez) tabuleiro.removerPeca(origem);
		p.acrescentarNumeroDeMovimento();
		Peca pecaCapturada = tabuleiro.removerPeca(alvo);
		tabuleiro.posicaoPeca(p, alvo);
		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}

		// movimento especial ROQUE
		if (p instanceof Rei && alvo.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao alvoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			Peca_xadrez torre = (Peca_xadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.posicaoPeca(torre, alvoT);
			torre.acrescentarNumeroDeMovimento();
		}
		if (p instanceof Rei && alvo.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao alvoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			Peca_xadrez torre = (Peca_xadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.posicaoPeca(torre, alvoT);
			torre.acrescentarNumeroDeMovimento();
		}

		// movimento EM PASSAGEM
		if (p instanceof Peao) {
			if (origem.getColuna() != alvo.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if (p.getCor() == Cor.WHITE) {
					posicaoPeao = new Posicao(alvo.getLinha() + 1, alvo.getColuna());
				} else {
					posicaoPeao = new Posicao(alvo.getLinha() - 1, alvo.getColuna());
				}
				pecaCapturada = tabuleiro.removerPeca(posicaoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}

		return pecaCapturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao alvo, Peca pecaCapturada) {
		Peca_xadrez p = (Peca_xadrez) tabuleiro.removerPeca(alvo);
		p.decrescerNumeroDeMovimento();
		tabuleiro.posicaoPeca(p, origem);
		if (pecaCapturada != null) {
			tabuleiro.posicaoPeca(pecaCapturada, alvo);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// movimento especial ROQUE
		if (p instanceof Rei && alvo.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao alvoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			Peca_xadrez torre = (Peca_xadrez) tabuleiro.removerPeca(alvoT);
			tabuleiro.posicaoPeca(torre, origemT);
			torre.decrescerNumeroDeMovimento();
		}

		if (p instanceof Rei && alvo.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao alvoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			Peca_xadrez torre = (Peca_xadrez) tabuleiro.removerPeca(alvoT);
			tabuleiro.posicaoPeca(torre, origemT);
			torre.decrescerNumeroDeMovimento();
		}

		// Movimento EM PASSAGEM
		if (p instanceof Peao) {
			if (origem.getColuna() != alvo.getColuna() && pecaCapturada == emPassagemVulneravel) {
				Peca_xadrez peao = (Peca_xadrez) tabuleiro.removerPeca(alvo);
				Posicao posicaoPeao;
				if (p.getCor() == Cor.WHITE) {
					posicaoPeao = new Posicao(3, alvo.getColuna());
				} else {
					posicaoPeao = new Posicao(4, alvo.getColuna());
				}
				tabuleiro.posicaoPeca(peao, posicaoPeao);
			}
		}

	}

	private void verificarPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.existePeca(posicao)) {
			throw new excecao_Tabuleiro("Não existe peça na posição de origem.");
		}
		if (!tabuleiro.peca(posicao).existeAlgumMovimentoPossivel()) {
			throw new excecao_Tabuleiro("Não existe movimentos possíveis para essa peça.");
		}
		if (jogadorAtual != ((Peca_xadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new Excecao_xadrez("A peça escolhida não é sua.");
		}
	}

	private void verificarPosicaoAlvo(Posicao origem, Posicao alvo) {
		if (!tabuleiro.peca(origem).movimentoPossivel(alvo)) {
			throw new excecao_Tabuleiro("A peça escolhida não pode se mexer para a posição de destino.");
		}
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.WHITE) ? Cor.BLACK : Cor.WHITE;
	}

	private Peca_xadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((Peca_xadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (Peca_xadrez) p;
			}
		}
		throw new IllegalStateException("Não existe " + cor + " rei no tabuleiro");
	}

	private boolean testarXeque(Cor cor) {
		// Pegar a posicao do rei em formato de MATRIZ
		Posicao posicaoRei = rei(cor).getPosicao_xadrez().toPosicao();
		List<Peca> pecasInimigas = pecasNoTabuleiro.stream().filter(x -> ((Peca_xadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());

		for (Peca p : pecasInimigas) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testarXequeMate(Cor cor) {
		if (!testarXeque(cor)) {
			return false;
		}
		List<Peca> pecasAliadas = pecasNoTabuleiro.stream().filter(x -> ((Peca_xadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : pecasAliadas) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((Peca_xadrez) p).getPosicao_xadrez().toPosicao();
						Posicao alvo = new Posicao(i, j);
						Peca pecaCapturada = fazerMovimento(origem, alvo);
						boolean testarXeque = testarXeque(cor);
						desfazerMovimento(origem, alvo, pecaCapturada);
						if (!testarXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void lugarNovaPeca(char coluna, int linha, Peca_xadrez peca) {
		tabuleiro.posicaoPeca(peca, new Posicao_xadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void setupInicial() {
		lugarNovaPeca('a', 1, new Torre(tabuleiro, Cor.WHITE));
		lugarNovaPeca('e', 1, new Rei(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('d', 1, new Rainha(tabuleiro, Cor.WHITE));
		lugarNovaPeca('c', 1, new Bispo(tabuleiro, Cor.WHITE));
		lugarNovaPeca('f', 1, new Bispo(tabuleiro, Cor.WHITE));
		lugarNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.WHITE));
		lugarNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.WHITE));
		lugarNovaPeca('h', 1, new Torre(tabuleiro, Cor.WHITE));
		lugarNovaPeca('a', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('b', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('c', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('d', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('e', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('f', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('g', 2, new Peao(tabuleiro, Cor.WHITE, this));
		lugarNovaPeca('h', 2, new Peao(tabuleiro, Cor.WHITE, this));

		lugarNovaPeca('a', 8, new Torre(tabuleiro, Cor.BLACK));
		lugarNovaPeca('e', 8, new Rei(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.BLACK));
		lugarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.BLACK));
		lugarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.BLACK));
		lugarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.BLACK));
		lugarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.BLACK));
		lugarNovaPeca('h', 8, new Torre(tabuleiro, Cor.BLACK));
		lugarNovaPeca('a', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('b', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('c', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('d', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('e', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('f', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('g', 7, new Peao(tabuleiro, Cor.BLACK, this));
		lugarNovaPeca('h', 7, new Peao(tabuleiro, Cor.BLACK, this));
	}
}
