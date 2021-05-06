/*****************************************************************************************
 *
 *   This file is part of JNdiff project.
 *
 *   JNdiff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   JNdiff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with JNdiff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.relation;

import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * @author schirinz NXN: memorizza le informazioni rilevate durante il calcolo
 *         del diff.
 */
public class NxN {

	Logger logger = Logger.getLogger(getClass().getName());

	public int dom;
	public int cod;
	private Vector<Interval> X = new Vector<Interval>();
	private Vector<Interval> Y = new Vector<Interval>();
	private Vector<Vector<Field>> PLANE = new Vector<Vector<Field>>();

	// Vettori per la gestione delle priority list
	private Vector<Field> First = new Vector<Field>();
	private Vector<Field> Last = new Vector<Field>();

	// Reference al Field della priority list attualmente processato
	private Field nowInProcess;

	/**
	 * Costruttore
	 * 
	 * @param dom
	 *            Dominio della Relazione
	 * @param cod
	 *            Codominio delle Relatione
	 */
	public NxN(int dom, int cod) {
		this.dom = dom;
		this.cod = cod;

		// Creo i Field per la gestione delle property list
		createNewPropertyList(Field.NO);
		createNewPropertyList(Field.LOCALITY);

		// Creo gli intervalli iniziali
		Interval tmpX = new Interval(0, dom);
		Interval tmpY = new Interval(0, cod);

		// Creo il primo Field e lo inserisco nella lista corretta
		Field tmpF = new Field(Field.LOCALITY, tmpX, tmpY);
		tmpF.bindFirst(getLast(Field.LOCALITY));

		// Inserisco gli elementi nella struttura
		X.add(tmpX);
		Y.add(tmpY);
		PLANE.add(new Vector<Field>());
		PLANE.get(0).add(tmpF);
	}

	/**
	 * Aggiunge il vettore colonna (col) alla destra della colonna specificata
	 * da index
	 * 
	 * @param index
	 *            indice della colonna di riferimento
	 * @param intX
	 *            intervallo per la colonna aggiunta
	 * @param col
	 *            Vettore che rappresenta la colonna da aggiungere
	 */
	private void addColumn(int index, Interval intX, Vector<Field> col) {
		X.add(index + 1, intX);
		PLANE.add(index + 1, col);
	}

	/**
	 * Aggiunge un nuovo elemento per l'accesso alle liste di Field per la
	 * proprietà property
	 * 
	 * @param property
	 *            proprietà per la quale creare una nuova lista
	 */
	public void createNewPropertyList(int property) {
		Field FirstField = new Field(-1, null, null);
		Field LastField = new Field(-1, null, null);

		FirstField.next = LastField;
		FirstField.prev = null;
		LastField.next = null;
		LastField.prev = FirstField;

		// Creazione di First e Last per la proprietà
		First.add(property, FirstField);
		Last.add(property, LastField);
	}

	/**
	 * Trova l'indice della colonna in cui si è memorizzato l'intervallo I
	 * 
	 * @param I
	 *            intervallo da cercare
	 * @return indice della colonna nel quale si trova l'intervallo
	 */
	private int findColumn(Interval I) {
		for (int i = 0; i < X.size(); i++) {
            if ((I.inf >= X.get(i).inf) && (I.inf <= X.get(i).sup)) {
                return i;
            }
		}
		return -1;
	}

	/**
	 * Trova l'indice della riga in cui sta l'ntervallo I
	 * 
	 * @param I
	 *            intervallo da cercare
	 * @return indice della riga su cui si trova l'intervallo
	 */
	private int findRow(Interval I) {

		for (int i = 0; i < Y.size(); i++) {
			if ((I.inf >= Y.get(i).inf) && (I.inf <= Y.get(i).sup))
				return i;
		}
		return -1;
	}

	/**
	 * Restituisce il Field che si trova nella posizione (x,y)
	 * 
	 * @param x
	 *            indice della colonna
	 * @param y
	 *            indice della riga
	 * @return Field che si trovva nella posizione specificata
	 */
	public Field getField(int x, int y) {
		return PLANE.get(x).get(y);
	}

	/**
	 * Ritorna un riferimento al campo Field simbolico per la lista property
	 * 
	 * @param property
	 *            proprietà di cui si vuole avere il primo campo
	 * @return Field di riferimento First(nn contiene dati reali)
	 */
	private Field getFirst(int property) {
		return First.get(property);
	}

	/**
	 * Restituisce un riferimento al vettore contenete gli intervalli presenti
	 * sul dominio
	 * 
	 * @return Vector con intervalli presenti sul dominio
	 */
	public Vector<Interval> getIntervalsOnX() {
		return X;
	}

	/**
	 * Restituisce un riferimento al vettore contenente gli intervalli presenti
	 * sul codominio
	 * 
	 * @return Vector con intervalli presenti sul codominio
	 */
	public Vector<Interval> getIntervalsOnY() {
		return Y;
	}

	/**
	 * Ritorna il riferimento all'elemnto last per la proprietà property
	 * 
	 * @param property
	 *            proprietà per la quale richiedere l'elemento last
	 * @return riferimento all'elemento last(non contiene dati reali)
	 */
	private Field getLast(int property) {
		return Last.get(property);
	}

	/**
	 * Aggiorna il puntatore interno che mantiene l'ultimo Field processato
	 * Viene usato abbinato con StartFieldProcess, che setta il puntatore
	 * interno alla lista dei Field che si vuole processare
	 * 
	 * @return Riferimento al Field successivo da processare
	 */
	public Field nextField() {
		nowInProcess = nowInProcess.next;
		// L'ultimo elemento della lista è simbolico - Non va processato
		if (nowInProcess.next == null)
			return null;
		return nowInProcess;

	}

	/**
	 * Divide la colonna Ix sull'intervallo intX. Svincola i campi modificati
	 * dalle relative liste L'inserimento di un intervallo sul dominio può
	 * portare 3 casi: 0: La divisione porta all'eliminazione della colonna(intX
	 * si sovrappone completamente all'intervallo presente) 1: La divisione
	 * porta a cambiare l'intervallo della colonna (uno degli estremi di intX
	 * corrispondone con l'intervallo presente) 2: La divisione porta a dividere
	 * la colonna in due parti(intX è strettamente contenuto nell'intervallo
	 * presente)
	 * 
	 * @param Ix
	 *            indice della colonna in cui si trova l'intervallo da splittare
	 * @param intX
	 *            intervallo da inserire
	 * @return Tipo di divisione effettuata
	 */
	private int splitCols(int Ix, Interval intX) {

		int ret = -1;

		logger.info("SplitCols col:" + Ix);

		// Prendo l'intervallo da spittare
		Interval splitX = X.get(Ix);

		logger.info("Spit interval:" + splitX.show());

		// Calcolo il first e follow dello split
		Interval firstX = new Interval(splitX.inf, intX.inf - 1);
		Interval followX = new Interval(intX.sup + 1, splitX.sup);

		logger.info("FirstX:" + firstX.show());
		logger.info("FollowX:" + followX.show());

		// Rimozione completa della colonna
		if ((firstX.size() <= 0) && (followX.size() <= 0)) {
			ret = 0;
			untieColumn(Ix);
			subColumn(Ix);
		}

		// Sostituzione del valore della colonna con Follow
		if ((firstX.size() <= 0) && (followX.size() > 0)) {
			ret = 1;
			/* Cambio gli estremi dell'intervallo */
			X.get(Ix).set(followX);
			/* Svincolo gli elementi della colonna */
			// untieColumn(Ix);
		}

		// Sostituzione del valore della colonna con First
		if ((firstX.size() > 0) && (followX.size() <= 0)) {
			ret = 1;
			/* Cambio gli estremi dell'intervallo */
			X.get(Ix).set(firstX);
			/* Svincolo gli elementi della colonna */
			// untieColumn(Ix);
		}

		// Aggiunta di una colonna
		if ((firstX.size() > 0) && (followX.size() > 0)) {
			ret = 2;

			/* Cambio intervallo di Ix con first */
			X.get(Ix).set(firstX);

			/* Svincolo elementi della colonna */
			// untieColumn(Ix);

			/* Creo una nuova colonna */
			Vector<Field> newCol = new Vector<Field>();
			Field tmp;
			for (int i = 0; i < PLANE.get(Ix).size(); i++) {
				tmp = new Field(getField(Ix, i).property, followX, getField(Ix,
						i).yRef);
				tmp.bindAfter(getField(Ix, i));
				newCol.add(i, tmp);
			}
			addColumn(Ix, followX, newCol);
		}
		return ret;
	}

	/**
	 * Divide la riga Iy sull'intervallo intY. Svincola i campi modificati dalle
	 * relative liste L'inserimento di un intervallo sul codominio può portare
	 * 3 casi: 0: La divisione porta all'eliminazione della riga(intY si
	 * sovrappone completamente all'intervallo presente) 1: La divisione porta a
	 * cambiare l'intervallo della riga (uno degli estremi di intY corrispondone
	 * con l'intervallo presente) 2: La divisione porta a dividere la riga in
	 * due parti(intY è strettamente contenuto nell'intervallo presente)
	 * 
	 * @param Iy
	 *            Indice della riga in cuisi trova l'intervallo da dividere
	 * @param intY
	 *            intervallo da inserire
	 * @return Tipo di divisione effettuata
	 */
	private int splitRows(int Iy, Interval intY) {

		int ret = -1;

		logger.info("SplitRows row:" + Iy);

		/* Vedo lo splitting di X a cosa mi porta */
		Interval splitY = Y.get(Iy);

		logger.info("Split interval:" + splitY.show());

		Interval firstY = new Interval(splitY.inf, intY.inf - 1);
		Interval followY = new Interval(intY.sup + 1, splitY.sup);

		logger.info("FirstY:" + firstY.show());
		logger.info("FollowY:" + followY.show());

		// Rimozione completa della riga
		if ((firstY.size() <= 0) && (followY.size() <= 0)) {
			ret = 0;
			/* Slego Riga */
			untieRow(Iy);
			/* Rimuovo la riga */
			subRow(Iy);
		}

		// Sostituzione del valore della riga con Follow
		if ((firstY.size() <= 0) && (followY.size() > 0)) {
			ret = 1;
			/* Cambio gli estremi dell'intervallo */
			Y.get(Iy).set(followY);
		}

		// Sostituzione del valore della riga con First
		if ((firstY.size() > 0) && (followY.size() <= 0)) {
			ret = 1;
			/* Cambio gli estremi dell'intervallo */
			Y.get(Iy).set(firstY);
		}

		// Aggiunta di una riga
		if ((firstY.size() > 0) && (followY.size() > 0)) {
			ret = 2;

			/* Cambio intervallo di Iy con followY */
			Y.get(Iy).set(firstY);

			/* Creo una nuova riga */
			Y.add(Iy + 1, followY);

			Field tmp;
			for (int i = 0; i < X.size(); i++) {
				tmp = new Field(getField(i, Iy).property, getField(i, Iy).xRef,
						followY);
				tmp.bindAfter(getField(i, Iy));
				PLANE.get(i).add(Iy + 1, tmp);
			}
		}
		return ret;
	}

	/**
	 * Inizializza la struttura dati per iniziare a processare la lista di Field
	 * della propietà property. La lista di Field da processare viene
	 * selezionata attraverso il parametro property
	 * 
	 * @param property
	 *            Specifica il tipo di lista dei Field da processare
	 */
	public void StartFieldProcess(int property) {
		nowInProcess = getFirst(property);
	}

	/**
	 * Elimina l'area individuata da intX e intY. Inoltre setta le proprietà
	 * dei 4 quadranti che si vengono a creare dopo la sottrazione. Mantiene il
	 * puntatore nowInProcess consistente, inserendo un puntatore temporaneo di
	 * riferimento durante la divisione
	 * 
	 * @param intX
	 *            intervallo sul dominio dell'area da rimuovere
	 * @param intY
	 *            intervallo sul codominio dell'area da rimuovere
	 * @param propertyAS
	 *            proprità da settare per il quadrante in alto a sinistra
	 * @param propertyAD
	 *            proprità da settare per il quadrante in alto a destra
	 * @param propertyBS
	 *            proprità da settare per il quadrante in basso a sinistra
	 * @param propertyBD
	 *            proprità da settare per il quadrante in basso a destra
	 */
	private void subArea(Interval intX, Interval intY, int propertyAS,
			int propertyAD, int propertyBS, int propertyBD) {

		int Ix = findColumn(intX);
		int Iy = findRow(intY);

		/* inserisco Field Fittizio per tenere indice */
		Field pointer = new Field(-1, null, null);
		pointer.bindFirst(getField(Ix, Iy));

		int splitC = splitCols(Ix, intX);
		int splitR = splitRows(Iy, intY);

		if ((splitC == 2) && ((splitR == 2))) {
			getField(Ix, Iy).property = propertyBS;
			getField(Ix + 1, Iy + 1).property = propertyAD;
			getField(Ix, Iy + 1).property = propertyAS;
			getField(Ix + 1, Iy).property = propertyBD;

			getField(Ix, Iy).untie();
			getField(Ix + 1, Iy + 1).untie();
			getField(Ix, Iy + 1).untie();
			getField(Ix + 1, Iy).untie();

			getField(Ix, Iy).bindFirst(getLast(propertyBS));
			getField(Ix + 1, Iy + 1).bindFirst(getLast(propertyAD));
			getField(Ix, Iy + 1).bindFirst(getLast(propertyAS));
			getField(Ix + 1, Iy).bindFirst(getLast(propertyBD));

		} else if ((splitC == 2) && (splitR == 1)) {

			// Se la divisione per righe sta sotto
			if (intY.sup < getField(Ix, Iy).yRef.inf) {
				getField(Ix, Iy).property = propertyAS;
				getField(Ix + 1, Iy).property = propertyAD;
			} else {
				getField(Ix, Iy).property = propertyBS;
				getField(Ix + 1, Iy).property = propertyBD;
			}

			getField(Ix, Iy).untie();
			getField(Ix + 1, Iy).untie();

			getField(Ix, Iy).bindFirst(getLast(getField(Ix, Iy).property));
			getField(Ix + 1, Iy).bindFirst(
					getLast(getField(Ix + 1, Iy).property));
		} else if ((splitR == 2) && (splitC == 1)) {

			// Se la divisione per righe sta a sinistra/destra
			if (intX.sup < getField(Ix, Iy).xRef.inf) {
				getField(Ix, Iy).property = propertyBD;
				getField(Ix, Iy + 1).property = propertyAD;
			} else {
				getField(Ix, Iy).property = propertyBS;
				getField(Ix, Iy + 1).property = propertyAS;
			}

			getField(Ix, Iy).untie();
			getField(Ix, Iy + 1).untie();

			getField(Ix, Iy).bindFirst(getLast(getField(Ix, Iy).property));
			getField(Ix, Iy + 1).bindFirst(
					getLast(getField(Ix, Iy + 1).property));
		} else if ((splitR == 1) && (splitC == 1)) {

			if ((intX.sup < getField(Ix, Iy).xRef.inf)
					&& (intY.sup < getField(Ix, Iy).yRef.inf)) {
				getField(Ix, Iy).property = propertyAD;
			} else if ((intX.sup < getField(Ix, Iy).xRef.inf)
					&& (intY.inf > getField(Ix, Iy).yRef.sup)) {
				getField(Ix, Iy).property = propertyBD;
			} else if ((intX.inf > getField(Ix, Iy).xRef.sup)
					&& (intY.inf > getField(Ix, Iy).yRef.sup)) {
				getField(Ix, Iy).property = propertyBS;
			} else if ((intX.inf > getField(Ix, Iy).xRef.sup)
					&& (intY.inf < getField(Ix, Iy).yRef.sup)) {
				getField(Ix, Iy).property = propertyAS;
			}

			getField(Ix, Iy).untie();
			getField(Ix, Iy).bindFirst(getLast(getField(Ix, Iy).property));
		}

		nowInProcess = pointer.prev;
		pointer.untie();
	}

	/**
	 * Elimina il vettore colonna di indice index
	 * 
	 * @param index
	 *            indice della colonna da rimuovere
	 */
	private void subColumn(int index) {
		PLANE.remove(index);
		X.remove(index);
	}

	/**
	 * Elimina l'area individuata da intX e intY. Inoltre setta le proprietà
	 * dei 4 quadranti che si vengono a creare dopo la sottrazione. Mantiene il
	 * puntatore nowInProcess consistente, inserendo un puntatore temporaneo di
	 * riferimento durante la divisione
	 * 
	 * @param intX
	 *            intervallo sul dominio dell'area da rimuovere
	 * @param intY
	 *            intervallo sul codominio dell'area da rimuovere
	 * @param propertyAS
	 *            proprità da settare per il quadrante in alto a sinistra
	 * @param propertyAD
	 *            proprità da settare per il quadrante in alto a destra
	 * @param propertyBS
	 *            proprità da settare per il quadrante in basso a sinistra
	 * @param propertyBD
	 *            proprità da settare per il quadrante in basso a destra
	 */
	public void subField(Interval intX, Interval intY, int propertyAS,
			int propertyAD, int propertyBS, int propertyBD) {
		try {
			// logger.info("-------------------------------------------------------------------------------");
			subArea(intX, intY, propertyAS, propertyAD, propertyBS, propertyBD);
			// logger.info("\n-------------------------------------------------------------------------------");
		} catch (Exception e) {
            logger.error("Space subtraction error:" + intX.show()
               					+ " , " + intY.show(), e);
		}
	}

	/**
	 * Elimina il singolo punto di coordinate [intX,intY], settando le relative
	 * proprietà
	 * 
	 * @param intX
	 *            intero che individua l'ascissa del punto
	 * @param intY
	 *            intero che individua l'ordinata del punto
	 * @param propertyAS
	 *            proprità da settare per il quadrante in alto a sinistra
	 * @param propertyAD
	 *            proprità da settare per il quadrante in alto a destra
	 * @param propertyBS
	 *            proprità da settare per il quadrante in basso a sinistra
	 * @param propertyBD
	 *            proprità da settare per il quadrante in basso a destra
	 */
	public void subPoint(int intX, int intY, int propertyAS, int propertyAD,
			int propertyBS, int propertyBD) {
		try {
			// logger.info("-------------------------------------------------------------------------------");
			subArea(new Interval(intX, intX), new Interval(intY, intY),
					propertyAS, propertyAD, propertyBS, propertyBD);
			// logger.info("\n-------------------------------------------------------------------------------");
		} catch (Exception e) {
            logger.error("Error in point subtraction: [" + intX + ","
               					+ intY + "]", e);
		}
	}

	/**
	 * Elimina la riga indicizzata da index
	 * 
	 * @param index
	 *            indice della riga da rimuovere
	 */
	private void subRow(int index) {
		for (int i = 0; i < X.size(); i++) {
			PLANE.get(i).remove(index);
		}
		Y.remove(index);
	}

	/**
	 * Svincola tutti gli elementi della colonna Ix dalle relative liste
	 * 
	 * @param Ix
	 *            indice della colonna degli elementi da svincolare
	 */
	private void untieColumn(int Ix) {
		for (int j = 0; j < Y.size(); j++)
			getField(Ix, j).untie();
	}

	/**
	 * Svincola tutti gli elementi della riga Iy dalle relative lista
	 * 
	 * @param Iy
	 *            indice della riga degli elementi da svincolare
	 */
	private void untieRow(int Iy) {
		for (int i = 0; i < X.size(); i++) {
			getField(i, Iy).untie();
		}
    }
    /**
     * Not need for now return
     */
//    public Vector<Vector<Field>> getPlane() {
//        return PLANE;
//    }

    /**
     *
     * @param intervalX
     * @param intervalY
     * @return boolean
     */
    public boolean isExistsInterval(Interval intervalX, Interval intervalY) {

        int Ix = findColumn(intervalX);
        int Iy = findRow(intervalY);
        if (Ix == -1 || Iy == -1) {
            return false;
        }
        return getField(Ix, Iy).inList;

    }


}
