package ca.cmpt213.as2;
import java.util.Comparator;
public class TeamComparator implements Comparator<Team>{
    public int compare(Team one, Team two){
        return one.getTeamNum() - two.getTeamNum();
    }
}
