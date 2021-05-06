package main.diff_L1_L2.phases.common;

/**
 * @author Mike Usato come wrapper per i valori di ritorno della funzione
 *         explorer
 */
public class Match {
	public Integer weight; // Peso del match trovato
	public Integer length; // Lunghezza dell'intervallo di nodi del match

	/**
	 * Costruttore
	 * 
	 * @param length
	 *            Lunghezza del match trovato
	 * @param weight
	 *            Peso del match trovato
	 */
	public Match(Integer length, Integer weight) {
		this.weight = weight;
		this.length = length;
	}
}