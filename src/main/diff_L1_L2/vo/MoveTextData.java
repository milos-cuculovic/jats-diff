/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sasha
 */
public class MoveTextData {

	private static MoveTextData instance = null;

	List<MoveText> moveTextChanges;

	public MoveTextData() {
		moveTextChanges = new ArrayList<>();

	}

	public static MoveTextData getInstance() {
		if (instance == null) {
			instance = new MoveTextData();
		}

		return instance;
	}

	public void add(MoveText moveText) {
		if (!moveTextChanges.contains(moveText)) {
			moveTextChanges.add(moveText);
		}

	}

	public List<MoveText> getAllMoveChanges() {
		return moveTextChanges;
	}

	public int size() {
		return moveTextChanges.size();
	}

	public MoveText get(int key) {
		return moveTextChanges.get(key);
	}
}
