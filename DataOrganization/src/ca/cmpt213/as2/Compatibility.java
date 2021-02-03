package ca.cmpt213.as2;

public class Compatibility {
    private double score;
    private String comment;

    public Compatibility(){
        score = 0;
        comment = "";
    }
    public Compatibility(double score, String comment){
        this.score = score;
        this.comment = comment;
    }

    public double getScore(){
        return score;
    }

    public String getComment(){
        return comment;
    }
}