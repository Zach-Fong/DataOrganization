package ca.cmpt213.as2;

public class Tokimon {
    private String name;
    private String id;
    private Compatibility compatibility;
    private boolean hasJson;
    private boolean isFirst;

    public Tokimon(){
        name = "";
        id = "";
        compatibility = new Compatibility();
        hasJson = false;
        isFirst = false;
    }

    public Tokimon(String name, String id, Compatibility compatibility){
        this.name = name;
        this.id = id;
        this.compatibility = compatibility;
        hasJson = false;
        isFirst = false;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }

    public Compatibility getCompatibility(){
        return compatibility;
    }

    public boolean getHasJson(){
        return hasJson;
    }

    public void setHasJson(boolean hasJson){
        this.hasJson = hasJson;
    }

    public boolean getIsFirst(){
        return isFirst;
    }

    public void setIsFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
}
