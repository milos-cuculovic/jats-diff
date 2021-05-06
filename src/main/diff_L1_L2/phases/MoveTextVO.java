/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.phases;
import org.w3c.dom.Node;

/**
 *
 * @author sasha
 */
public class MoveTextVO {

    public int position;
    public String text;
    public int size;
    public boolean deleteIndicator;

    @Override
    public String toString() {
        return String.format("Position: " + this.position + " Size: " + this.size + " Text: " + this.text);
    }
}
