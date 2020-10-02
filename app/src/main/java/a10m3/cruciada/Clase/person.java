package a10m3.cruciada.Clase;


public class person {

    private String Name,Type;

    public person()
    {}

    public person(String Name,String Type)
    {
        this.Name=Name;this.Type=Type;
    }

    public String getName(){return Name;}

    public void setName(String Name){
        this.Name=Name;
    }

    public String getType(){return Type;}

    public void setType(String Type){this.Type=Type;}

}
