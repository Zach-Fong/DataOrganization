package ca.cmpt213.as2;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.*;

public class TokimonProcessor {

    public static void main (String[] args){

        //checks if the paths provided from args are valid
        validPaths(args);

        ArrayList<File> files = new ArrayList<File>();

        //finds all JSON files
        joinFiles(args[0], files);
        if(files.size()==0){
            System.out.println("No JSON files found");
            System.out.println("Path and Filename: " + args[0]);
            System.exit(-1);
        }

        //converts JSON files to teams
        ArrayList<Team> teams = jsontoTeam(files, args[0]);

        //checks all teams are valid
        checkOne(teams);

        //sorts teams to be grouped by team number
        Collections.sort(teams, new TeamComparator());

        //writes to csv
        writeCsv(args[1], teams);
    }

    private static void joinFiles(String mainPath, ArrayList<File> oldFiles){
        //FileFilter for json files
        FileFilter jsonFilter = new FileFilter() {
            public boolean accept(File path) {
                return path.getName().endsWith(".json");
            }
        };

        //contains all files in path
        File fileMain = new File(mainPath);
        File[] fileList = fileMain.listFiles();
        boolean checkedPath = false;

        if(fileList!=null){
            for(File fileTemp: fileList){
                //checkedPath makes sure JSON files are not cloned
                if(fileTemp.isFile() && checkedPath == false){
                    //adds the JSON files found to arraylist
                    File file = new File(mainPath);
                    File[] jsonList = file.listFiles(jsonFilter);
                    for(File temp: jsonList){
                        oldFiles.add(temp);
                    }
                    checkedPath = true;
                }
                //if file found is a folder, recursively searches until JSON file is found
                else if(fileTemp.isDirectory()){
                    joinFiles(fileTemp.getAbsolutePath(), oldFiles);
                }
            }
        }
    }


    private static void validPaths(String args[]){
        //checks that all arguments provided on run are valid
        if(args.length!=2){
            System.out.println("First argument required is path, Second argument required is output directory");
            System.exit(-1);
        }
        File json = new File(args[0]);
        File csv = new File (args[1]);
        if(json.exists() == false || csv.exists() == false){
            if(json.exists()==false){
                System.out.println("Path: " + args[0] + " does not exist");
            }
            if(csv.exists()==false){
                System.out.println("Path: " + args[1] + " does not exist");
            }
            System.exit(-1);
        }
    }

    private static ArrayList<Team> jsontoTeam(ArrayList<File> fileList, String path){
        //converts JSON file to team
        ArrayList<Team> teamArray = new ArrayList<Team>();
        int count = 0;

        for(File file: fileList){
            try {
                //reads the file as a JsonElement
                JsonElement fileElement = JsonParser.parseReader(new FileReader(file));
                //converts the jsonElement into as jsonObject
                JsonObject fileObject = fileElement.getAsJsonObject();
                //reads team array and stores as a JsonArray
                JsonArray jsonArrayTeam = fileObject.get("team").getAsJsonArray();

                String extra_comments = fileObject.get("extra_comments").getAsString();
                teamArray.add(new Team(extra_comments,file.toString()));

                //searches through all JSON files converts to a team
                for(JsonElement tokimonElement: jsonArrayTeam){
                    JsonObject tokimonJsonObject = tokimonElement.getAsJsonObject();
                    try {
                        String name = tokimonJsonObject.get("name").getAsString();
                        String id = tokimonJsonObject.get("id").getAsString();

                        JsonObject compatibilityObject = tokimonJsonObject.get("compatibility").getAsJsonObject();
                        Double score = compatibilityObject.get("score").getAsDouble();
                        if(score<0){
                            System.out.println("Score less than 0");
                            System.out.println("Path and Filename: " + file.toString());
                            System.exit(-1);
                        }
                        String comment = compatibilityObject.get("comment").getAsString();
                        teamArray.get(count).addToki(new Tokimon(name, id, new Compatibility(score, comment)));
                    } catch(Exception e){
                        e.printStackTrace();
                        System.out.println("Path and Filename: " + file.toString());
                        System.exit(-1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Path and Filename: " + file.toString());
                System.exit(-1);
            }
            count++;
        }
        return teamArray;
    }

    private static void checkOne(ArrayList<Team> teamArray){
        //checks to make sure all the JSON file teams are valid
        int teamNum=1;
        int check = 0;
        boolean used = false;

        //sets Tokimon that have a JSON or is the first tokimon on the JSON
        //sorts Tokimon on team alphabetically and sorts teams by team number
        for(Team team: teamArray){
            team.getMembers().get(0).setHasJson(true);
            team.getMembers().get(0).setIsFirst(true);
            team.setOwner(team.getMembers().get(0));
            Collections.sort(team.getMembers(), new TokimonComparator());
        }
        Collections.sort(teamArray, new TeamComparator());

        for(int a=0; a<teamArray.size()-1; a++){
            for(int b=0; b<teamArray.get(a).getMembers().size(); b++){
                for(int c=a+1; c<teamArray.size(); c++){
                    //finds if the Tokimon submitted a JSON file
                    if(teamArray.get(a).getMembers().get(b).getId().trim().equalsIgnoreCase(teamArray.get(c).getMembers().get(0).getId().trim())){
                        teamArray.get(c).getMembers().get(0).setHasJson(true);
                    }
                    for(int d=0; d<teamArray.get(c).getMembers().size(); d++){
                        //If two Tokimon have the same ID, but different names on JSON files then exit
                        if(teamArray.get(a).getMembers().get(b).getId().trim().equalsIgnoreCase(teamArray.get(c).getMembers().get(d).getId().trim())){
                            if(teamArray.get(a).getMembers().get(b).getName().equals(teamArray.get(c).getMembers().get(d).getName())==false){
                                System.out.println("Tokimon property match error between:\n");
                                System.out.println("Name: " + teamArray.get(a).getMembers().get(b).getName() + " ID: " + teamArray.get(a).getMembers().get(b).getId());
                                System.out.println("Path and Filename: " + teamArray.get(c).getFilename());
                                System.out.println("and");
                                System.out.println("Name: " + teamArray.get(c).getMembers().get(d).getName() + " ID: " + teamArray.get(c).getMembers().get(d).getId());
                                System.out.println("Path and Filename: " + teamArray.get(a).getFilename());
                                System.exit(-1);
                            }
                            if(teamArray.get(a).getMembers().get(b).getHasJson()==true || teamArray.get(c).getMembers().get(d).getHasJson()==true){
                                teamArray.get(a).getMembers().get(b).setHasJson(true);
                                teamArray.get(c).getMembers().get(d).setHasJson(true);
                            }
                        }
                        //used to check if team is equal
                        if(teamArray.get(a).getMembers().size() == teamArray.get(c).getMembers().size()){
                            used = true;
                            if(teamArray.get(a).getMembers().get(d).getId().trim().equalsIgnoreCase(teamArray.get(c).getMembers().get(d).getId().trim())){
                                check++;
                            }
                        }
                    }
                    //sets what team they are on
                    if(check== teamArray.get(a).getMembers().size() && used == true ){
                        if(teamArray.get(a).getTeamNum() == -1){
                            teamArray.get(a).setTeamNum(teamNum);
                        }
                        teamArray.get(c).setTeamNum(teamArray.get(a).getTeamNum());
                    }else if(check != teamArray.get(a).getMembers().size() && check!=0){
                        System.out.println("Tokimon contained on different teams");
                        System.out.println("Path and Filename: " + teamArray.get(a).getFilename());
                        System.exit(-1);
                    }
                    if(teamArray.get(a).getMembers().size()==1){
                        if(check == 1){
                            System.out.println("Duplicate of single Tokimon team found");
                            System.out.println("Path and Filename: " + teamArray.get(a).getFilename());
                            System.exit(-1);
                        }
                        teamArray.get(a).setTeamNum(teamNum);
                    }
                    check = 0;
                    used = false;
                }
                teamNum++;
            }
        }
        //base case if a single Tokimon team is last
        if(teamArray.get(teamArray.size()-1).getMembers().size()==1 && teamArray.get(teamArray.size()-1).getTeamNum()==-1){
            teamArray.get(teamArray.size()-1).setTeamNum(99999);
        }
        for(Team team: teamArray){
            for(Tokimon tokimon: team.getMembers()){
                //makes sure all tokimon have JSON
                if(tokimon.getHasJson()==false){
                    System.out.println("Tokimon missing JSON submission");
                    System.out.println("Path and Filename: " + team.getFilename());
                    System.out.println("Name: " + tokimon.getName() + " ID: " + tokimon.getId());
                    System.exit(-1);
                }
//                System.out.println(tokimon.getId());
            }
            //makes sure tokimon only appear on one team
            if(team.getTeamNum()==-1){
                System.out.println("One or more teams are incorrect");
                System.out.println("Path and Filename: " + team.getFilename());
                System.exit(-1);
            }

        }
    }

    public static boolean compareTokimon(Tokimon one, Tokimon two){
        if(one.getName().equals(two.getName())==false || one.getId().trim().equalsIgnoreCase(two.getId().trim())==false){
            return false;
        }
        return true;
    }

    public static ArrayList<Integer> partitionTeam(ArrayList<Team> allTeams){
        //ArrayList of ints remembering what position each new team starts at (teams are already sorted by team)
        int count = 0;
        ArrayList<Integer> all = new ArrayList<Integer>();
        Team prev = allTeams.get(0);
        ArrayList<Team> finalTeams = new ArrayList<Team>();
        finalTeams.add(prev);
        all.add(0);
        for(Team team: allTeams){
            if(prev.getTeamNum() != team.getTeamNum()){
                all.add(count);
            }
            prev = team;
            count++;
        }
        return all;
    }

    public static void writeCsv(String path, ArrayList<Team> allTeams){
        try{
            PrintWriter writer = new PrintWriter(new File(path));
            StringBuilder builder = new StringBuilder();
            outerWriter(builder, allTeams);

            //closing
            writer.write(builder.toString());
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error writing csv");
            System.out.println("Path and Filename: " + path);
            System.exit(-1);
        }
    }

    public static void outerWriter(StringBuilder builder, ArrayList<Team> allTeams) {
        ArrayList<Integer> partition = partitionTeam(allTeams);
        int count = 0;

        for(int c=0; c<partition.size()-1; c++){
            for(int a=partition.get(c); a<partition.get(c+1); a++) {
                if (a == partition.get(c)) {
                    //prints header for every team
                    count++;
                    builder.append("Team#");
                    builder.append(",");
                    builder.append("From Toki");
                    builder.append(",");
                    builder.append("To Toki");
                    builder.append(",,");
                    builder.append("Score");
                    builder.append(",");
                    builder.append("Comment");
                    builder.append(",,");
                    builder.append("Extra");
                    builder.append("\r\n");
                    builder.append("Team " + allTeams.get(a).getTeamNum());
                    builder.append(",,,,,,");
                    builder.append("\r\n");
                }
                //prints all inner data
                Tokimon owner = allTeams.get(a).getOwner();
                for (int b = 0; b < allTeams.get(a).getMembers().size(); b++) {
                    if ((owner.getId().trim().equalsIgnoreCase(allTeams.get(a).getMembers().get(b).getId().trim())) == false) {
                        builder.append(",");
                        builder.append(owner.getId());
                        builder.append(",");
                        builder.append(allTeams.get(a).getMembers().get(b).getId());
                        builder.append(",,");
                        builder.append(allTeams.get(a).getMembers().get(b).getCompatibility().getScore());
                        builder.append(",");
                        String comment = allTeams.get(a).getMembers().get(b).getCompatibility().getComment();
                        builder.append(comment.substring(0, comment.length() - 9));
//                        builder.append("" + comment + "");
                        builder.append(",,");
                        builder.append("\n");
                    }
                }
                //prints data for when the owner Tokimon is being compared with itself
                builder.append(",");
                builder.append(owner.getId());
                builder.append(",");
                builder.append("-");
                builder.append(",,");
                builder.append(allTeams.get(a).getOwner().getCompatibility().getScore());
                builder.append(",");
                String commentMain = allTeams.get(a).getOwner().getCompatibility().getComment();
                builder.append(commentMain.substring(0,commentMain.length()-9));
                builder.append(",,");
                builder.append(allTeams.get(a).getExtra_comments());
                builder.append("\n");
            }
            builder.append("\n");
        }

        //Loop above does not run for last team, this is the code for the last team
        builder.append("Team#");
        builder.append(",");
        builder.append("From Toki");
        builder.append(",");
        builder.append("To Toki");
        builder.append(",,");
        builder.append("Score");
        builder.append(",");
        builder.append("Comment");
        builder.append(",,");
        builder.append("Extra");
        builder.append("\r\n");
        builder.append("Team " + allTeams.get(partition.get(partition.size()-1)).getTeamNum());
        builder.append(",,,,,,");
        builder.append("\r\n");
        for(int a=partition.get(partition.size()-1); a<allTeams.size(); a++) {
            Tokimon owner = allTeams.get(a).getOwner();
            for (int b = 0; b < allTeams.get(a).getMembers().size(); b++) {
                if ((owner.getId().trim().equalsIgnoreCase(allTeams.get(a).getMembers().get(b).getId().trim())) == false) {
                    builder.append(",");
                    builder.append(owner.getId());
                    builder.append(",");
                    builder.append(allTeams.get(a).getMembers().get(b).getId());
                    builder.append(",,");
                    builder.append(allTeams.get(a).getMembers().get(b).getCompatibility().getScore());
                    builder.append(",");
                    String comment = allTeams.get(a).getMembers().get(b).getCompatibility().getComment();
                    builder.append(comment.substring(0, comment.length() - 9));
                    builder.append(",,");
                    builder.append("\n");
                }
            }
            builder.append(",");
            builder.append(owner.getId());
            builder.append(",");
            builder.append("-");
            builder.append(",,");
            builder.append(allTeams.get(a).getOwner().getCompatibility().getScore());
            builder.append(",");
            String commentMain = allTeams.get(a).getOwner().getCompatibility().getComment();
            builder.append(commentMain.substring(0,commentMain.length()-9));
//            builder.append(commentMain);
            builder.append(",,");
            builder.append(allTeams.get(a).getExtra_comments());
            builder.append("\n");
        }

    }

    //end
}

