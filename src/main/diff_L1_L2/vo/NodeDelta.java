/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import com.github.difflib.patch.AbstractDelta;
import main.diff_L1_L2.vdom.diffing.Dnode;

/**
 * Temporary class for Deltas and nodes
 *
 * @author sasha
 *
 */
public class NodeDelta {

    public AbstractDelta delta;
    public Dnode a;
    public Dnode b;

    @Override
    public String toString() {
        return "text: " + this.delta;
    }
}
