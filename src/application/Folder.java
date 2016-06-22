package application;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

class Folder
{

    public String toString()
    {
        JSONObject object = new JSONObject();
        object.put("dir", dir);
        object.put("files", files);
        return object.toJSONString();
    }

    public Folder(String dir)
    {
        this.dir = dir;
        files = new ArrayList();
    }

    public String getDir()
    {
        return dir;
    }

    public void addFile(String file)
    {
        files.add(file);
    }

    public List getFiles()
    {
        return files;
    }

    private String dir;
    private List<String> files;
}
