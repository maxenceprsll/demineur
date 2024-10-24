public class demineur {
	static class Case {
		boolean decouverte = false;
		boolean drapeau = false;
		boolean estBombe = false;
		int entourage = 0;
	}
	static final int largeur = 15;
	static final int hauteur = 15;
	static final int RATIOBOMBES = hauteur*largeur/4;
	static Case [][] plateau = new Case [hauteur][largeur];
	static boolean jeu = true;
	static boolean premierTour = true;
	static boolean resultat = false;
	static void initialisation() {
		for (int y=0; y<hauteur; y++) {
			for (int x=0; x<largeur; x++) {
				plateau[y][x]= new Case();
			}
		}
	}
	static boolean placementValide(int x, int y, int x0, int y0) {
		boolean valide = true;
		if (plateau[y][x].estBombe) {
			valide = false;
		}
		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				if (x==x0+i && y==y0+j) {
					valide = false;
				}
			}
		}
		return valide;
	}
	static void placementBombe(int x0, int y0) {
		for (int i=0; i<RATIOBOMBES; i++) {
			boolean enCours=true;
			int x;
			int y;
			while(enCours) {
				do {
					x = (int)(Math.random()*(largeur));
					y = (int)(Math.random()*(hauteur));
				} while (!placementValide(x,y,x0,y0));
				plateau[y][x].estBombe=true;
				enCours=false;
			}
		}
	}
	static void nbrBombeAutour() {
		for (int y=0; y<hauteur; y++) {
			for (int x=0; x<largeur; x++) {
				if (!plateau[y][x].estBombe) {
					for (int i=-1; i<2; i++) {
						for (int j=-1; j<2; j++) {
							if (x+i>-1 && x+i<largeur && y+j>-1 && y+j<hauteur) {
								if (plateau[y+j][x+i].estBombe) {
									plateau[y][x].entourage++;
								}
							}
						}
					}
				}
			}
		}
	}
	static void affichagePlateau() {
		for (int y=-1; y<hauteur; y++) {
			for (int x=-1; x<largeur; x++) {
				if (y<0) {
					if (x<0) {
						Ecran.afficher("   ");
					} else if (x<10) {
						Ecran.afficher("\u001B[36m  ",x,"\u001B[0m");
					} else {
						Ecran.afficher("\u001B[36m ",x,"\u001B[0m");
					}
				} else if (x<0 && y<10) {
					Ecran.afficher("\u001B[36m  ",y,"\u001B[0m");
				} else if (x<0) {
					Ecran.afficher("\u001B[36m ",y,"\u001B[0m");	
				} else {
					if (plateau[y][x].drapeau) {
						Ecran.afficher("  ▶");
					} else if (!plateau[y][x].decouverte) {
						Ecran.afficher("  ❏");
					} else {
						if (plateau[y][x].estBombe) {
							Ecran.afficher("  ✵");
						} else {
							switch (plateau[y][x].entourage) {
							case 0:
								Ecran.afficher("   ");
								break;
							case 1:
								Ecran.afficher("\u001B[34m  1\u001B[0m");
								break;
							case 2: 
								Ecran.afficher("\u001B[32m  2\u001B[0m");
								break;
							case 3: 
								Ecran.afficher("\u001B[31m  3\u001B[0m");
								break;
							case 4: 
								Ecran.afficher("\u001B[35m  4\u001B[0m");
								break;
							case 5: 
								Ecran.afficher("\u001B[33m  5\u001B[0m");
								break;
							default: 
								Ecran.afficher("\u001B[37m  ",plateau[y][x].entourage,"\u001B[0m");
								break;
							}
						}
					}
				}
			}
			Ecran.sautDeLigne();
		}
	}
	static void decouverteCase(String s) {
		String [] s_split = s.split(" ");
		int x = Integer.parseInt(s_split[0]);
		int y = Integer.parseInt(s_split[1]);
		if(premierTour) {
			placementBombe(x,y);
			nbrBombeAutour();
			premierTour = false;
		} 
		if (s_split.length>2) {
			if (plateau[y][x].drapeau) {
				plateau[y][x].drapeau=false;
			} else if (!plateau[y][x].decouverte) {
				plateau[y][x].drapeau=true;
			}
		} else {
			if (!plateau[y][x].drapeau) {
				if (plateau[y][x].estBombe) {
					decouverteBombes();
					jeu=false;
				} else {
					if (plateau[y][x].entourage==0) {
						recurCaseZero(x,y);
					}
					plateau[y][x].decouverte=true;
				}
			}
		}
		
	}
	static void decouverteBombes() {
		for (int y=0; y<hauteur; y++) {
			for (int x=0; x<largeur; x++) {
				if (plateau[y][x].estBombe) {
					plateau[y][x].decouverte=true;
				}
			}
		}
	}
	static void recurCaseZero(int x, int y) {
		if (plateau[y][x].entourage==0 && plateau[y][x].decouverte==false) {
			plateau[y][x].decouverte=true;
			for (int i=-1; i<2; i++) {
				for (int j=-1; j<2; j++) {
					if (x+i>-1 && x+i<largeur && y+j>-1 && y+j<hauteur) {
						recurCaseZero(x+i,y+j);
					}
				}
			}
		} else {
			plateau[y][x].decouverte=true;
		}
	}
	static void tour() {
		affichagePlateau();
		Ecran.afficher("... ");
		String entreeClavier = Clavier.saisirString();
		decouverteCase(entreeClavier);
	}
	static void verifGagne() {
		boolean gagne = true;
		for (int y=0; y<hauteur; y++) {
			for (int x=0; x<largeur; x++) {
				if ((!plateau[y][x].estBombe && !plateau[y][x].decouverte) || (plateau[y][x].estBombe && !plateau[y][x].drapeau))  {
					gagne = false;
				}
			}
		}
		if (gagne) {
			jeu=false;
			resultat=true;
		}
	}
	static void fin() {
		if(resultat) {
			Ecran.afficher("Gagné!");
		} else {
			Ecran.afficher("Perdu..");
		}
	}
	public static void main (String [] args) {
		initialisation();
		tour();
		while (jeu) {
			tour();
			verifGagne();
		}
		affichagePlateau();
		fin();
	}
}