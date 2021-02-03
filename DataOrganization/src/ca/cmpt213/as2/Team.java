package ca.cmpt213.as2;
import java.util.ArrayList;

public class Team {
    private ArrayList<Tokimon> members = new ArrayList<Tokimon>();
    private String extra_comments;
    private String filename;
    private int teamNum;
    private Tokimon owner;

    public Team(){
        extra_comments = "";
        filename = "";
        teamNum = -1;
        owner = null;
    }
    public Team(String extra_comments, String filename){
        this.extra_comments = extra_comments;
        this.filename = filename;
        teamNum = -1;
        owner = null;
    }
    public void addToki(Tokimon tokimon){
        members.add(tokimon);
    }

    public ArrayList<Tokimon> getMembers(){
        return members;
    }

    public String getExtra_comments(){
        return extra_comments;
    }

    public String getFilename(){
        return filename;
    }

    public int getTeamNum(){
        return teamNum;
    }

    public void setTeamNum(int teamNum){
        this.teamNum = teamNum;
    }

    public Tokimon getOwner(){
        return owner;
    }

    public void setOwner(Tokimon owner){
        this.owner = owner;
    }
}
