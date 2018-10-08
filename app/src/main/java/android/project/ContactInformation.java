package android.project;

public class ContactInformation {

    private int _identifier;
    private String _name;
    private String _number;
    private String _image;


    public ContactInformation(){

    }

    public ContactInformation(String name, String number, String image){
        this._name=name;
        this._number=number;
        this._image=image;
    }

    public int getIdentifier(){
        return _identifier;
    }
    public void setIdentifier(int id){
        this._identifier=id;
    }
    public String getName(){
        return this._name;
    }
    public void setName(String name){
        this._name=name;

    }
    public String getNumber(){
        return this._number;
    }
    public void setNumber(String number){
        this._number=number;
    }
    public String getImage(){
        return this._image;
    }
    public void setImage(String image){
        this._image=image;
    }
}
