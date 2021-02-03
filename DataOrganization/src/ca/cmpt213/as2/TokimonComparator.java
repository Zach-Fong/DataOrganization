package ca.cmpt213.as2;

import java.util.Comparator;

public class TokimonComparator implements Comparator<Tokimon> {
    public int compare(Tokimon one, Tokimon two){
        return one.getId().trim().compareToIgnoreCase(two.getId().trim());
    }
}
