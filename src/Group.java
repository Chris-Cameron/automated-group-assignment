import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;

public class Group {
    private String groupName;
    private ArrayList<String> memberList;

    public Group(String name){
        groupName = name;
        memberList = new ArrayList<String>();
    }

    public void addMember(String member){
        memberList.add(member);
    }

    public void print(File outputFile) throws IOException {
        FileWriter writer = new FileWriter(outputFile, true);
        writer.write(groupName + "\n");
        for(String m: memberList){
            writer.write(m + "\n");
        }
        writer.write("\n");
        writer.close();
    }
}
