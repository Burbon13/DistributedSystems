/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author Razvan
 */
public class B implements Serializable {
    
    public static final long serialVersionUID = 3;
    
    private String name;
    private HashSet<A> as;

    public B(String name, HashSet<A> as) {
        this.as = as;
        this.name = name;
    }

    public HashSet<A> getAs() {
        return as;
    }

    public void setAs(HashSet<A> as) {
        this.as = as;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
